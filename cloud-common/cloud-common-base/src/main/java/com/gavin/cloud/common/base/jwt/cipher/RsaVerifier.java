package com.gavin.cloud.common.base.jwt.cipher;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;

public class RSAVerifier implements Verifier {

    private final RSAPublicKey key;
    private final String alg;

    public RSAVerifier(RSAPublicKey key) {
        this(key, Algorithm.Type.RS256);
    }

    public RSAVerifier(RSAPublicKey key, Algorithm.Type type) {
        this(key, type.value());
    }

    public RSAVerifier(RSAPublicKey key, String alg) {
        this.key = key;
        this.alg = alg;
    }

    @Override
    public boolean verify(byte[] content, byte[] signature) {
        try {
            Signature sig = Signature.getInstance(alg);
            sig.initVerify(key);
            sig.update(content);
            return sig.verify(signature);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAlgorithm() {
        return alg;
    }

}