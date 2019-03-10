package com.gavin.cloud;

import com.gavin.cloud.test.AppWebProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AppWebProperties.class)
public class TestApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TestApp.class, args);
        log.info("====== {} ======", context.getBean(AppWebProperties.class));
    }

}
