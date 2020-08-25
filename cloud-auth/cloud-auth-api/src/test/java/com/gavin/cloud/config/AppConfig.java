package com.gavin.cloud.config;

import com.gavin.cloud.config.properties.GitHubProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConfigurationProperties("api.github")
    GitHubProperties gitHubProperties() {
        return new GitHubProperties();
    }

}
