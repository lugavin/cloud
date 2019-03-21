package com.gavin.cloud.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
public class ServiceB {

    @Autowired
    private ServiceA serviceA;

    public ServiceB() {
        log.info("====== {} ======", serviceA); // Null
    }

    @PostConstruct
    public void afterPropertiesSet() {
        log.info("====== {} ======", serviceA); // NonNull
    }

}
