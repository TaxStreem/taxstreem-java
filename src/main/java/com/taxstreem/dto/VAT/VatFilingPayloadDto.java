package com.taxstreem.dto.VAT;

public record VatFilingPayloadDto(
        String encryptedPayload,
        Number month,
        Number year,
        VatFilingItemDto[] data
){}