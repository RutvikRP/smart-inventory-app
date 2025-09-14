package com.smartinventory.inventory.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PurchaseOrderReceiveLineDTO(
        @NotNull
        Long lineId,
        @NotNull
        BigDecimal receivedQuantity
) {
}
