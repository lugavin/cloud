package com.gavin.cloud.common.base.jwt;

import com.gavin.cloud.common.base.jwt.cipher.Algorithm;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierFactory;
import com.gavin.cloud.common.base.jwt.cipher.Verifier;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        Verifier verifier = SignerVerifierFactory.newVerifier(key, Algorithm.forName(header.getAlg()));
        byte[] signed = (parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8);
        byte[] signature = Base64.getUrlDecoder().decode(parts[2]);
        return verifier.verify(signed, signature);
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
