package com.smartinventory.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponseDTO(
        Long id,
        String orderNumber,
        Long supplierId,
        LocalDateTime orderDate,
        String orderStatus,
        List<PurchaseOrderLineResponseDTO> lines
) { }

