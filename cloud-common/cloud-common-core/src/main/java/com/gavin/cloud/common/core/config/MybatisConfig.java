package com.gavin.cloud.common.core.config;

import com.gavin.cloud.common.core.dialect.Database;
import com.gavin.cloud.common.core.plugin.PageInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @see org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
public class MybatisConfig {

    @Bean
    @ConditionalOnMissingBean
    public DatabaseIdProvider databaseIdProvider() {
        Map<String, String> productNames = Arrays.stream(Database.values())
                .collect(Collectors.toMap(Database::getType, Database::getAlias));
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties p = new Properties();
        p.putAll(productNames);
        databaseIdProvider.setProperties(p);
        return databaseIdProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public Interceptor pageInterceptor() {
        return new PageInterceptor();
    }

}
