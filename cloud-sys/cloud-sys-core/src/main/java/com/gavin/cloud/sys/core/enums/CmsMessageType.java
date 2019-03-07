package com.gavin.cloud.sys.core.enums;

import com.gavin.cloud.common.base.exception.MessageType;
import com.gavin.cloud.common.base.exception.Status;

public enum CmsMessageType implements MessageType {

    ERR_UPLOAD_PICTURE(Status.INTERNAL_SERVER_ERROR, "图片上传失败"),
    ERR_ACCOUNT_ALREADY_USED(Status.BAD_REQUEST, "账号已存在"),
    ERR_ROLE_ALREADY_USED(Status.BAD_REQUEST, "角色已存在");

    private final Status status;
    private final String phrase;

    CmsMessageType(Status status, String phrase) {
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
