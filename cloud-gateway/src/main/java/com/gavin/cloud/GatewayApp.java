package com.gavin.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class GatewayApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GatewayApp.class, args);
    }

}
