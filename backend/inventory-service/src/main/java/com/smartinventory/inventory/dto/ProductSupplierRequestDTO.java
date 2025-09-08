package com.smartinventory.inventory.dto;

import com.smartinventory.inventory.entity.Product;
import com.smartinventory.inventory.entity.Supplier;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductSupplierRequestDTO(
        @NotNull(message = "Product ID is required")
        long productId,
        @NotNull(message = "Supplier ID is required")
        long supplierId,
        @DecimalMin(value = "0.00", message = "Price must be >= 0")
        BigDecimal supplierPrice,
        @Min(value = 0, message = "Lead time must be >= 0")
        Integer leadTimeDays,
        Boolean preferredSupplier) {
}
