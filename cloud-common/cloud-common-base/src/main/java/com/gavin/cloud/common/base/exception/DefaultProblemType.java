package com.gavin.cloud.common.base.exception;

import java.net.URI;

import static com.gavin.cloud.common.base.exception.HttpStatus.*;

public enum DefaultProblemType implements ProblemType {

    INTERNAL_SERVER_ERROR_TYPE(INTERNAL_SERVER_ERROR),
    AUTHENTICATION_FAILED_TYPE(UNAUTHORIZED),
    AUTHORIZATION_FAILED_TYPE(FORBIDDEN),
    CONSTRAINT_VIOLATION_TYPE(BAD_REQUEST, "Parameter verification failed"),

    ;

    private static final String DEFAULT_TYPE = "/problem-with-message";

    private final HttpStatus status;
    private final String title;
    private final URI type;

    DefaultProblemType(HttpStatus status) {
        this(status, status.reason());
    }

    DefaultProblemType(HttpStatus status, String title) {
        this(status, title, DEFAULT_TYPE);
    }

    DefaultProblemType(HttpStatus status, String title, String type) {
        this(status, title, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    DefaultProblemType(HttpStatus status, String title, URI type) {
        this.status = status;
        this.title = title;
        this.type = type;
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

}
