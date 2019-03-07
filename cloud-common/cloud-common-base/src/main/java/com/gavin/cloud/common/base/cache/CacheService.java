package com.gavin.cloud.common.base.cache;

public interface CacheService {

    String get(String key);

    boolean set(String key, String value);

    boolean set(String key, String value, long maxAge);

    <T> T get(String key, Class<T> clazz);

    <T> boolean set(String key, T dto);

    <T> boolean set(String key, T dto, long maxAge);

    boolean delete(String key);

    boolean hasKey(String key);

    long getExpire(String key);

    boolean setExpire(String key, long maxAge);

}
