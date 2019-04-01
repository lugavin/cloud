package com.gavin.cloud.common.core.dialect;

public class MySQLDialectHandler extends AbstractDialectHandler {

    private static final String LIMIT_REPLACEMENT_TEMPLATE = "SELECT * FROM (%s) row_ LIMIT %d, %d";

    @Override
    public boolean supportsType(Database database) {
        return Database.MYSQL == database;
    }

    @Override
    public String getLimitQueryString(String sql, int offset, int pageSize) {
        return String.format(LIMIT_REPLACEMENT_TEMPLATE, sql, offset, pageSize);
    }

}
