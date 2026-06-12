package com.taxstreem.dto.WHT;

public record WhtFilingBatchPayloadDto(
        String batchId,
        WhtFilingPayloadDto[] data
) {
}
