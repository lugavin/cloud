package com.gavin.cloud.common.core.dialect;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Database {

    ORACLE(new OracleDialect()),
    MYSQL(new MySQLDialect());

    private final Dialect dialect;

    Database(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect() {
        return dialect;
    }

    private static final Map<String, Database> MAP = Arrays.stream(Database.values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Database fromType(final String type) {
        return Optional.ofNullable(MAP.get(type.toUpperCase()))
                .orElseThrow(() -> new RuntimeException("Unsupported database type"));
    }

}
