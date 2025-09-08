package com.smartinventory.inventory.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierResponseDTO(
        long id,
        String name,
        String email,
        String phone,
//    private PurchaseOrder purchaseOrder;
        String address
) {};
