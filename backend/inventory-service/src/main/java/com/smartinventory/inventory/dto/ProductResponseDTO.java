package com.smartinventory.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal quantity,
        BigDecimal price,
        String sku,
        String description,
        String uom,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
