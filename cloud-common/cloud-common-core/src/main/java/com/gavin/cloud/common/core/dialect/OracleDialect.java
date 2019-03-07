package com.gavin.cloud.common.core.dialect;

public class OracleDialect implements Dialect {

    private static final String LIMIT_REPLACEMENT_TEMPLATE = "SELECT * FROM (SELECT ROWNUM rownum_, row_.* FROM (%s) row_ WHERE ROWNUM <= %d) WHERE rownum_ > %d";

    @Override
    public String getLimitQueryString(String sql, int page, int pageSize) {
        return String.format(LIMIT_REPLACEMENT_TEMPLATE, sql, Math.max(page, 1) * pageSize, (Math.max(page, 1) - 1) * pageSize);
    }

}
