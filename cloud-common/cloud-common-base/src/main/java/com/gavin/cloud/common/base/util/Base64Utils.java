package com.gavin.cloud.common.base.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Base64Utils {

    /**
     * Base64-encode the given byte array.
     *
     * @param src the original byte array (may be {@code null})
     * @return the encoded byte array (or {@code null} if the input was {@code null})
     */
    public static byte[] encode(byte[] src) {
        if (src == null || src.length == 0) {
            return src;
        }
        return Base64.getEncoder().encode(src);
    }

    /**
     * Encode the value using Base64.
     *
     * @param src the String to Base64 encode
     * @return the Base64 encoded value
     */
    public static String encode(String src) {
        return new String(encode(src.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Base64-decode the given byte array.
     *
     * @param src the encoded byte array (may be {@code null})
     * @return the original byte array (or {@code null} if the input was {@code null})
     */
    public static byte[] decode(byte[] src) {
        if (src == null || src.length == 0) {
            return src;
        }
        return Base64.getDecoder().decode(src);
    }

    /**
     * Decode the value using Base64.
     *
     * @param src the Base64 String to decode
     * @return the Base64 decoded value
     */
    public static String decode(String src) {
        try {
            return new String(decode(src.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Base64-encode the given byte array using the RFC 4648
     * "URL and Filename Safe Alphabet".
     *
     * @param src the original byte array (may be {@code null})
     * @return the encoded byte array (or {@code null} if the input was {@code null})
     */
    public static byte[] encodeUrlSafe(byte[] src) {
        if (src == null || src.length == 0) {
            return src;
        }
        return Base64.getUrlEncoder().encode(src);
    }

    /**
     * Base64-decode the given byte array using the RFC 4648
     * "URL and Filename Safe Alphabet".
     *
     * @param src the encoded byte array (may be {@code null})
     * @return the original byte array (or {@code null} if the input was {@code null})
     */
    public static byte[] decodeUrlSafe(byte[] src) {
        if (src == null || src.length == 0) {
            return src;
        }
        return Base64.getUrlDecoder().decode(src);
    }

    /**
     * Base64-encode the given byte array to a String.
     *
     * @param src the original byte array (may be {@code null})
     * @return the encoded byte array as a UTF-8 String
     */
    public static String encodeToString(byte[] src) {
        if (src == null) {
            return null;
        }
        if (src.length == 0) {
            return "";
        }
        return new String(encode(src), StandardCharsets.UTF_8);
    }

    /**
     * Base64-decode the given byte array from an UTF-8 String.
     *
     * @param src the encoded UTF-8 String (may be {@code null})
     * @return the original byte array (or {@code null} if the input was {@code null})
     */
    public static byte[] decodeFromString(String src) {
        if (src == null) {
            return null;
        }
        if (src.isEmpty()) {
            return new byte[0];
        }
        return decode(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64-encode the given byte array to a String using the RFC 4648
     * "URL and Filename Safe Alphabet".
     *
     * @param src the original byte array (may be {@code null})
     * @return the encoded byte array as a UTF-8 String
     */
    public static String encodeToUrlSafeString(byte[] src) {
        return new String(encodeUrlSafe(src), StandardCharsets.UTF_8);
    }

    /**
     * Base64-decode the given byte array from an UTF-8 String using the RFC 4648
     * "URL and Filename Safe Alphabet".
     *
     * @param src the encoded UTF-8 String (may be {@code null})
     * @return the original byte array (or {@code null} if the input was {@code null})
     */
    public static byte[] decodeFromUrlSafeString(String src) {
        return decodeUrlSafe(src.getBytes(StandardCharsets.UTF_8));
    }

}
