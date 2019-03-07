package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.web.service.RedisCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisTemplate并不支持直接存储对象, 解决方案:
 * (1)自己实现{@link RedisSerializer}接口来对传入对象进行序列化和反序列化操作
 * (2)先将对象转为JSON字符串再存储
 *
 * @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.RedisConfiguration
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @Primary
    public RedisCacheService redisCacheService(StringRedisTemplate stringRedisTemplate) {
        return new RedisCacheService(stringRedisTemplate);
    }

}
