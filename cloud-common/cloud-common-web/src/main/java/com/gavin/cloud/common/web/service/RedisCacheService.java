package com.gavin.cloud.common.web.service;

import com.gavin.cloud.common.base.cache.CacheService;
import com.gavin.cloud.common.base.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisCacheService implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheService.class);

    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("缓存%s时被中断操作 ", key), e);
            return false;
        }
    }

    @Override
    public boolean set(String key, String value, long maxAge) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, maxAge, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("缓存%s时被中断操作 ", key), e);
            return false;
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = get(key);
        return StringUtils.isNotBlank(value) ? JsonUtils.fromJson(value, clazz) : null;
    }

    @Override
    public <T> boolean set(String key, T dto) {
        return set(key, JsonUtils.toJson(dto));
    }

    @Override
    public <T> boolean set(String key, T dto, long maxAge) {
        return set(key, JsonUtils.toJson(dto), maxAge);
    }

    @Override
    public boolean delete(String key) {
        try {
            stringRedisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("删除缓存%s时被中断操作 ", key), e);
            return false;
        }
    }

    @Override
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public boolean setExpire(String key, long maxAge) {
        return stringRedisTemplate.expire(key, maxAge, TimeUnit.SECONDS);
    }

}
