package com.gavin.cloud.common.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @see org.springframework.session.MapSession
 * @see org.springframework.session.web.http.HeaderHttpSessionStrategy
 * @see org.springframework.session.web.http.CookieHttpSessionStrategy
 * @see org.springframework.session.data.redis.RedisSessionExpirationPolicy
 * @see org.springframework.session.data.redis.RedisOperationsSessionRepository
 * @see <a href="https://docs.spring.io/spring-session/docs/1.3.2.RELEASE/reference/html5/">Document</a>
 */
@Configuration
@EnableRedisHttpSession
public class RedisHttpSessionConfig {

    /**
     * @see RedisHttpSessionConfiguration#setDefaultRedisSerializer
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    // @Bean
    // public HttpSessionStrategy httpSessionStrategy() {
    //     return new HeaderHttpSessionStrategy();
    // }

    @Bean
    @ConfigurationProperties("app.cookie")
    public CookieSerializer cookieSerializer() {
        return new DefaultCookieSerializer();
    }

}
