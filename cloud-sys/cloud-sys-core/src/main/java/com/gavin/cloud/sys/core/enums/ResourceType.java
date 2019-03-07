package com.gavin.cloud.sys.core.enums;

/**
 * 枚举类:
 * (1)枚举的构造函数为私有的
 * (2)可以把枚举类型当作构造函数为私有的类
 */
public enum ResourceType {

    MENU, FUNC;

    /*
    private static Map<String, ResourceType> types = new HashMap<>();

    static {
        for (ResourceType resourceType : ResourceType.values()) {
            types.put(resourceType.name(), resourceType);
        }
    }

    public static ResourceType forType(String type) {
        return types.get(type);
    }
    */

}
