package com.smartinventory.inventory.dto;

public record AuthResponseDTO(String accessToken, String refreshToken, String tokenType, Long expiresIn) {
}
