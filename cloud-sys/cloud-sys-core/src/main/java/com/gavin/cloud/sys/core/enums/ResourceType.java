package com.gavin.cloud.sys.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 枚举类:
 * (1)枚举的构造函数为私有的
 * (2)可以把枚举类型当作构造函数为私有的类
 */
public enum ResourceType {

    MENU, FUNC;

    private static final Map<String, ResourceType> MAP = Arrays.stream(ResourceType.values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static ResourceType forType(String type) {
        return MAP.get(Objects.requireNonNull(type).toUpperCase());
    }

}
