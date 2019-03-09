package com.gavin.cloud.common.core.config;

import com.gavin.cloud.common.core.plugin.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see org.mybatis.spring.annotation.MapperScan
 * @see org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
public class AppCoreConfig {

    @Bean
    @ConditionalOnMissingBean
    public Interceptor pageInterceptor() {
        return new PageInterceptor();
    }

}

