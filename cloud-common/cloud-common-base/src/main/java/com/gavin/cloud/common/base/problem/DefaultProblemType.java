package com.gavin.cloud.common.base.problem;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.HttpStatus.*;

public enum DefaultProblemType implements ProblemType {

    INTERNAL_SERVER_ERROR_TYPE(INTERNAL_SERVER_ERROR),
    AUTHENTICATION_FAILED_TYPE(UNAUTHORIZED),
    AUTHORIZATION_FAILED_TYPE(FORBIDDEN),
    CONSTRAINT_VIOLATION_TYPE(BAD_REQUEST, "Parameter verification failed"),

    ;

    private static final String DEFAULT_TYPE = "/problem-with-message";

    private final StatusType status;
    private final String title;
    private final URI type;

    DefaultProblemType(StatusType status) {
        this(status, status.getReasonPhrase());
    }

    DefaultProblemType(StatusType status, String title) {
        this(status, title, DEFAULT_TYPE);
    }

    DefaultProblemType(StatusType status, String title, String type) {
        this(status, title, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    DefaultProblemType(StatusType status, String title, URI type) {
        this.status = status;
        this.title = title;
        this.type = type;
    }

    @Override
    public URI getType() {
        return type;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public String getTitle() {
        return title;
    }

}
