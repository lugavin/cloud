package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.auth.AuthProperties;
import com.gavin.cloud.common.base.auth.JwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.auth", ignoreUnknownFields = false)
    public AuthProperties authProperties() {
        return new AuthProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "app.jwt", ignoreUnknownFields = false)
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    //@Bean
    //@ConfigurationProperties("app.task")
    //public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    //    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    //    pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
    //    return pool;
    //}

}
