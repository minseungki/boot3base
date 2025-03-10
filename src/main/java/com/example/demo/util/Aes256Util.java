package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class Aes256Util {

    private final String AES = "AES";
    private final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private final int GCM_IV_LENGTH = 12; // 96 bits
    private final int GCM_TAG_LENGTH = 16; // 128 bits
    private final int KEY_LENGTH = 256; // 256 bits
    private final int ITERATION_COUNT = 65536;
    private final String REPLACEMENT_CHAR = "rEpLaCeMeNtChAr";

    private final SecretKey secretKey;

    // Constructor to generate a secret key from a password and salt
    public Aes256Util(
            @Value("${encryption.password}") String password,
            @Value("${encryption.salt}") String salt) {
        try {
            this.secretKey = generateSecretKey(password, salt);
        } catch(Exception ex) {
            throw new RuntimeException();
        }
    }

    // Generate SecretKey from password and salt using PBKDF2
    private SecretKey generateSecretKey(String password, String salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, AES);
    }

    // Encrypt a plain text
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            byte[] iv = generateIV();
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] cipherWithIv = concatenate(iv, cipherText);

            return Base64.getEncoder().encodeToString(cipherWithIv);
        } catch(Exception ex) {
            throw new RuntimeException();
        }
    }

    // Decrypt an encrypted text
    public String decrypt(String encryptedText) {
        try {
            byte[] cipherWithIv = Base64.getDecoder().decode(encryptedText);
            byte[] iv = extractIV(cipherWithIv);
            byte[] cipherText = extractCipherText(cipherWithIv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch(Exception ex) {
            throw new RuntimeException();
        }
    }

    // Generate a random IV
    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    // Concatenate IV and CipherText
    private byte[] concatenate(byte[] iv, byte[] cipherText) {
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        return combined;
    }

    // Extract IV from combined data
    private byte[] extractIV(byte[] combined) {
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        return iv;
    }

    // Extract CipherText from combined data
    private byte[] extractCipherText(byte[] combined) {
        byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherText.length);
        return cipherText;
    }

    public String encryptReplaceSlash(String str) {
        return encrypt(str).replaceAll("/", REPLACEMENT_CHAR);
    }
    public String decryptReplaceSlash(String str)  {
        return decrypt(str.replaceAll(REPLACEMENT_CHAR, "/"));
    }

}
