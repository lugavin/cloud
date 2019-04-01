package com.gavin.cloud.common.core.dialect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class DialectHandlerFactory {

    private static List<DialectHandler> dialectHandlers = Arrays.asList(
            new MySQLDialectHandler(),
            new OracleDialectHandler()
    );

    public static List<DialectHandler> getDialectHandler() {
        return Collections.unmodifiableList(dialectHandlers);
    }

}