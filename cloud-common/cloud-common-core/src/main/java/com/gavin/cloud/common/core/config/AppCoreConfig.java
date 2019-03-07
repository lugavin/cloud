package com.gavin.cloud.common.core.config;

import com.gavin.cloud.common.core.plugin.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see org.mybatis.spring.annotation.MapperScan
 * @see org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
 */
@Configuration
public class AppCoreConfig {

    @Bean
    public Interceptor getInterceptor() {
        return new PageInterceptor();
    }

}

