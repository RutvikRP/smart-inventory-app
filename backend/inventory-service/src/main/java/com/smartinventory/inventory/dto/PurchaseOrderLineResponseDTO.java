package com.smartinventory.inventory.dto;

import java.math.BigDecimal;

public record PurchaseOrderLineResponseDTO(
        long id,
        long productId,
        BigDecimal quantity,
        BigDecimal price

) {
}
