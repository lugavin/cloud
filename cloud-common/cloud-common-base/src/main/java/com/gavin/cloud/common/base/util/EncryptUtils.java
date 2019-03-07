package com.gavin.cloud.common.base.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

public abstract class EncryptUtils {

    public static final String DEFAULT_KEY = "CHINESE SOFTWARE";
    public static final String AES_ALGORITHM_NAME = "AES";
    public static final String AES_ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    public static String base64Encode(byte[] src) {
        return Base64.getUrlEncoder().encodeToString(src);
    }

    public static byte[] base64Decode(String src) {
        return Base64.getUrlDecoder().decode(src);
    }

    public static String encrypt(String src, String key) {
        return base64Encode(crypt(src.getBytes(), key.getBytes(), Cipher.ENCRYPT_MODE));
    }

    public static String decrypt(String src, String key) {
        return new String(crypt(base64Decode(src), key.getBytes(), Cipher.DECRYPT_MODE));
    }

    private static byte[] crypt(byte[] bytes, byte[] key, int mode) {
        Cipher cipher = newCipherInstance();
        initCipher(cipher, mode, new SecretKeySpec(key, AES_ALGORITHM_NAME));
        return crypt(cipher, bytes);
    }

    private static Cipher newCipherInstance() {
        try {
            return Cipher.getInstance(AES_ALGORITHM_STR);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to acquire a Java JCA Cipher instance using %s.getInstance(\"%s\"). ", Cipher.class.getName(), AES_ALGORITHM_STR), e);
        }
    }

    private static void initCipher(Cipher cipher, int mode, Key key) {
        initCipher(cipher, mode, key, null, null);
    }

    private static void initCipher(Cipher cipher, int mode, Key key, AlgorithmParameterSpec spec, SecureRandom random) {
        try {
            if (random != null) {
                if (spec != null) {
                    cipher.init(mode, key, spec, random);
                } else {
                    cipher.init(mode, key, random);
                }
            } else {
                if (spec != null) {
                    cipher.init(mode, key, spec);
                } else {
                    cipher.init(mode, key);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to init cipher instance.", e);
        }
    }

    private static byte[] crypt(Cipher cipher, byte[] bytes) {
        try {
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute 'doFinal' with cipher instance [" + cipher + "].", e);
        }
    }

}
