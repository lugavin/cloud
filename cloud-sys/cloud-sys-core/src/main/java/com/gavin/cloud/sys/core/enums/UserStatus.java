package com.gavin.cloud.sys.core.enums;

public enum UserStatus {

    LOCKED("0"), UNLOCKED("1");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
