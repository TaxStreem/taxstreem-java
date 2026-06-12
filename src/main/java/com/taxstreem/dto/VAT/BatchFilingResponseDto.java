package com.taxstreem.dto.VAT;

public record BatchFilingResponseDto(
        String status,
        String batchId,
        String message,
        Object data
) {
}
