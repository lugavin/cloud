package com.gavin.cloud.common.base.auth;

import com.gavin.cloud.common.base.util.KeyHelper;
import lombok.Data;

import java.security.PublicKey;

@Data
public class JwtProperties {

    public static final Integer DEFAULT_VALIDITY_IN_SECONDS = 1800;
    public static final String DEFAULT_COOKIE_PATH = "/";

    private PublicKey publicKey;
    private Integer validityInSeconds = DEFAULT_VALIDITY_IN_SECONDS;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath = DEFAULT_COOKIE_PATH;
    private Integer cookieMaxAge;
    private boolean useHttpOnlyCookie = true;
    private boolean useSecureCookie = false;

    public void setPublicKey(String publicKey) {
        this.publicKey = KeyHelper.createPublicKey(publicKey);
    }

}
