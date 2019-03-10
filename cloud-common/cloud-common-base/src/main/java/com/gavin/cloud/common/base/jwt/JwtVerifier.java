package com.gavin.cloud.common.base.jwt;

import com.gavin.cloud.common.base.jwt.cipher.Algorithm;
import com.gavin.cloud.common.base.jwt.cipher.CryptoHolder;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierFactory;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierHandler;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JwtVerifier {

    private final String[] parts;
    private final Header header;
    private final Payload payload;

    public JwtVerifier(String token) {
        this.parts = splitToken(token);
        this.header = new Header(JsonHelper.toMap(decodeFromBase64(parts[0])));
        this.payload = new Payload(JsonHelper.toMap(decodeFromBase64(parts[1])));
    }

    public boolean verify(byte[] key) {
        Algorithm.Type alg = Algorithm.forType(header.getAlg());
        if (alg == null) {
            throw new IllegalArgumentException("Unsupported signature algorithm: " + header.getAlg());
        }
        for (SignerVerifierHandler signerVerifierHandler : SignerVerifierFactory.getSignerVerifierHandler()) {
            if (signerVerifierHandler.supportsAlgorithm(alg.name())) {
                byte[] data = (parts[0] + "." + parts[1]).getBytes(UTF_8);
                CryptoHolder cryptoHolder = new CryptoHolder(alg.value(), key, data);
                return signerVerifierHandler.verify(cryptoHolder, Base64.getUrlDecoder().decode(parts[2]));
            }
        }
        return false;
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

    private static String decodeFromBase64(String src) {
        return new String(Base64.getUrlDecoder().decode(src), StandardCharsets.UTF_8);
    }

    private static String[] splitToken(String token) throws IllegalArgumentException {
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
