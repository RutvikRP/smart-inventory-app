package com.smartinventory.inventory.exception;

public class DuplicateSkuException extends RuntimeException{
    public DuplicateSkuException(String message){
        super(message);
    }
}
