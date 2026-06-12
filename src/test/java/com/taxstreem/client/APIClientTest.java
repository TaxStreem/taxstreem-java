package com.taxstreem.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

class APIClientTest {

    /** Minimal HttpResponse implementation — only statusCode() and body() matter for APIClient. */
    private static HttpResponse<String> fakeResponse(int statusCode, String body) {
        return new HttpResponse<>() {
            @Override public int statusCode() { return statusCode; }
            @Override public String body() { return body; }
            @Override public HttpRequest request() { return null; }
            @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
            @Override public HttpHeaders headers() { return HttpHeaders.of(java.util.Map.of(), (a, b) -> true); }
            @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
            @Override public URI uri() { return null; }
            @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
        };
    }

    private APIClient client(int maxRetries, HttpSender sender) {
        return new APIClient("test-key", "test-secret", false, maxRetries, sender);
    }

    @Test
    void request_success_returnsResponseBody() throws Exception {
        APIClient apiClient = client(0, req -> fakeResponse(200, "{\"status\":\"success\"}"));

        assertEquals("{\"status\":\"success\"}", apiClient.request("POST", "/flux/vat-filing/single", "{}"));
    }

    @Test
    void request_clientError_returnsBodyWithoutRetrying() throws Exception {
        int[] callCount = {0};
        APIClient apiClient = client(3, req -> { callCount[0]++; return fakeResponse(400, "{\"error\":\"bad request\"}"); });

        String result = apiClient.request("POST", "/test", "{}");

        assertEquals("{\"error\":\"bad request\"}", result);
        assertEquals(1, callCount[0], "4xx must not trigger retries");
    }

    @Test
    void request_serverError_retriesBeforeSuccess() throws Exception {
        int[] callCount = {0};
        APIClient apiClient = client(1, req -> {
            callCount[0]++;
            return callCount[0] == 1 ? fakeResponse(500, "error") : fakeResponse(200, "{\"status\":\"ok\"}");
        });

        String result = apiClient.request("POST", "/test", "{}");

        assertEquals("{\"status\":\"ok\"}", result);
        assertEquals(2, callCount[0], "Should have tried twice — once failing, once succeeding");
    }

    @Test
    void request_serverError_exhaustsRetriesAndReturnsLastBody() throws Exception {
        APIClient apiClient = client(0, req -> fakeResponse(500, "{\"error\":\"server error\"}"));

        String result = apiClient.request("POST", "/test", null);

        assertEquals("{\"error\":\"server error\"}", result);
    }

    @Test
    void request_ioException_throwsAfterMaxRetries() {
        APIClient apiClient = client(0, req -> { throw new IOException("connection refused"); });

        assertThrows(Exception.class, () -> apiClient.request("POST", "/test", null));
    }

    @Test
    void request_setsApiKeyHeader() throws Exception {
        APIClient apiClient = new APIClient("my-key", "secret", false, 0, req -> {
            assertEquals("my-key", req.headers().firstValue("x-api-key").orElse(null));
            return fakeResponse(200, "{}");
        });

        apiClient.request("GET", "/test", null);
    }

    @Test
    void request_setsUserAgentHeader() throws Exception {
        APIClient apiClient = client(0, req -> {
            assertEquals("TaxStreem-Java-SDK/1.0.0", req.headers().firstValue("User-Agent").orElse(null));
            return fakeResponse(200, "{}");
        });

        apiClient.request("GET", "/test", null);
    }

    @Test
    void request_nullBody_sendsNoBody() throws Exception {
        APIClient apiClient = client(0, req -> {
            assertTrue(req.bodyPublisher().isEmpty() || req.bodyPublisher().get().contentLength() == 0);
            return fakeResponse(200, "{}");
        });

        apiClient.request("GET", "/test", null);
    }
}
