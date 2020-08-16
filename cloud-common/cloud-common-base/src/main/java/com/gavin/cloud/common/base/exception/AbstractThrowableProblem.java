package com.gavin.cloud.common.base.exception;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractThrowableProblem extends ThrowableProblem {

    private final URI type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final Map<String, Object> parameters;

    protected AbstractThrowableProblem(final URI type,
                                       final HttpStatus status,
                                       final String title) {
        this(type, status, title, null);
    }

    protected AbstractThrowableProblem(final URI type,
                                       final HttpStatus status,
                                       final String title,
                                       final String detail) {
        this(type, status, title, detail, null);
    }

    protected AbstractThrowableProblem(final URI type,
                                       final HttpStatus status,
                                       final String title,
                                       final String detail,
                                       final ThrowableProblem cause) {
        this(type, status, title, detail, cause, null);
    }

    protected AbstractThrowableProblem(final URI type,
                                       final HttpStatus status,
                                       final String title,
                                       final String detail,
                                       final ThrowableProblem cause,
                                       final Map<String, Object> parameters) {
        super(cause);
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.parameters = Optional.ofNullable(parameters).orElseGet(LinkedHashMap::new);
    }

    @Override
    public URI getType() {
        return type;
    }

    @Override
    public HttpStatus getStatus() {
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
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

}