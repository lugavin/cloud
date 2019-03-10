package com.gavin.cloud.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Data
@ConfigurationProperties(prefix = "app.auth", ignoreUnknownFields = false)
public class AuthProperties {

    private byte[] privateKey;

    public void setPrivateKey(String privateKey) {
        this.privateKey = Base64.getUrlDecoder().decode(privateKey);
    }

}
