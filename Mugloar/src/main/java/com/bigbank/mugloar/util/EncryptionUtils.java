package com.bigbank.mugloar.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtils {

    public static String decryptBase64(String original){
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(original);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
           throw new IllegalStateException("Impossible to decrypt "+original+" Because of "+e.getMessage());
        }
    }



}
