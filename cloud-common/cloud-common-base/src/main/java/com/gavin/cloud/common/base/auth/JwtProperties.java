package com.gavin.cloud.common.base.auth;

import com.gavin.cloud.common.base.util.KeyHelper;
import lombok.Data;

import java.security.PublicKey;

@Data
public class JwtProperties {

    private static final Integer DEFAULT_ACCESS_TOKEN_EXPIRES = 5 * 60;
    private static final Integer DEFAULT_REFRESH_TOKEN_EXPIRES = 30 * 24 * 60 * 60;
    private static final String DEFAULT_COOKIE_PATH = "/";

    private Integer accessTokenExpires = DEFAULT_ACCESS_TOKEN_EXPIRES;
    private Integer refreshTokenExpires = DEFAULT_REFRESH_TOKEN_EXPIRES;
    private PublicKey publicKey;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath = DEFAULT_COOKIE_PATH;
    private Integer cookieMaxAge;
    private boolean useHttpOnlyCookie = true;
    private boolean useSecureCookie = false;

    public void setPublicKey(String publicKeyEncoded) {
        this.publicKey = KeyHelper.createPublicKey(publicKeyEncoded);
    }

}
