package com.gavin.cloud.common.core.dialect;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @see org.apache.ibatis.type.JdbcType
 */
public enum Database {

    H2("H2"),
    DB2("DB2"),
    DERBY("Derby"),
    MYSQL("MySQL"),
    ORACLE("Oracle"),
    POSTGRESQL("PostgreSQL"),
    SQLSERVER("MS-SQL", "sqlserver");

    private final String type;
    private final String alias;

    Database(String type) {
        this(type, type.toLowerCase());
    }

    Database(String type, String alias) {
        this.type = type;
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    private static final Map<String, Database> MAP = Arrays.stream(Database.values())
            .collect(Collectors.toMap(Database::getType, Function.identity()));

    public static Database fromType(final String productName) {
        return MAP.get(Objects.requireNonNull(productName));
    }

}
