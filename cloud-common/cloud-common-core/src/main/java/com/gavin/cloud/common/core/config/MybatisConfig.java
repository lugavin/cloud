package com.gavin.cloud.common.core.config;

import com.gavin.cloud.common.core.dialect.Database;
import com.gavin.cloud.common.core.dialect.MySQLDialect;
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

    /**
     * @see <a href="http://www.mybatis.org/mybatis-3/zh/dynamic-sql.html">Multi-db vendor support</a>
     */
    @Bean
    @ConditionalOnMissingBean
    DatabaseIdProvider databaseIdProvider() {
        Map<String, String> productNames = Arrays.stream(Database.values())
                .collect(Collectors.toMap(Database::getType, Database::getAlias));
        Properties p = new Properties();
        p.putAll(productNames);
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        databaseIdProvider.setProperties(p);
        return databaseIdProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    Interceptor pageInterceptor() {
        return new PageInterceptor(new MySQLDialect());
    }

}

