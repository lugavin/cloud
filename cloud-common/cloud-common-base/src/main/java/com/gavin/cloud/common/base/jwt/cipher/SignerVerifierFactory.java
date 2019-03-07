package com.gavin.cloud.common.base.jwt.cipher;

public abstract class SignerVerifierFactory {

    public static Signer newSigner(byte[] key, Algorithm.Type alg) {
        switch (alg) {
            case HS256:
            case HS384:
            case HS512:
                return new MacSignerVerifier(KeyHelper.createSecretKey(key), alg);
            case RS256:
            case RS384:
            case RS512:
                return new RSASigner(KeyHelper.createRSAPrivateKey(key), alg);
            default:
                throw new IllegalArgumentException("Unsupported signature algorithm: " + alg.value());
        }
    }

    public static Verifier newVerifier(byte[] key, Algorithm.Type alg) {
        switch (alg) {
            case HS256:
            case HS384:
            case HS512:
                return new MacSignerVerifier(KeyHelper.createSecretKey(key), alg);
            case RS256:
            case RS384:
            case RS512:
                return new RSAVerifier(KeyHelper.createRSAPublicKey(key), alg);
            default:
                throw new IllegalArgumentException("Unsupported signature algorithm: " + alg.value());
        }
    }

}
