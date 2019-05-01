package com.gavin.cloud.common.core.dialect;

public interface Dialect {

    String getLimitString(String sql, int offset, int pageSize);

    String getCountString(String sql);

}
