package com.taxstreem.services;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionTest {

    private byte[] randomKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return key;
    }

    @Test
    void encryptThenDecrypt_returnsOriginal() throws Exception {
        byte[] key = randomKey();
        String original = "username:s3cr3tP@ssword";
        String encrypted = Encryption.encryptTpmCredentials(original, key);
        String decrypted = Encryption.decryptTpmCredentials(encrypted, key);
        assertEquals(original, decrypted);
    }

    @Test
    void encrypt_producesDifferentCiphertextEachTime() throws Exception {
        byte[] key = randomKey();
        String cred = "username:password";
        String first = Encryption.encryptTpmCredentials(cred, key);
        String second = Encryption.encryptTpmCredentials(cred, key);
        assertNotEquals(first, second, "Random IV must produce distinct ciphertext on each call");
    }

    @Test
    void encrypt_outputIsValidBase64() throws Exception {
        byte[] key = randomKey();
        String encrypted = Encryption.encryptTpmCredentials("test-cred", key);
        assertDoesNotThrow(() -> Base64.getDecoder().decode(encrypted));
    }

    @Test
    void encrypt_outputContainsIvPlusCiphertextPlusTag() throws Exception {
        byte[] key = randomKey();
        String encrypted = Encryption.encryptTpmCredentials("hi", key);
        byte[] combined = Base64.getDecoder().decode(encrypted);
        // 12-byte IV + at least 1 byte ciphertext + 16-byte GCM tag
        assertTrue(combined.length >= 29, "Combined bytes must be at least 29 (12 IV + 1 data + 16 tag)");
    }

    @Test
    void decrypt_withWrongKey_throwsException() throws Exception {
        byte[] key = randomKey();
        byte[] wrongKey = randomKey();
        String encrypted = Encryption.encryptTpmCredentials("secret", key);
        assertThrows(Exception.class, () -> Encryption.decryptTpmCredentials(encrypted, wrongKey));
    }

    @Test
    void decrypt_withTamperedCiphertext_throwsException() throws Exception {
        byte[] key = randomKey();
        String encrypted = Encryption.encryptTpmCredentials("secret", key);
        byte[] combined = Base64.getDecoder().decode(encrypted);
        combined[combined.length - 1] ^= 0xFF; // flip last byte of auth tag
        String tampered = Base64.getEncoder().encodeToString(combined);
        assertThrows(Exception.class, () -> Encryption.decryptTpmCredentials(tampered, key));
    }
}
