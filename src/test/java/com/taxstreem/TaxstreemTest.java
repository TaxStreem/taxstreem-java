package com.taxstreem;

import com.taxstreem.dto.TaxStreemConfig;
import com.taxstreem.services.Encryption;
import com.taxstreem.services.Flux;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaxstreemTest {

    @Test
    void constructor_initializesFluxAndEncryption() {
        TaxStreemConfig config = new TaxStreemConfig("test-api-key", "test-secret");
        Taxstreem sdk = new Taxstreem(config);

        assertNotNull(sdk.getFlux(), "Flux service must be initialized");
        assertNotNull(sdk.getEncryption(), "Encryption service must be initialized");
    }

    @Test
    void constructor_withDebugFlag_initializesSuccessfully() {
        TaxStreemConfig config = new TaxStreemConfig("test-api-key", "test-secret", true);
        Taxstreem sdk = new Taxstreem(config);

        assertNotNull(sdk.getFlux());
        assertNotNull(sdk.getEncryption());
    }

    @Test
    void getFlux_returnsFluxInstance() {
        TaxStreemConfig config = new TaxStreemConfig("key", "secret");
        Taxstreem sdk = new Taxstreem(config);

        assertNotNull(sdk.getFlux() instanceof Flux ? sdk.getFlux() : null);
    }

    @Test
    void getEncryption_returnsEncryptionInstance() {
        TaxStreemConfig config = new TaxStreemConfig("key", "secret");
        Taxstreem sdk = new Taxstreem(config);

        assertNotNull(sdk.getEncryption() instanceof Encryption ? sdk.getEncryption() : null);
    }
}
