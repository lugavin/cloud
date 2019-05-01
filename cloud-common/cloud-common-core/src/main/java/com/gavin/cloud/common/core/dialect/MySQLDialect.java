package com.gavin.cloud.common.core.dialect;

public class MySQLDialect extends AbstractDialect {

    private static final String LIMIT_REPLACEMENT_TEMPLATE = "SELECT * FROM (%s) row_ LIMIT %d, %d";

    @Override
    protected String getLimitQueryString(String sql, int offset, int pageSize) {
        return String.format(LIMIT_REPLACEMENT_TEMPLATE, sql, offset, pageSize);
    }

}
