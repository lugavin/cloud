package com.gavin.cloud.mail.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.mail", ignoreUnknownFields = false)
public class MailProperties {

    private String from;
    private String baseUrl;

}
