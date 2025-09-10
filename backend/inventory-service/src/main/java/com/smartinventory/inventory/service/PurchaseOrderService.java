package com.smartinventory.inventory.service;

import com.smartinventory.inventory.dto.*;
import com.smartinventory.inventory.entity.*;
import com.smartinventory.inventory.exception.*;
import com.smartinventory.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ProductSupplierRepository productSupplierRepository;

    @Transactional
    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO dto) {
        // 1. Validate supplier
        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id: " + dto.supplierId()));

        // 2. Create PurchaseOrder
        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.DRAFT)
                .build();

        // 3. Validate each product with supplier
        List<PurchaseOrderLine> lines = dto.productLines().stream().map(lineDTO -> {
            Product product = productRepository.findById(lineDTO.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + lineDTO.productId()));

            // Check supplier supplies this product
            boolean supplied = productSupplierRepository
                    .findByProductIdAndSupplierId(product.getId(), supplier.getId())
                    .isPresent();

            if (!supplied) {
                throw new InvalidSupplierProductRelationException(
                        "Supplier " + supplier.getName() + " does not supply product " + product.getName()
                );
            }

            // Get price from ProductSupplier relation
            ProductSupplier productSupplier = (ProductSupplier) productSupplierRepository
                    .findByProductIdAndSupplierId(product.getId(), supplier.getId())
                    .orElseThrow(() -> new InvalidSupplierProductRelationException("Invalid relation"));

            return PurchaseOrderLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantity(lineDTO.getQuantity())
                    .unitPrice(productSupplier.getSupplierPrice()) // Use supplier-specific price
                    .build();
        }).collect(Collectors.toList());

        po.setPurchaseOrderLineList(lines);

        // 4. Save PO
        PurchaseOrder saved = purchaseOrderRepository.save(po);

        // 5. Map to ResponseDTO
        return mapToResponseDTO(saved);
    }

    public PurchaseOrderResponseDTO getPurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase Order not found: " + id));
        return mapToResponseDTO(po);
    }

    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseOrderResponseDTO confirmOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("PO not found: " + id));
        po.setOrderStatus(OrderStatus.RECEIVED);
        return mapToResponseDTO(purchaseOrderRepository.save(po));
    }

    @Transactional
    public void cancelOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException("PO not found: " + id));
        po.setOrderStatus(OrderStatus.CANCELLED);
        purchaseOrderRepository.save(po);
    }

    // ✅ Mapper
    private PurchaseOrderResponseDTO mapToResponseDTO(PurchaseOrder po) {
        List<PurchaseOrderLineResponseDTO> lineDTOs = po.getPurchaseOrderLineList().stream()
                .map(line -> new PurchaseOrderLineResponseDTO(
                        line.getId(),
                        line.getProduct().getId(),
                        line.getQuantity(),
                        line.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new PurchaseOrderResponseDTO(
                po.getId(),
                po.getSupplier().getId(),
                po.getOrderDate(),
                po.getOrderStatus().name(),
                lineDTOs
        );
    }
}
