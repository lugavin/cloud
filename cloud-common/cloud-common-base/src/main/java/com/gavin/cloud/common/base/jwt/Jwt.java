package com.gavin.cloud.common.base.jwt;

public abstract class Jwt {

    public static JwtBuilder builder() {
        return new JwtBuilder();
    }

    public static JwtVerifier verifier(String token) {
        return new JwtVerifier(token);
    }

}
