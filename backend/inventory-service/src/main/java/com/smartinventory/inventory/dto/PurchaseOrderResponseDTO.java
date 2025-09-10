package com.smartinventory.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponseDTO(
        Long id,
        Long supplierId,
        LocalDateTime orderDate,
        String status,
        List<PurchaseOrderLineResponseDTO> lines
) {
}
