package com.gavin.cloud.config.properties;

import lombok.Data;

@Data
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
