package com.gavin.cloud.common.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Data
@ConfigurationProperties(prefix = "app.jwt", ignoreUnknownFields = false)
public class JwtProperties {

    public static final Integer DEFAULT_VALIDITY_IN_SECONDS = 1800;
    public static final String DEFAULT_COOKIE_PATH = "/";

    private byte[] publicKey;
    private Integer validityInSeconds = DEFAULT_VALIDITY_IN_SECONDS;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath = DEFAULT_COOKIE_PATH;
    private Integer cookieMaxAge;
    private boolean cookieSecure = false;
    private boolean cookieHttpOnly = false;

    public void setPublicKey(String publicKey) {
        this.publicKey = Base64.getUrlDecoder().decode(publicKey);
    }

}
