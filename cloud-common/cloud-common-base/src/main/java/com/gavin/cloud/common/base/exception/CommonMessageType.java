package com.gavin.cloud.common.base.exception;

public enum CommonMessageType implements MessageType {

    ERR_AUTHC(Status.UNAUTHORIZED, "未认证"),
    ERR_AUTHZ(Status.FORBIDDEN, "未授权"),
    ERR_VALIDATION(Status.BAD_REQUEST, "参数校验失败"),
    ERR_BUSINESS(Status.BAD_REQUEST, "业务异常"),
    ERR_SERVER(Status.INTERNAL_SERVER_ERROR, "服务器异常"),
    ERR_DATABASE(Status.INTERNAL_SERVER_ERROR, "数据库异常"),
    ERR_UNKNOWN(Status.INTERNAL_SERVER_ERROR, "未知异常");

    private final Status status;
    private final String phrase;

    CommonMessageType(Status status, String phrase) {
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
