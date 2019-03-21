package com.gavin.cloud.common.base.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public abstract class KeyHelper {

    private static final String ALGORITHM_RSA = "RSA";

    public static PrivateKey createPrivateKey(String privateKeyEncoded) {
        try {
            return getKeyFactory(ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(decodeFromBase64(privateKeyEncoded)));
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static PublicKey createPublicKey(String publicKeyEncoded) {
        try {
            return getKeyFactory(ALGORITHM_RSA).generatePublic(new X509EncodedKeySpec(decodeFromBase64(publicKeyEncoded)));
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static KeyFactory getKeyFactory(String algorithm) {
        try {
            return KeyFactory.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] decodeFromBase64(String keyEncoded) {
        return Base64.getUrlDecoder().decode(keyEncoded);
    }

}
