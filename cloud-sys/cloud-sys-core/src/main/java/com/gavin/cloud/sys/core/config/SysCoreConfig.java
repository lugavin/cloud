package com.gavin.cloud.sys.core.config;

import com.gavin.cloud.sys.core.config.properties.OssProperties;
import com.gavin.cloud.sys.core.config.properties.SftpProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SysCoreConfig {

    @Bean
    @ConfigurationProperties("ftp")
    public SftpProperties sftpProperties() {
        return new SftpProperties();
    }

    @Bean
    @ConfigurationProperties("oss")
    public OssProperties ossProperties() {
        return new OssProperties();
    }

}
