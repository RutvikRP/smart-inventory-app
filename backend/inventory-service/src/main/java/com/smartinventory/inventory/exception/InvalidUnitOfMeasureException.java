package com.smartinventory.inventory.exception;

public class InvalidUnitOfMeasureException extends RuntimeException{
    public InvalidUnitOfMeasureException(String message){
        super(message);
    }
}
