package com.gavin.cloud.common.base.jwt.cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class KeyHelper {

    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_RSA = "RSA";

    public static SecretKey createSecretKey(byte[] key) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES);
            keyGenerator.init(256, new SecureRandom(key));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyEncoded = secretKey.getEncoded();
            return new SecretKeySpec(keyEncoded, ALGORITHM_AES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey createRSAPrivateKey(byte[] privateKeyEncoded) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(privateKeyEncoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPublicKey createRSAPublicKey(byte[] publicKeyEncoded) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance(ALGORITHM_RSA).generatePublic(new X509EncodedKeySpec(publicKeyEncoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
