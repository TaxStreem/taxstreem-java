package com.taxstreem.dto;

public record TaxStreemConfig(
        String apiKey,
        String sharedSecret,
        Boolean debug
) {
    public  TaxStreemConfig(String apiKey, String sharedSecret) {
        this(apiKey, sharedSecret, false);
    }
}
