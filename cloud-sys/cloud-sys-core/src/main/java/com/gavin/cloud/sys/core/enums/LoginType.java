package com.gavin.cloud.sys.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum LoginType {

    USERNAME(1), PHONE(2), EMAIL(3);

    private final int value;

    LoginType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    private static final Map<Integer, LoginType> MAP = Arrays.stream(LoginType.values())
            .collect(Collectors.toMap(LoginType::value, Function.identity()));

    public static LoginType fromType(int type) {
        return MAP.get(type);
    }

}
