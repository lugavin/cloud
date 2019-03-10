package com.gavin.cloud.common.base.jwt;

import com.gavin.cloud.common.base.jwt.cipher.Algorithm;
import com.gavin.cloud.common.base.jwt.cipher.CryptoHolder;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierFactory;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierHandler;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JwtVerifier {

    private final String token;
    private final Header header;
    private final Payload payload;

    private final String[] parts;

    public JwtVerifier(String token) {
        this.token = token;
        this.parts = splitToken(token);
        this.header = new Header(JsonHelper.toMap(decodeFromBase64(parts[0])));
        this.payload = new Payload(JsonHelper.toMap(decodeFromBase64(parts[1])));
    }

    public boolean verify(byte[] key) {
        Algorithm.Type alg = verifyAlgorithm(header.getAlg());
        for (SignerVerifierHandler signerVerifierHandler : SignerVerifierFactory.getSignerVerifierHandler()) {
            if (signerVerifierHandler.supportsAlgorithm(alg.name())) {
                return verifySignature(signerVerifierHandler, alg.value(), key) && verifyClaims(payload);
            }
        }
        return false;
    }

    public String getToken() {
        return token;
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    public String getSignature() {
        return parts[2];
    }

    private Algorithm.Type verifyAlgorithm(String algorithm) {
        Algorithm.Type alg = Algorithm.forType(algorithm);
        if (alg == null) {
            throw new IllegalArgumentException("Unsupported signature algorithm: " + algorithm);
        }
        return alg;
    }

    private boolean verifySignature(SignerVerifierHandler signerVerifierHandler, String alg, byte[] key) {
        byte[] data = (parts[0] + "." + parts[1]).getBytes(UTF_8);
        CryptoHolder cryptoHolder = new CryptoHolder(alg, key, data);
        return signerVerifierHandler.verify(cryptoHolder, Base64.getUrlDecoder().decode(parts[2]));
    }

    private boolean verifyClaims(Payload payload) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) < Long.parseLong(payload.getExp());
    }

    private static String decodeFromBase64(String src) {
        return new String(Base64.getUrlDecoder().decode(src), UTF_8);
    }

    private static String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length == 2 && token.endsWith(".")) {
            // Tokens with alg='none' have empty String as Signature.
            parts = new String[]{parts[0], parts[1], ""};
        }
        if (parts.length != 3) {
            throw new IllegalArgumentException("The token was expected to have 3 parts, but got " + parts.length);
        }
        return parts;
    }

}
