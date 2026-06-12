package com.taxstreem.dto.VAT;

public record FilingResponseDto(
        String status,
        String reference_id,
        String message,
        Object data
) {
    public FilingResponseDto(String status, String reference_id, String message) {
        this(status, reference_id, message, null);
    }
}
