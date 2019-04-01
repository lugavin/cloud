package com.gavin.cloud.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Service
public class ServiceB {

    @Autowired
    private ServiceA serviceA;

    public ServiceB() {
        Assert.isNull(serviceA, "serviceA must be null");
    }

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(serviceA, "serviceA must not be null");

    }

}
