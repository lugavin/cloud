package com.gavin.cloud.common.base.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public abstract class AbstractException extends RuntimeException implements Exceptional {

    private final Status status;
    private final String title;
    private final String detail;
    private final Map<String, Object> parameters;

    protected AbstractException(final Status status,
                                final String title) {
        this(status, title, null);
    }

    protected AbstractException(final Status status,
                                final String title,
                                final String detail) {
        this(status, title, detail, null);
    }

    protected AbstractException(final Status status,
                                final String title,
                                final String detail,
                                final Throwable cause) {
        this(status, title, detail, cause, null);
    }

    protected AbstractException(final Status status,
                                final String title,
                                final String detail,
                                final Throwable cause,
                                final Map<String, Object> parameters) {
        super(cause);
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.parameters = Optional.ofNullable(parameters).orElseGet(LinkedHashMap::new);
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
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public String getMessage() {
        return Stream.of(title, detail)
                .filter(Objects::nonNull)
                .collect(joining(": "));
    }
}
