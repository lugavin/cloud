package com.gavin.cloud.common.base.jwt.cipher;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

public class MacSignerVerifier implements Signer, Verifier {

    private final SecretKey key;
    private final String alg;

    public MacSignerVerifier(SecretKey key) {
        this(key, Algorithm.Type.HS256);
    }

    public MacSignerVerifier(SecretKey key, Algorithm.Type type) {
        this(key, type.value());
    }

    public MacSignerVerifier(SecretKey key, String alg) {
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

    @Override
    public boolean verify(byte[] data, byte[] signature) {
        return isEqual(sign(data), signature);
    }

    @Override
    public String getAlgorithm() {
        return alg;
    }

    /**
     * @see <a href="https://codahale.com/a-lesson-in-timing-attacks/"></a>
     */
    private boolean isEqual(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int xor = 0;
        for (int i = 0; i < a.length; i++) {
            xor |= a[i] ^ b[i];
        }
        return xor == 0;
    }

}