package com.taxstreem.dto.WHT;

public record WhtFilingPayloadDto(
        String encryptedPayload,
        Number month,
        Number year,
        WhtFilingItemDto[] data
) {
}
