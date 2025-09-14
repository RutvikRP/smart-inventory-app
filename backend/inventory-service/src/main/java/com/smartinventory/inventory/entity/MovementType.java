package com.smartinventory.inventory.entity;

public enum MovementType {
    IN,         // incoming stock (PO receive)
    OUT,        // outgoing stock (sales, shipments)
    ADJUSTMENT, // manual admin adjustments
    RESERVE,    // reserved for an order
    RELEASE     // release reservation
}
