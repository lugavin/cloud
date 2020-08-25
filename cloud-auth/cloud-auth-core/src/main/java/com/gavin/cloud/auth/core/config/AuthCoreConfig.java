package com.gavin.cloud.auth.core.config;

import com.gavin.cloud.auth.core.config.properties.JwtExtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
class AuthCoreConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.jwt", ignoreUnknownFields = false)
    JwtExtProperties jwtProperties() {
        return new JwtExtProperties();
    }

}
