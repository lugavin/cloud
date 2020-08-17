package com.gavin.cloud.sys.core.enums;

/**
 * @see <a href="https://developer.aliyun.com/article/531067">阿里云Redis开发规范</a>
 */
public enum RedisKey {

    ROLE_PERMS,
    ROLE_MUTEX,

    ;

    private static final String SERVICE_PREFIX = "sys";

    public String getKey(String value) {
        return String.join(":", SERVICE_PREFIX, this.name().replace('_', ':').toLowerCase(), value);
    }

}
