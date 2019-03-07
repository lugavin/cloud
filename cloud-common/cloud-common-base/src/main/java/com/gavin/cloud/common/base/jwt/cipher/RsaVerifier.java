package com.gavin.cloud.common.base.jwt.cipher;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;

public class RsaVerifier implements Verifier {

    private final RSAPublicKey key;
    private final String alg;

    public RsaVerifier(RSAPublicKey key) {
        this(key, Algorithm.Type.RS256);
    }

    public RsaVerifier(RSAPublicKey key, Algorithm.Type type) {
        this(key, type.value());
    }

    public RsaVerifier(RSAPublicKey key, String alg) {
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