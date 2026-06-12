package com.taxstreem.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxstreem.client.IApiClient;
import com.taxstreem.dto.VAT.BatchFilingResponseDto;
import com.taxstreem.dto.VAT.FilingResponseDto;
import com.taxstreem.dto.VAT.VatFilingBatchPayloadDto;
import com.taxstreem.dto.VAT.VatFilingPayloadDto;
import com.taxstreem.dto.WHT.WhtFilingBatchPayloadDto;
import com.taxstreem.dto.WHT.WhtFilingPayloadDto;

public class Flux {
    private final IApiClient apiClient;

    public Flux(IApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Submit a single VAT filing
     *
     * @param vatFilingPayloadDto vat filing information
     * @return Information about the filing action
     * @throws Exception if an error occurs
     */
    public FilingResponseDto fileVatSingle(VatFilingPayloadDto vatFilingPayloadDto) throws Exception {
        String jsonResponse = this.apiClient.request("POST","/flux/vat-filing/single", vatFilingPayloadDto );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, FilingResponseDto.class);
    }

    /**
     * Submit a batch of VAT filings
     *
     * @param vatFilingBatchPayloadDto batch vat filing information
     * @return Information about the filing action
     * @throws Exception if an error occurs
     */
    public BatchFilingResponseDto fileVatBatch(VatFilingBatchPayloadDto vatFilingBatchPayloadDto) throws Exception {
        String jsonResponse = apiClient.request("POST", "/flux/vat-filing/batch", vatFilingBatchPayloadDto);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, BatchFilingResponseDto.class);
    }

    /**
     * Submit a single VHT filing
     *
     * @param whtFilingPayloadDto wht filing information
     * @return Information about the filing action
     * @throws Exception if an error occurs
     */
    public FilingResponseDto fileWhtSingle(WhtFilingPayloadDto whtFilingPayloadDto) throws Exception {
        String jsonResponse = this.apiClient.request("POST","/flux/wht-filing/single", whtFilingPayloadDto );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, FilingResponseDto.class);
    }

    /**
     * Submit a batch of WHT filings
     *
     * @param whtFilingBatchPayloadDto batch wht filing information
     * @return Information about the filing action
     * @throws Exception if an error occurs
     */
    public BatchFilingResponseDto fileWhtBatch(WhtFilingBatchPayloadDto whtFilingBatchPayloadDto) throws Exception {
        String jsonResponse = apiClient.request("POST", "/flux/wht-filing/batch", whtFilingBatchPayloadDto);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, BatchFilingResponseDto.class);
    }
}
