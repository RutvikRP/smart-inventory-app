package com.smartinventory.inventory.exception;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(String message) {
        super(message);
    }
}

