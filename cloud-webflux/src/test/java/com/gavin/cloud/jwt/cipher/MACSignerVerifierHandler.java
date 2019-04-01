package com.gavin.cloud.jwt.cipher;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MACSignerVerifierHandler implements SignerVerifierHandler {

    private static final Set<String> ALGORITHMS = new HashSet<>(Arrays.asList(
            Algorithm.Type.HS256.name(),
            Algorithm.Type.HS384.name(),
            Algorithm.Type.HS512.name()
    ));

    @Override
    public boolean supportsAlgorithm(String alg) {
        return ALGORITHMS.contains(alg);
    }

    @Override
    public byte[] sign(CryptoHolder cryptoHolder) {
        SecretKey secretKey = KeyCreator.createSecretKey(cryptoHolder.getKey());
        return new MACSignerVerifier(secretKey, cryptoHolder.getAlg()).sign(cryptoHolder.getData());
    }

    @Override
    public boolean verify(CryptoHolder cryptoHolder, byte[] sig) {
        SecretKey secretKey = KeyCreator.createSecretKey(cryptoHolder.getKey());
        return new MACSignerVerifier(secretKey, cryptoHolder.getAlg()).verify(cryptoHolder.getData(), sig);
    }

}
