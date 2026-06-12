package com.taxstreem.services;

import com.taxstreem.client.IApiClient;
import com.taxstreem.dto.VAT.BatchFilingResponseDto;
import com.taxstreem.dto.VAT.FilingResponseDto;
import com.taxstreem.dto.VAT.VatFilingBatchPayloadDto;
import com.taxstreem.dto.VAT.VatFilingItemDto;
import com.taxstreem.dto.VAT.VatFilingPayloadDto;
import com.taxstreem.dto.WHT.WhtFilingBatchPayloadDto;
import com.taxstreem.dto.WHT.WhtFilingItemDto;
import com.taxstreem.dto.WHT.WhtFilingPayloadDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FluxTest {

    @Mock
    private IApiClient mockApiClient;

    private Flux flux;

    private static final String SINGLE_RESPONSE_JSON =
            "{\"status\":\"success\",\"reference_id\":\"REF-001\",\"message\":\"Filed successfully\",\"data\":null}";

    private static final String BATCH_RESPONSE_JSON =
            "{\"status\":\"success\",\"batchId\":\"BATCH-001\",\"message\":\"Batch filed successfully\",\"data\":null}";

    @BeforeEach
    void setUp() {
        flux = new Flux(mockApiClient);
    }

    // --- VAT ---

    @Test
    void fileVatSingle_returnsDeserializedResponse() throws Exception {
        when(mockApiClient.request(eq("POST"), eq("/flux/vat-filing/single"), any()))
                .thenReturn(SINGLE_RESPONSE_JSON);

        VatFilingItemDto item = new VatFilingItemDto(1, 5000, "Consulting", "Q1 services", "TIN-123", "Acme Corp");
        VatFilingPayloadDto payload = new VatFilingPayloadDto("enc-payload", 6, 2026, new VatFilingItemDto[]{item});

        FilingResponseDto response = flux.fileVatSingle(payload);

        assertEquals("success", response.status());
        assertEquals("REF-001", response.reference_id());
        assertEquals("Filed successfully", response.message());
    }

    @Test
    void fileVatBatch_returnsDeserializedResponse() throws Exception {
        when(mockApiClient.request(eq("POST"), eq("/flux/vat-filing/batch"), any()))
                .thenReturn(BATCH_RESPONSE_JSON);

        VatFilingItemDto item = new VatFilingItemDto(1, 5000, "Consulting", "Q1 services", "TIN-123", "Acme Corp");
        VatFilingPayloadDto singlePayload = new VatFilingPayloadDto("enc-payload", 6, 2026, new VatFilingItemDto[]{item});
        VatFilingBatchPayloadDto batchPayload = new VatFilingBatchPayloadDto("BATCH-001", new VatFilingPayloadDto[]{singlePayload});

        BatchFilingResponseDto response = flux.fileVatBatch(batchPayload);

        assertEquals("success", response.status());
        assertEquals("BATCH-001", response.batchId());
        assertEquals("Batch filed successfully", response.message());
    }

    // --- WHT ---

    @Test
    void fileWhtSingle_returnsDeserializedResponse() throws Exception {
        when(mockApiClient.request(eq("POST"), eq("/flux/wht-filing/single"), any()))
                .thenReturn(SINGLE_RESPONSE_JSON);

        WhtFilingItemDto item = new WhtFilingItemDto("Acme Corp", "TIN-456", "2026-06-01", "Consulting fee", "10000", "10", "1000", "SCH-1");
        WhtFilingPayloadDto payload = new WhtFilingPayloadDto("enc-payload", 6, 2026, new WhtFilingItemDto[]{item});

        FilingResponseDto response = flux.fileWhtSingle(payload);

        assertEquals("success", response.status());
        assertEquals("REF-001", response.reference_id());
    }

    @Test
    void fileWhtBatch_returnsDeserializedResponse() throws Exception {
        when(mockApiClient.request(eq("POST"), eq("/flux/wht-filing/batch"), any()))
                .thenReturn(BATCH_RESPONSE_JSON);

        WhtFilingItemDto item = new WhtFilingItemDto("Acme Corp", "TIN-456", "2026-06-01", "Consulting fee", "10000", "10", "1000", "SCH-1");
        WhtFilingPayloadDto singlePayload = new WhtFilingPayloadDto("enc-payload", 6, 2026, new WhtFilingItemDto[]{item});
        WhtFilingBatchPayloadDto batchPayload = new WhtFilingBatchPayloadDto("BATCH-001", new WhtFilingPayloadDto[]{singlePayload});

        BatchFilingResponseDto response = flux.fileWhtBatch(batchPayload);

        assertEquals("success", response.status());
        assertEquals("BATCH-001", response.batchId());
    }

    // --- Error propagation ---

    @Test
    void fileVatSingle_propagatesExceptionFromApiClient() throws Exception {
        when(mockApiClient.request(any(), any(), any())).thenThrow(new Exception("Network error"));

        VatFilingPayloadDto payload = new VatFilingPayloadDto("enc", 6, 2026, new VatFilingItemDto[]{});

        assertThrows(Exception.class, () -> flux.fileVatSingle(payload));
    }
}
