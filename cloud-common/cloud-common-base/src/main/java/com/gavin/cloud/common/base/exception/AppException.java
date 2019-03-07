package com.gavin.cloud.common.base.exception;

import java.util.Map;

public class AppException extends AbstractException {

    public AppException(final MessageType messageType) {
        this(messageType, null);
    }

    public AppException(final MessageType messageType,
                        final String detail) {
        this(messageType, detail, null);
    }

    public AppException(final MessageType messageType,
                        final String detail,
                        final Throwable cause) {
        this(messageType, detail, cause, null);
    }

    public AppException(final MessageType messageType,
                        final String detail,
                        final Throwable cause,
                        final Map<String, Object> parameters) {
        this(messageType.getStatus(), messageType.getPhrase(), detail, cause, parameters);
    }

    public AppException(final Status status,
                        final String title,
                        final String detail,
                        final Throwable cause,
                        final Map<String, Object> parameters) {
        super(status, title, detail, cause, parameters);
    }
}
