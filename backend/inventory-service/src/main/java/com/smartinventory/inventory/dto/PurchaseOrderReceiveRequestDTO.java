package com.smartinventory.inventory.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseOrderReceiveRequestDTO(
        List<PurchaseOrderReceiveLineDTO> lines,
        String receiptNumber  // optional
) {}

