package com.gavin.cloud.common.base.problem;

import java.util.Collections;
import java.util.Map;

public final class DefaultProblem implements Exceptional {

    private final Status status;
    private final String title;
    private final String detail;
    private final Exceptional cause;
    private final Map<String, Object> parameters;

    DefaultProblem(Status status, String title, String detail, Exceptional cause, Map<String, Object> parameters) {
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.cause = cause;
        this.parameters = parameters;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public Exceptional getCause() {
        return cause;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

}