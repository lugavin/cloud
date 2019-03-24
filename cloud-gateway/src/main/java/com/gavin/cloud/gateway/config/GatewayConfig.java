package com.gavin.cloud.gateway.config;

import com.gavin.cloud.common.base.auth.AuthProperties;
import com.gavin.cloud.common.base.auth.JwtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GatewayConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.auth", ignoreUnknownFields = false)
    public AuthProperties authProperties() {
        return new AuthProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.jwt", ignoreUnknownFields = false)
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

}
