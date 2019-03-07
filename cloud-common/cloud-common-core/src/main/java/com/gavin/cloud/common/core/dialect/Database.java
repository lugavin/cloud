package com.gavin.cloud.common.core.dialect;

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

    public static Database fromType(final String type) {
        for (Database db : Database.values()) {
            if (db.name().equalsIgnoreCase(type)) {
                return db;
            }
        }
        return null;
    }

}
