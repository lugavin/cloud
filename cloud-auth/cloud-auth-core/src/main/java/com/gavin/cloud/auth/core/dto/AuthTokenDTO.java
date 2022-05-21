package com.gavin.cloud.auth.core.dto;

import lombok.Data;

@Data
public class AuthTokenDTO {

    private final String accessToken;
    private final String refreshToken;
    private final Long expiresIn;
    // private final String nonce;

}
