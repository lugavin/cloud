package com.gavin.cloud;

import com.gavin.cloud.retry.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

@Slf4j
@EnableRetry
@SpringBootApplication
public class CloudApp {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(CloudApp.class, args);
        RemoteService remoteService = context.getBean(RemoteService.class);
        remoteService.call();
    }

}
