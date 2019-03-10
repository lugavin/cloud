package com.gavin.cloud.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.web")
public class AppWebProperties {

    private String contactName;
    private String contactUrl;

}
