package com.gavin.cloud.common.core.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Dialect {

    String COUNT_REPLACEMENT_TEMPLATE = "SELECT COUNT(1) %s";
    Pattern ORDER_BY_PATTERN = Pattern.compile("\\s+ORDER\\s+BY\\s+.*", Pattern.CASE_INSENSITIVE);

    String getLimitQueryString(String sql, int page, int pageSize);

    /**
     * @param sql      select sql
     * @param page     current page
     * @param pageSize page size
     * @return count sql
     */
    default String getLimitString(String sql, int page, int pageSize) {
        if (sql == null || sql.length() == 0) {
            throw new IllegalArgumentException("The argument [sql] is required and it must not be null.");
        }
        return getLimitQueryString(sql, page, pageSize);
    }

    default String getCountString(String sql) {
        if (sql == null || sql.length() == 0) {
            throw new IllegalArgumentException("The argument [sql] is required and it must not be null.");
        }
        return String.format(COUNT_REPLACEMENT_TEMPLATE, removeSelect(removeOrderBy(sql)));
    }


    /**
     * Remove select clause
     */
    static String removeSelect(String sql) {
        return sql.substring(sql.toUpperCase().indexOf("FROM"));
    }

    /**
     * Remove Order By clause
     */
    static String removeOrderBy(String sql) {
        Matcher matcher = ORDER_BY_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
