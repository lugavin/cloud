package com.gavin.cloud.common.core.dialect;

public interface DialectHandler {

    boolean supportsType(Database database);

    String getLimitString(String sql, int offset, int pageSize);

    String getCountString(String sql);

}
