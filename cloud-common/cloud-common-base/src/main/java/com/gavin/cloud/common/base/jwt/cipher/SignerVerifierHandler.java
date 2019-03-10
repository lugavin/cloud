package com.gavin.cloud.common.base.jwt.cipher;

public interface SignerVerifierHandler {

    boolean supportsAlgorithm(String alg);

    byte[] sign(CryptoHolder cryptoHolder);

    boolean verify(CryptoHolder cryptoHolder, byte[] sig);

}