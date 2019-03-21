package com.gavin.cloud.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app")
public class AppProperties {

    private final Zookeeper zookeeper = new Zookeeper();

    private String contactName;
    private String contactUrl;

    @Data
    public static class Zookeeper {
        private String address;
        private String root;
    }

}
