package com.gavin.cloud.auth.core.config.properties;

import com.gavin.cloud.common.base.auth.JwtProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JwtExtProperties extends JwtProperties {

    private String privateKey;

}
