package com.gavin.cloud.common.base.jwt;

import com.gavin.cloud.common.base.jwt.cipher.Algorithm;
import com.gavin.cloud.common.base.jwt.cipher.CryptoHolder;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierFactory;
import com.gavin.cloud.common.base.jwt.cipher.SignerVerifierHandler;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JwtBuilder {

    private final Map<String, String> header;
    private final Map<String, String> payload;

    public JwtBuilder() {
        this.header = new LinkedHashMap<>();
        this.payload = new LinkedHashMap<>();
    }

    public JwtBuilder withHeader(Map<String, String> header) {
        this.header.putAll(header);
        return this;
    }

    public JwtBuilder withIss(String iss) {
        addClaim(PublicClaim.Payload.iss.name(), iss);
        return this;
    }

    public JwtBuilder withSub(String sub) {
        addClaim(PublicClaim.Payload.sub.name(), sub);
        return this;
    }

    //public JwtBuilder withAud(String... aud) {
    //    addClaim(PublicClaim.Payload.aud.name(), Arrays.asList(aud));
    //    return this;
    //}

    public JwtBuilder withAud(String aud) {
        addClaim(PublicClaim.Payload.aud.name(), aud);
        return this;
    }

    public JwtBuilder withExp(String exp) {
        addClaim(PublicClaim.Payload.exp.name(), exp);
        return this;
    }

    public JwtBuilder withNbf(String nbf) {
        addClaim(PublicClaim.Payload.nbf.name(), nbf);
        return this;
    }

    public JwtBuilder withIat(String iat) {
        addClaim(PublicClaim.Payload.iat.name(), iat);
        return this;
    }

    public JwtBuilder withJti(String jti) {
        addClaim(PublicClaim.Payload.jti.name(), jti);
        return this;
    }

    public JwtBuilder with(String name, String value) {
        Objects.requireNonNull(name, "The Custom Claim's name must not be null.");
        addClaim(name, value);
        return this;
    }

    public String sign(byte[] key, Algorithm.Type alg) {
        for (SignerVerifierHandler signerVerifierHandler : SignerVerifierFactory.getSignerVerifierHandler()) {
            if (signerVerifierHandler.supportsAlgorithm(alg.name())) {
                header.put(PublicClaim.Header.alg.name(), alg.name());
                header.put(PublicClaim.Header.typ.name(), Header.JWT_TYPE);
                String headerJson = JsonHelper.fromMap(header);
                String payloadJson = JsonHelper.fromMap(payload);
                String headerBase64Encoded = Base64.getUrlEncoder().encodeToString(headerJson.getBytes(UTF_8));
                String payloadBase64Encoded = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes(UTF_8));
                byte[] data = (headerBase64Encoded + "." + payloadBase64Encoded).getBytes(UTF_8);
                CryptoHolder cryptoHolder = new CryptoHolder(alg.value(), key, data);
                byte[] sig = signerVerifierHandler.sign(cryptoHolder);
                return headerBase64Encoded + "." + payloadBase64Encoded + "." + Base64.getUrlEncoder().encodeToString(sig);
            }
        }
        throw new IllegalArgumentException("Unsupported signature algorithm: " + alg.name());
    }

    private void addClaim(String name, String value) {
        if (value == null) {
            payload.remove(name);
        } else {
            payload.put(name, value);
        }
    }

}