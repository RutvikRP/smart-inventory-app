package com.smartinventory.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.00", message = "Quantity must be >= 0")
        BigDecimal quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must be >= 0")
        BigDecimal price,

        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "Unit of Measure is required")
        String uom,   // Example: "kg", "pcs", "liters"

        String description // optional
) {}
