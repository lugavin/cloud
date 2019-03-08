package com.gavin.cloud.auth.enums;

import com.gavin.cloud.common.base.exception.MessageType;
import com.gavin.cloud.common.base.exception.Status;

public enum AuthMessageType implements MessageType {

    ERR_INVALID_PASSWORD(Status.BAD_REQUEST, "密码不正确"),
    ERR_ACCOUNT_NOT_FOUND(Status.BAD_REQUEST, "账号不存在"),
    ERR_ACCOUNT_NOT_ACTIVATED(Status.BAD_REQUEST, "账号未激活"),
    ERR_UNAUTHORIZED(Status.BAD_REQUEST, "未认证");

    private final Status status;
    private final String phrase;

    AuthMessageType(Status status, String phrase) {
        this.status = status;
        this.phrase = phrase;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getPhrase() {
        return phrase;
    }
}
