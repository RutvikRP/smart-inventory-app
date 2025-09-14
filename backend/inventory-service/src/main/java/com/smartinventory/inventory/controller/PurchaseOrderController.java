package com.smartinventory.inventory.controller;

import com.smartinventory.inventory.dto.PurchaseOrderRequestDTO;
import com.smartinventory.inventory.dto.PurchaseOrderResponseDTO;
import com.smartinventory.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // Create new PO
    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequestDTO requestDTO) {
        PurchaseOrderResponseDTO response = purchaseOrderService.createPurchaseOrder(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get PO by id
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrder(id));
    }

    // Get all POs
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAllPurchaseOrders(Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders(pageable));
    }

    // Confirm PO
    @PutMapping("/{id}/confirm")
    public ResponseEntity<PurchaseOrderResponseDTO> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.confirmOrder(id));
    }

    // Cancel PO
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
