package com.gavin.cloud.sys.core.enums;

public enum RoleStatus {

    UNAVAILABLE("0"), AVAILABLE("1");

    private final String value;

    RoleStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
