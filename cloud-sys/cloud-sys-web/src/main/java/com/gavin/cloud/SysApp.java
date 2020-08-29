package com.gavin.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SysApp {

    public static void main(String[] args) {
        SpringApplication.run(SysApp.class, args);
    }

}
