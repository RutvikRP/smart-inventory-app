package com.smartinventory.inventory.exception;

public class DuplicateLineItemException extends RuntimeException {
    public DuplicateLineItemException(String message) {
        super(message);
    }
}
