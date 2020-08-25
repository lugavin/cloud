package com.gavin.cloud.sys.core.config;

import com.gavin.cloud.sys.core.config.properties.OssProperties;
import com.gavin.cloud.sys.core.config.properties.SftpProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SysCoreConfig {

    @Bean
    @ConfigurationProperties("ftp")
    SftpProperties sftpProperties() {
        return new SftpProperties();
    }

    @Bean
    @ConfigurationProperties("oss")
    OssProperties ossProperties() {
        return new OssProperties();
    }

}
