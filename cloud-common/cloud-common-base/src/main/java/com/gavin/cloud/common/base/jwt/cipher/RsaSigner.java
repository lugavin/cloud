package com.gavin.cloud.common.base.jwt.cipher;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;

public class RsaSigner implements Signer {

    private final RSAPrivateKey key;
    private final String alg;

    public RsaSigner(RSAPrivateKey key) {
        this(key, Algorithm.Type.RS256);
    }

    public RsaSigner(RSAPrivateKey key, Algorithm.Type type) {
        this(key, type.value());
    }

    public RsaSigner(RSAPrivateKey key, String alg) {
        this.key = key;
        this.alg = alg;
    }

    @Override
    public byte[] sign(byte[] bytes) {
        try {
            Signature sig = Signature.getInstance(alg);
            sig.initSign(key);
            sig.update(bytes);
            return sig.sign();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAlgorithm() {
        return alg;
    }

}
