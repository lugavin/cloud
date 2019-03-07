package com.gavin.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

@SpringBootApplication
@EnableZipkinStreamServer
public class TraceApp {

    public static void main(String[] args) {
        SpringApplication.run(TraceApp.class, args);
    }

}
