package com.gavin.cloud.common.base.auth;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class JwtProperties {

    private static final Long DEFAULT_ACCESS_TOKEN_EXPIRES = TimeUnit.MINUTES.toSeconds(5);
    private static final Long DEFAULT_REFRESH_TOKEN_EXPIRES = TimeUnit.DAYS.toSeconds(30);
    private static final String DEFAULT_COOKIE_PATH = "/";

    private Long accessTokenExpires = DEFAULT_ACCESS_TOKEN_EXPIRES;
    private Long refreshTokenExpires = DEFAULT_REFRESH_TOKEN_EXPIRES;
    private String publicKey;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath = DEFAULT_COOKIE_PATH;
    private Integer cookieMaxAge;
    private boolean useHttpOnlyCookie = true;
    private boolean useSecureCookie = false;

}
