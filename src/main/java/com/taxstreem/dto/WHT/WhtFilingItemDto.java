package com.taxstreem.dto.WHT;

public record WhtFilingItemDto(
        String beneficiaryName,
        String beneficiaryTin,
        String transactionDate,
        String transactionDesc,
        String transactionAmount,
        String rate,
        String taxAmount,
        String scheduleReference
) {
}
