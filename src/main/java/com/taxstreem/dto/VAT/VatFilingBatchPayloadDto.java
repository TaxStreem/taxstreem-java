package com.taxstreem.dto.VAT;

public record VatFilingBatchPayloadDto(
        String batchId,
        VatFilingPayloadDto[] payload
) {
}
