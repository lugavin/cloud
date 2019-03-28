package com.gavin.cloud.distributed.jwt.cipher;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class MACSignerVerifier implements Signer, Verifier {

    private final SecretKey key;
    private final String alg;

    public MACSignerVerifier(SecretKey key, String alg) {
        this.key = key;
        this.alg = alg;
    }

    @Override
    public byte[] sign(byte[] data) {
        try {
            Mac mac = Mac.getInstance(alg);
            mac.init(key);
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <pre>{@code
     * static boolean isEqual(byte[] a, byte[] b) {
     *     if (a.length != b.length) {
     *         return false;
     *     }
     *     int result = 0;
     *     for (int i = 0; i < a.length; i++) {
     *         result |= a[i] ^ b[i];
     *     }
     *     return result == 0;
     * }
     * }</pre>
     *
     * @see <a href="https://codahale.com/a-lesson-in-timing-attacks/">MessageDigest#isEqual</a>
     */
    @Override
    public boolean verify(byte[] data, byte[] sig) {
        return MessageDigest.isEqual(sign(data), sig);
    }

    @Override
    public String getAlgorithm() {
        return alg;
    }

}