package com.gavin.cloud.auth.core.config.properties;

import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.common.base.util.KeyHelper;

import java.security.PrivateKey;

public class JwtExtProperties extends JwtProperties {

    private PrivateKey privateKey;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = KeyHelper.createPrivateKey(privateKey);
    }

}
