package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.auth.AclProperties;
import com.gavin.cloud.common.base.auth.JwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.acl", ignoreUnknownFields = false)
    AclProperties aclProperties() {
        return new AclProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.jwt", ignoreUnknownFields = false)
    JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    // @Bean
    // @ConfigurationProperties("app.task")
    // ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    //     ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    //     pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
    //     return pool;
    // }

}
