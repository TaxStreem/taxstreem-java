package com.taxstreem.services;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryption {
    /**
     * Encrypt TaxProMax credentials using AES-256-GCM.
     * This matches the encryption logic used by the TaxStreem API.
     *
     * @param tpmCred   The credentials to encrypt
     * @param secretKey The 32-byte (256-bit) AES key
     * @return A base64 encoded string containing IV + ciphertext + tag
     */
    public static String encryptTpmCredentials(String tpmCred, byte[] secretKey) throws Exception {
        // Generate a random 12-byte IV (recommended size for GCM)
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128-bit auth tag

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

        // Encrypt — output is ciphertext + 16-byte auth tag appended by Java's GCM impl
        byte[] ciphertextWithTag = cipher.doFinal(tpmCred.getBytes(StandardCharsets.UTF_8));

        // Concatenate: IV (12 bytes) + ciphertext + tag
        byte[] combined = new byte[iv.length + ciphertextWithTag.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertextWithTag, 0, combined, iv.length, ciphertextWithTag.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts the encrypted TPM credentials.
     *
     * @param encryptedBase64 The base64 encoded string containing IV + ciphertext + tag
     * @param secretKey     The 32-byte (256-bit) AES key
     * @return The decrypted TPM credentials
     */
    public static String decryptTpmCredentials(String encryptedBase64, byte[] secretKey) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedBase64);

        // Extract IV (first 12 bytes)
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, 12);

        // Extract ciphertext + tag (remainder)
        byte[] ciphertextWithTag = new byte[combined.length - 12];
        System.arraycopy(combined, 12, ciphertextWithTag, 0, ciphertextWithTag.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

        byte[] plaintext = cipher.doFinal(ciphertextWithTag);
        return new String(plaintext, StandardCharsets.UTF_8);
    }
}
