package com.gavin.cloud.sys.api.enums;

public enum LoginType {

    USERNAME(1), PHONE(2), EMAIL(3);

    private final int value;

    LoginType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static LoginType fromType(int type) {
        for (LoginType loginType : LoginType.values()) {
            if (loginType.value() == type) {
                return loginType;
            }
        }
        return null;
    }

}
