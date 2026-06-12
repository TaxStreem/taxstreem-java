package com.taxstreem.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIClient implements IApiClient {
    private static final String BASE_URL = "https://api.taxstreem.com/v1";
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long BASE_BACKOFF_MS = 500; // 500ms, 1000ms, 2000ms...

    private final String apiKey;
    private final String apiSecret;
    private final boolean debug;
    private final int maxRetries;
    private final HttpSender sender;

    public APIClient(String apiKey, String apiSecret) {
        this(apiKey, apiSecret, true, DEFAULT_MAX_RETRIES);
    }

    public APIClient(String apiKey, String apiSecret, boolean debug) {
        this(apiKey, apiSecret, debug, DEFAULT_MAX_RETRIES);
    }

    public APIClient(String apiKey, String apiSecret, boolean debug, int maxRetries) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.debug = debug;
        this.maxRetries = maxRetries;
        HttpClient client = HttpClient.newHttpClient();
        this.sender = req -> client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    APIClient(String apiKey, String apiSecret, boolean debug, int maxRetries, HttpSender sender) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.debug = debug;
        this.maxRetries = maxRetries;
        this.sender = sender;
    }

    @Override
    public String request(String method, String path, Object body) throws Exception {
        URI uri = URI.create(BASE_URL + path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("User-Agent", "TaxStreem-Java-SDK/1.0.0")
                .header("x-api-key", this.apiKey)
                .method(method, body != null
                        ? HttpRequest.BodyPublishers.ofString(body.toString())
                        : HttpRequest.BodyPublishers.noBody())
                .build();

        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                if (attempt > 0 && debug) {
                    System.out.println("Retry attempt " + attempt + " of " + maxRetries);
                }

                HttpResponse<String> response = sender.send(request);

                if (debug) {
                    System.out.println("Status Code: " + response.statusCode());
                    System.out.println("Response Body: \n" + response.body());
                }

                // Don't retry on client errors (4xx) — only on server errors (5xx)
                if (response.statusCode() >= 500 && attempt < maxRetries) {
                    attempt++;
                    sleepWithBackoff(attempt);
                    continue;
                }

                return response.body();

            } catch (IOException e) {
                lastException = e;
                if (attempt < maxRetries) {
                    attempt++;
                    sleepWithBackoff(attempt);
                } else {
                    break;
                }
            }
        }

        throw new Exception("Request failed after " + maxRetries + " retries", lastException);
    }

    private void sleepWithBackoff(int attempt) {
        long waitMs = BASE_BACKOFF_MS * (1L << (attempt - 1)); // 500, 1000, 2000...
        if (debug) {
            System.out.println("Waiting " + waitMs + "ms before next retry...");
        }
        try {
            Thread.sleep(waitMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
