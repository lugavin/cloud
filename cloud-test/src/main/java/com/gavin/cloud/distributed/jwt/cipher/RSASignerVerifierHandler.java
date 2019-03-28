package com.gavin.cloud.distributed.jwt.cipher;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RSASignerVerifierHandler implements SignerVerifierHandler {

    private static final Set<String> ALGORITHMS = new HashSet<>(Arrays.asList(
            Algorithm.Type.RS256.name(),
            Algorithm.Type.RS384.name(),
            Algorithm.Type.RS512.name()
    ));

    @Override
    public boolean supportsAlgorithm(String alg) {
        return ALGORITHMS.contains(alg);
    }

    @Override
    public byte[] sign(CryptoHolder cryptoHolder) {
        RSAPrivateKey privateKey = KeyCreator.createRSAPrivateKey(cryptoHolder.getKey());
        return new RSASigner(privateKey, cryptoHolder.getAlg()).sign(cryptoHolder.getData());
    }

    @Override
    public boolean verify(CryptoHolder cryptoHolder, byte[] sig) {
        RSAPublicKey publicKey = KeyCreator.createRSAPublicKey(cryptoHolder.getKey());
        return new RSAVerifier(publicKey, cryptoHolder.getAlg()).verify(cryptoHolder.getData(), sig);
    }

}
