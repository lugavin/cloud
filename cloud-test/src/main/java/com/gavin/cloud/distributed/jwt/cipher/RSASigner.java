package com.gavin.cloud.distributed.jwt.cipher;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;

public class RSASigner implements Signer {

    private final RSAPrivateKey key;
    private final String alg;

    public RSASigner(RSAPrivateKey key, String alg) {
        this.key = key;
        this.alg = alg;
    }

    @Override
    public byte[] sign(byte[] data) {
        try {
            Signature sig = Signature.getInstance(alg);
            sig.initSign(key);
            sig.update(data);
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
