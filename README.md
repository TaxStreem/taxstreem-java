# TaxStreem Java SDK

A lightweight Java client library for the TaxStreem API. Built for modern Java runtimes using the built-in `HttpClient`.

## Features

- **Minimal Dependencies**: Only Jackson Databind for JSON serialization — no bloated HTTP libraries.
- **HMAC Signing**: Automatic payload signing for secure communication.
- **In-Flight Encryption**: Built-in AES-256-GCM encryption for sensitive credentials.
- **Automatic Retries**: Configurable retry logic with exponential backoff for resilient API calls.
- **VAT & WHT Filings**: Single and batch filing support for both VAT and Withholding Tax.
- **Debug Mode**: Built-in request logging for easier integration.

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>com.taxstreem</groupId>
  <artifactId>taxstreem-java-sdk</artifactId>
  <version>1.0</version>
</dependency>
```

For Gradle:

```groovy
implementation 'com.taxstreem:taxstreem-java-sdk:1.0'
```

## Quick Start

### Basic Initialization

```java
import com.taxstreem.Taxstreem;
import com.taxstreem.dto.TaxStreemConfig;

TaxStreemConfig config = new TaxStreemConfig("your-api-key", "your-shared-secret", true); // debug=true
Taxstreem sdk = new Taxstreem(config);
```

To disable debug logging, omit the third argument:

```java
TaxStreemConfig config = new TaxStreemConfig("your-api-key", "your-shared-secret");
Taxstreem sdk = new Taxstreem(config);
```

## Usage

### 1. Encrypting Credentials

TaxStreem uses **In-Flight Encryption** (AES-256-GCM) to protect sensitive credentials (like TaxProMax login details) before they are sent to the API.

```java
import com.taxstreem.services.Encryption;
import java.security.SecureRandom;

// Your 32-byte (256-bit) AES key derived from your shared secret
byte[] secretKey = new byte[32];
new SecureRandom().nextBytes(secretKey);

// Encrypt the credentials
String encryptedPayload = Encryption.encryptTpmCredentials("user@example.com:secure-password", secretKey);
```

### 2. Submitting a VAT Filing (Flux)

Once you have the `encryptedPayload`, you can submit a VAT return.

```java
import com.taxstreem.dto.VAT.VatFilingItemDto;
import com.taxstreem.dto.VAT.VatFilingPayloadDto;
import com.taxstreem.dto.VAT.FilingResponseDto;

VatFilingItemDto item = new VatFilingItemDto(
    1,                      // vatStatus
    5000,                   // amount
    "Baby diapers",         // item
    "Bought kisskid diaper", // narration
    "2345544-0001",         // taxId
    "Retail Customer"       // beneficiary
);

VatFilingPayloadDto payload = new VatFilingPayloadDto(
    encryptedPayload,
    1,    // month
    2024, // year
    new VatFilingItemDto[]{ item }
);

try {
    FilingResponseDto result = sdk.getFlux().fileVatSingle(payload);
    System.out.println("Filing submitted: " + result.reference_id());
} catch (Exception e) {
    System.err.println("Filing failed: " + e.getMessage());
}
```

### 3. Submitting a Batch VAT Filing

```java
import com.taxstreem.dto.VAT.VatFilingBatchPayloadDto;
import com.taxstreem.dto.VAT.BatchFilingResponseDto;

VatFilingPayloadDto[] payloads = { payload1, payload2 };
VatFilingBatchPayloadDto batchPayload = new VatFilingBatchPayloadDto("BATCH-001", payloads);

BatchFilingResponseDto result = sdk.getFlux().fileVatBatch(batchPayload);
System.out.println("Batch submitted: " + result.batchId());
```

### 4. Submitting a WHT Filing

```java
import com.taxstreem.dto.WHT.WhtFilingItemDto;
import com.taxstreem.dto.WHT.WhtFilingPayloadDto;
import com.taxstreem.dto.VAT.FilingResponseDto;

WhtFilingItemDto item = new WhtFilingItemDto(
    "Acme Corp",      // beneficiaryName
    "TIN-456",        // beneficiaryTin
    "2024-01-15",     // transactionDate
    "Consulting fee", // transactionDesc
    "10000",          // transactionAmount
    "10",             // rate
    "1000",           // taxAmount
    "SCH-001"         // scheduleReference
);

WhtFilingPayloadDto payload = new WhtFilingPayloadDto(
    encryptedPayload,
    1,    // month
    2024, // year
    new WhtFilingItemDto[]{ item }
);

FilingResponseDto result = sdk.getFlux().fileWhtSingle(payload);
System.out.println("WHT filing submitted: " + result.reference_id());
```

### 5. Submitting a Batch WHT Filing

```java
import com.taxstreem.dto.WHT.WhtFilingBatchPayloadDto;
import com.taxstreem.dto.VAT.BatchFilingResponseDto;

WhtFilingPayloadDto[] payloads = { payload1, payload2 };
WhtFilingBatchPayloadDto batchPayload = new WhtFilingBatchPayloadDto("BATCH-002", payloads);

BatchFilingResponseDto result = sdk.getFlux().fileWhtBatch(batchPayload);
System.out.println("Batch submitted: " + result.batchId());
```

## Configuration

| Parameter      | Type      | Required | Default | Description                              |
|----------------|-----------|----------|---------|------------------------------------------|
| `apiKey`       | `String`  | Yes      | —       | Your TaxStreem API key                   |
| `sharedSecret` | `String`  | Yes      | —       | Your shared secret for HMAC signing      |
| `debug`        | `Boolean` | No       | `false` | Logs outgoing requests to `System.out`   |

## Security Design

This SDK implements two layers of security:

1. **Request Signing (HMAC-SHA256)**: Every outgoing request body is signed using your `sharedSecret`. The signature is sent in the `x-taxstreem-signature` header to verify request integrity.
2. **Payload Encryption (AES-256-GCM)**: Sensitive credentials within the payload are encrypted using AES-256-GCM before transmission, ensuring they remain protected end-to-end.

## Retry Behaviour

The SDK automatically retries failed requests on server-side errors (HTTP 5xx) using exponential backoff:

| Attempt | Delay    |
|---------|----------|
| 1st     | 500 ms   |
| 2nd     | 1,000 ms |
| 3rd     | 2,000 ms |

Client errors (HTTP 4xx) are not retried. The default maximum retry count is **3** and can be configured via the `APIClient` constructor directly if needed.

## Requirements

- Java 21 or higher
- Maven 3.6+ or Gradle 7+

## Issues

Kindly [open an issue](https://github.com/TaxStreem/taxstreem-java/issues) if you discover any bug or have problems using this library.

## License

MIT License. See [LICENSE](LICENSE) for more information.
