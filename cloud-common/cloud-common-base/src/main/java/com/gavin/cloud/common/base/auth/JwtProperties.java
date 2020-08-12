package com.gavin.cloud.common.base.auth;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class JwtProperties {

    private static final Long DEFAULT_ACCESS_TOKEN_EXPIRES = TimeUnit.MINUTES.toSeconds(5);
    private static final Long DEFAULT_REFRESH_TOKEN_EXPIRES = TimeUnit.DAYS.toSeconds(30);

    private final Cookie cookie = new Cookie();

    private Long accessTokenExpires = DEFAULT_ACCESS_TOKEN_EXPIRES;
    private Long refreshTokenExpires = DEFAULT_REFRESH_TOKEN_EXPIRES;
    private String publicKey;

    @Data
    public static class Cookie {

        private static final String DEFAULT_PATH = "/";

        private String name;
        private String domain;
        private Integer maxAge;
        private String path = DEFAULT_PATH;
        private boolean httpOnly = true;
        private boolean secure = false;

    }

}
