package com.deneme.influencerinsight.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class SocialTokenCipher {

    private static final String ENCRYPTED_PREFIX = "v1:";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int INITIALIZATION_VECTOR_LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.security.social-token-encryption-key}")
    private String encodedKey;

    private SecretKey encryptionKey;

    @PostConstruct
    void initialize() {
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("Social token encryption key must be 32 bytes.");
        }
        encryptionKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String value) {
        if (value == null || value.isBlank() || value.startsWith(ENCRYPTED_PREFIX)) {
            return value;
        }

        try {
            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_LENGTH];
            secureRandom.nextBytes(initializationVector);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, initializationVector));
            byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[initializationVector.length + encryptedValue.length];
            System.arraycopy(initializationVector, 0, payload, 0, initializationVector.length);
            System.arraycopy(encryptedValue, 0, payload, initializationVector.length, encryptedValue.length);
            return ENCRYPTED_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to encrypt social platform token.", ex);
        }
    }

    public String decrypt(String value) {
        if (value == null || value.isBlank() || !value.startsWith(ENCRYPTED_PREFIX)) {
            return value;
        }

        try {
            byte[] payload = Base64.getUrlDecoder().decode(value.substring(ENCRYPTED_PREFIX.length()));
            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_LENGTH];
            byte[] encryptedValue = new byte[payload.length - INITIALIZATION_VECTOR_LENGTH];
            System.arraycopy(payload, 0, initializationVector, 0, INITIALIZATION_VECTOR_LENGTH);
            System.arraycopy(payload, INITIALIZATION_VECTOR_LENGTH, encryptedValue, 0, encryptedValue.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, initializationVector));
            return new String(cipher.doFinal(encryptedValue), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to decrypt social platform token.", ex);
        }
    }
}
