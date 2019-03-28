package com.gavin.cloud.distributed.jwt.cipher;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;

public class RSAVerifier implements Verifier {

    private final RSAPublicKey key;
    private final String alg;

    public RSAVerifier(RSAPublicKey key, String alg) {
        this.key = key;
        this.alg = alg;
    }

    @Override
    public boolean verify(byte[] data, byte[] sig) {
        try {
            Signature signature = Signature.getInstance(alg);
            signature.initVerify(key);
            signature.update(data);
            return signature.verify(sig);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAlgorithm() {
        return alg;
    }

}