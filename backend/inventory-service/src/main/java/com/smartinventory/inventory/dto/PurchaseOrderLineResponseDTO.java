package com.smartinventory.inventory.dto;

import java.math.BigDecimal;

public record PurchaseOrderLineResponseDTO(
        long id,
        long productId,
        BigDecimal quantity,         // ordered qty
        BigDecimal price,            // unit price
        BigDecimal receivedQuantity, // how much received so far
        String lineStatus            // PENDING / PARTIALLY_RECEIVED / RECEIVED
) { }

