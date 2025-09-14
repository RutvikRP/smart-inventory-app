package com.smartinventory.inventory.service;

import com.smartinventory.inventory.dto.*;
import com.smartinventory.inventory.entity.*;
import com.smartinventory.inventory.exception.*;
import com.smartinventory.inventory.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ProductSupplierRepository productSupplierRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO dto) {
        // Validate supplier
        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id: " + dto.supplierId()));
        Long nextOrderNumber = ((Number) entityManager.createNativeQuery("SELECT nextval('order_number_seq')").getSingleResult()).longValue();
        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.DRAFT)
                .orderNumber("ORDER-"+String.format("%05d",nextOrderNumber))
                .build();
        List<PurchaseOrderLine> lines = dto.productLines().stream().map(lineDTO -> {
            Product product = productRepository.findById(lineDTO.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + lineDTO.productId()));

            boolean supplied = productSupplierRepository
                    .findByProductIdAndSupplierId(product.getId(), supplier.getId())
                    .isPresent();

            if (!supplied) {
                throw new InvalidSupplierProductRelationException(
                        "Supplier " + supplier.getName() + " does not supply product " + product.getName()
                );
            }

            ProductSupplier ps = (ProductSupplier) productSupplierRepository
                    .findByProductIdAndSupplierId(product.getId(), supplier.getId())
                    .orElseThrow(() -> new InvalidSupplierProductRelationException("Invalid relation"));

            BigDecimal qty = lineDTO.quantity();
            BigDecimal unitPrice = ps.getSupplierPrice();

            return PurchaseOrderLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .subtotal(unitPrice.multiply(qty))
                    .receivedQuantity(BigDecimal.ZERO)
                    .lineStatus(LineStatus.PENDING)
                    .build();
        }).collect(Collectors.toList());

        Set<Long> uniqueProducts = lines.stream()
                .map(line -> line.getProduct().getId())
                .collect(Collectors.toSet());
        if (uniqueProducts.size() != lines.size()) {
            throw new DuplicateLineItemException("Duplicate product lines are not allowed in a Purchase Order");
        }
        po.setPurchaseOrderLineList(lines);

        // Calculate total amount
        BigDecimal totalAmount = lines.stream()
                .map(l -> l.getSubtotal() == null ? BigDecimal.ZERO : l.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        po.setTotalAmount(totalAmount);

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToResponseDTO(saved);
    }

    public PurchaseOrderResponseDTO getPurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase Order not found: " + id));
        return mapToResponseDTO(po);
    }

    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseOrderResponseDTO confirmOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("PO not found: " + id));
        if (po.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot confirm a cancelled order");
        }
        if (po.getOrderStatus() == OrderStatus.RECEIVED) {
            throw new InvalidOrderStateException("Order already received");
        }
        po.setOrderStatus(OrderStatus.CONFIRMED);
        return mapToResponseDTO(purchaseOrderRepository.save(po));
    }

    @Transactional
    public void cancelOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("PO not found: " + id));
        if (po.getOrderStatus() == OrderStatus.RECEIVED) {
            throw new InvalidOrderStateException("Cannot cancel a received order");
        }
        po.setOrderStatus(OrderStatus.CANCELLED);
        purchaseOrderRepository.save(po);
    }

    /**
     * Receive purchase order lines (partial or full). Idempotent: only applies remaining qty.
     */
    @Transactional
    public PurchaseOrderResponseDTO receivePurchaseOrder(Long poId, PurchaseOrderReceiveRequestDTO request) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("PO not found: " + poId));

        if (po.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot receive a cancelled PO");
        }

        // Map existing lines
        Map<Long, PurchaseOrderLine> existingLines = po.getPurchaseOrderLineList()
                .stream().collect(Collectors.toMap(PurchaseOrderLine::getId, l -> l));

        List<StockMovement> movementsToSave = new ArrayList<>();

        for (PurchaseOrderReceiveLineDTO lineDto : request.lines()) {
            PurchaseOrderLine line = existingLines.get(lineDto.lineId());
            if (line == null) {
                throw new PurchaseOrderNotFoundException("Line " + lineDto.lineId() + " not part of PO " + poId);
            }

            BigDecimal alreadyReceived = line.getReceivedQuantity() == null ? BigDecimal.ZERO : line.getReceivedQuantity();
            BigDecimal ordered = line.getQuantity() == null ? BigDecimal.ZERO : line.getQuantity();
            BigDecimal wantToReceive = lineDto.receivedQuantity() == null ? BigDecimal.ZERO : lineDto.receivedQuantity();

            BigDecimal remaining = ordered.subtract(alreadyReceived);
            BigDecimal toApply = wantToReceive.min(remaining);
            if (toApply.compareTo(BigDecimal.ZERO) <= 0) {
                // nothing to apply for this line (either zero or already fully received)
                continue;
            }

            // Update product quantity (optimistic locking protects concurrent updates)
            Product product = line.getProduct();
            BigDecimal currentQty = product.getQuantity() == null ? BigDecimal.ZERO : product.getQuantity();
            product.setQuantity(currentQty.add(toApply));
            productRepository.save(product); // may throw OptimisticLockingFailureException on conflict

            // Create stock movement
            StockMovement movement = StockMovement.builder()
                    .product(product)
                    .movementType(MovementType.IN)
                    .quantity(toApply)
                    .referenceType("PO")
                    .referenceId(po.getId())
                    .note("Received for PO line " + line.getId() + (request.receiptNumber() != null ? " (receipt " + request.receiptNumber() + ")" : ""))
                    .build();
            movementsToSave.add(movement);

            // Update line's received quantity and status
            BigDecimal newReceived = alreadyReceived.add(toApply);
            line.setReceivedQuantity(newReceived);
            if (newReceived.compareTo(ordered) >= 0) {
                line.setLineStatus(LineStatus.RECEIVED);
            } else {
                line.setLineStatus(LineStatus.PARTIALLY_RECEIVED);
            }
            // subtotal and PO totals remain unchanged for receive operation
        }

        if (!movementsToSave.isEmpty()) {
            stockMovementRepository.saveAll(movementsToSave);
        }

        // Update PO status based on line statuses
        boolean allReceived = po.getPurchaseOrderLineList().stream()
                .allMatch(l -> l.getLineStatus() == LineStatus.RECEIVED);
        boolean anyReceived = po.getPurchaseOrderLineList().stream()
                .anyMatch(l -> l.getReceivedQuantity() != null && l.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0);

        if (allReceived) {
            po.setOrderStatus(OrderStatus.RECEIVED);
        } else if (anyReceived) {
            po.setOrderStatus(OrderStatus.PARTIALLY_RECEIVED);
        }

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToResponseDTO(saved);
    }

    // Mapper helper (keeps previous response shape)
    private PurchaseOrderResponseDTO mapToResponseDTO(PurchaseOrder po) {
        List<PurchaseOrderLineResponseDTO> lineDTOs = po.getPurchaseOrderLineList().stream()
                .map(line -> new PurchaseOrderLineResponseDTO(
                        line.getId(),
                        line.getProduct().getId(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getReceivedQuantity(),
                        line.getLineStatus().name()
                ))
                .collect(Collectors.toList());

        return new PurchaseOrderResponseDTO(
                po.getId(),
                po.getOrderNumber(),
                po.getSupplier().getId(),
                po.getOrderDate(),
                po.getOrderStatus().name(),
                lineDTOs
        );
    }
}
