package com.gavin.cloud.sys.core.enums;

public enum PermissionStatus {

    UNAVAILABLE("0"), AVAILABLE("1");

    private final String value;

    PermissionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
