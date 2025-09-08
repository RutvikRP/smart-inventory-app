package com.smartinventory.inventory.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequestDTO(

        @NotBlank(message = "Supplier name is required")
        @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
        String name,
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(min = 7, max = 15, message = "Phone number must be between 7 and 15 digits")
        String phone,
//    private PurchaseOrder purchaseOrder;

        @NotBlank(message = "Address is required")
        String address
) {};
