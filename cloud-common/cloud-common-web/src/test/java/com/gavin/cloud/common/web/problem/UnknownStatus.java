package com.gavin.cloud.common.web.problem;

public final class UnknownStatus implements StatusType {

    private final int statusCode;

    public UnknownStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getReasonPhrase() {
        return "Unknown";
    }

}