package com.gavin.cloud.common.core.dialect;

public class MySQLDialect implements Dialect {

    private static final String LIMIT_REPLACEMENT_TEMPLATE = "SELECT * FROM (%s) row_ LIMIT %d, %d";

    @Override
    public String getLimitQueryString(String sql, int page, int pageSize) {
        return String.format(LIMIT_REPLACEMENT_TEMPLATE, sql, (Math.max(page, 1) - 1) * pageSize, pageSize);
    }

}
