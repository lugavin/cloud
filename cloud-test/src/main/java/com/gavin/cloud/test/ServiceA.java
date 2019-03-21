package com.gavin.cloud.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
public class ServiceA {

    @Autowired
    private ServiceB serviceB;

    public ServiceA() {
        log.info("====== {} ======", serviceB); // null
    }

    @PostConstruct
    public void afterPropertiesSet() {
        log.info("====== {} ======", serviceB); // NonNull
    }

}
