package com.smartinventory.inventory.dto;

import com.smartinventory.inventory.entity.PurchaseOrderLine;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public record PurchaseOrderRequestDTO(
        @NotNull(message = "Supplier ID is required")
        long supplierId,
        @Valid
        List<PurchaseOrderLineRequestDTO> productLines
) { }
