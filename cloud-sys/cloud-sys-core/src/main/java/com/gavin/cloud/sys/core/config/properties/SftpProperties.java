package com.gavin.cloud.sys.core.config.properties;

import lombok.Data;

@Data
public class SftpProperties {

    private String username;
    private String password;
    private String host;
    private Integer port;
    private Integer timeout;
    private String basePath;

}
