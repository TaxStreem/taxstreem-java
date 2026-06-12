package com.taxstreem;

import com.taxstreem.services.Encryption;
import com.taxstreem.services.Flux;


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

    public Taxstreem(Flux flux, Encryption encryption) {
        this.flux = flux;
        this.encryption = encryption;
    }
}
