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

    MYSQL("MySQL", new MySQLDialect()),
    ORACLE("Oracle", new OracleDialect()),
    //SQLSERVER("SQL Server", "sqlserver", new SQLServerDialect())
    ;

    private final String type;
    private final String alias;
    private final Dialect dialect;

    Database(String type, Dialect dialect) {
        this(type, type.toLowerCase(), dialect);
    }

    Database(String type, String alias, Dialect dialect) {
        this.type = type;
        this.alias = alias;
        this.dialect = dialect;
    }

    public String getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    public Dialect getDialect() {
        return dialect;
    }

    private static final Map<String, Database> MAP = Arrays.stream(Database.values())
            .collect(Collectors.toMap(Database::getType, Function.identity()));

    public static Database fromType(final String productName) {
        return MAP.get(Objects.requireNonNull(productName));
    }

}
