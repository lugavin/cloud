package com.gavin.cloud.config;

import com.gavin.cloud.config.properties.AppProperties;
import com.gavin.cloud.test.ServiceA;
import com.gavin.cloud.test.ServiceB;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties("app")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }

}
