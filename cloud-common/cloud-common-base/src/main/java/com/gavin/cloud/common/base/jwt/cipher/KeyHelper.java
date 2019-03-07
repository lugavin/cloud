package com.gavin.cloud.common.base.jwt.cipher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class KeyHelper {

    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_RSA = "RSA";

    public static SecretKey createSecretKey(byte[] key) {
        return new SecretKeySpec(key, ALGORITHM_AES);
    }

    public static RSAPrivateKey createRSAPrivateKey(byte[] base64Encoded) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(base64Encoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPublicKey createRSAPublicKey(byte[] base64Encoded) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance(ALGORITHM_RSA).generatePublic(new X509EncodedKeySpec(base64Encoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
