package com.taxstreem.dto.VAT;
//import com.taxstreem.dto.VatFilingPayloadDto;

public record VatFilingItemDto(
        Number vatStatus,
        Number amount,
        String item,
        String narration,
        String taxId,
        String beneficiary
) {}