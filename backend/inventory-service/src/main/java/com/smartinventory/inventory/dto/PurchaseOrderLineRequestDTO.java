package com.smartinventory.inventory.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record PurchaseOrderLineRequestDTO(
        long productId,
        @Min(value = 1, message = "Quantity must be at least 1")
        BigDecimal quantity
) {
}
