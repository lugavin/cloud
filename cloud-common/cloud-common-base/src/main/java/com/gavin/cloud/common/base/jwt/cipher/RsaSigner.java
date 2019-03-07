package com.gavin.cloud.common.base.jwt.cipher;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;

public class RSASigner implements Signer {

    private final RSAPrivateKey key;
    private final String alg;

    public RSASigner(RSAPrivateKey key) {
        this(key, Algorithm.Type.RS256);
    }

    public RSASigner(RSAPrivateKey key, Algorithm.Type type) {
        this(key, type.value());
    }

    public RSASigner(RSAPrivateKey key, String alg) {
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
