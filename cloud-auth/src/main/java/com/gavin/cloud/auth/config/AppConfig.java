package com.gavin.cloud.auth.config;

import com.gavin.cloud.auth.config.properties.JwtExtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.jwt", ignoreUnknownFields = false)
    public JwtExtProperties jwtProperties() {
        return new JwtExtProperties();
    }

}
