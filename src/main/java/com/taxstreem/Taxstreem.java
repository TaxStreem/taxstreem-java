package com.taxstreem;

import com.taxstreem.client.APIClient;
import com.taxstreem.dto.TaxStreemConfig;
import com.taxstreem.services.Encryption;
import com.taxstreem.services.Flux;


/**
 *
 */
public class Taxstreem
{
    private final Flux flux;
    private final Encryption encryption;

    public Flux getFlux() {
        return flux;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public Taxstreem(TaxStreemConfig config) {
        this.encryption = new Encryption();
        APIClient apiClient = new APIClient(config.apiKey(), config.sharedSecret(), config.debug());
        this.flux = new Flux(apiClient);
    }

    public static void main (String[] args) {
        System.out.println("Starting Taxstreem");
    }
}
