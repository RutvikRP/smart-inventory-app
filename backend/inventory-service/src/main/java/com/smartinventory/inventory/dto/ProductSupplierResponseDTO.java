package com.smartinventory.inventory.dto;

import com.smartinventory.inventory.entity.Product;
import com.smartinventory.inventory.entity.Supplier;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductSupplierResponseDTO(

        Long id,
        Long productId,
        String productName,
        Long supplierId,
        String supplierName,
        BigDecimal supplierPrice,
        Integer leadTimeDays,
        Boolean preferredSupplier
) {
}
