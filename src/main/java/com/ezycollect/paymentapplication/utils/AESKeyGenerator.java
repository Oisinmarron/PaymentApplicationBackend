package com.ezycollect.paymentapplication.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AESKeyGenerator {

    // To generate encryption key for card number
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256-bit key for strong encryption
        SecretKey secretKey = keyGen.generateKey();

        // Encode key as Base64 for storage
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated AES Key: " + encodedKey);
    }
}
