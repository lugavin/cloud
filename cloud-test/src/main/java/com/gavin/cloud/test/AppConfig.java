package com.gavin.cloud.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public ServiceA serviceA() {
        ServiceA serviceA = new ServiceA();
        log.info("====== {} ======", serviceA);
        return serviceA;
    }

    @Bean
    public ServiceB serviceB() {
        ServiceA serviceA = serviceA();
        log.info("------ {} ------", serviceA);
        return new ServiceB(serviceA);
    }

}
