package com.gavin.cloud.common.base.exception;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ExceptionalBuilder {

    private static final Set<String> RESERVED_PROPERTIES = new HashSet<>(Arrays.asList(
            "status", "title", "detail", "cause"
    ));

    private Status status;
    private String title;
    private String detail;
    private Throwable cause;

    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public ExceptionalBuilder withStatus(final Status status) {
        this.status = status;
        return this;
    }

    public ExceptionalBuilder withTitle(final String title) {
        this.title = title;
        return this;
    }

    public ExceptionalBuilder withDetail(final String detail) {
        this.detail = detail;
        return this;
    }

    public ExceptionalBuilder withCause(final Throwable cause) {
        this.cause = cause;
        return this;
    }

    public ExceptionalBuilder with(final String key, final Object value) throws IllegalArgumentException {
        if (RESERVED_PROPERTIES.contains(key)) {
            throw new IllegalArgumentException("Property " + key + " is reserved");
        }
        parameters.put(key, value);
        return this;
    }

    public Exceptional build() {
        return new AppException(status, title, detail, cause, new LinkedHashMap<>(parameters));
    }

    public static ExceptionalBuilder newInstance() {
        return new ExceptionalBuilder();
    }

}
