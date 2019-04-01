package com.gavin.cloud.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Service
public class ServiceA {

    @Autowired
    private ServiceB serviceB;

    public ServiceA() {
        Assert.isNull(serviceB, "serviceB must be null");
    }

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(serviceB, "serviceB must not be null");
    }

}
