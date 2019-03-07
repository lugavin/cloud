package com.gavin.cloud.common.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 生成Md5摘要
 *
 * @author Gavin
 */
public abstract class Md5Hash {

    private static final int DEFAULT_ITERATIONS = 1;

    private static final String ALGORITHM_NAME = "MD5";

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String hash(String source, String salt) {
        return hash(source, salt, DEFAULT_ITERATIONS);
    }

    public static String hash(String source, String salt, int hashIterations) {
        return new String(toHex(hash(source.getBytes(), salt.getBytes(), Math.max(DEFAULT_ITERATIONS, hashIterations))));
    }

    public static byte[] digest(byte[] bytes) {
        return getDigest(ALGORITHM_NAME).digest(bytes);
    }

    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(String.format("Could not find MessageDigest with algorithm '%s'", algorithm), ex);
        }
    }

    private static char[] toHex(byte[] bytes) {
        char[] chars = new char[bytes.length << 1];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            chars[j++] = DIGITS[(0xF0 & bytes[i]) >>> 4];
            chars[j++] = DIGITS[0x0F & bytes[i]];
        }
        return chars;
    }

    private static byte[] hash(byte[] bytes, byte[] salt, int hashIterations) {
        MessageDigest digest = getDigest(ALGORITHM_NAME);
        if (salt != null) {
            digest.reset();
            digest.update(salt);
        }
        byte[] hashed = digest.digest(bytes);
        int iterations = hashIterations - DEFAULT_ITERATIONS;
        for (int i = 0; i < iterations; i++) {
            digest.reset();
            hashed = digest.digest(hashed);
        }
        return hashed;
    }

    // public static void main(String[] args) {
    //     String source = "111111";
    //     String salt = "10086";
    //     String hash = Md5Hash.hash(source, salt);
    //     System.out.println(hash);
    // }

}
