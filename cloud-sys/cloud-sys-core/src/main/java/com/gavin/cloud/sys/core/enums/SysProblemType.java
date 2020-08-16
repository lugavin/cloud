package com.gavin.cloud.sys.core.enums;

import com.gavin.cloud.common.base.exception.HttpStatus;
import com.gavin.cloud.common.base.exception.Problem;
import com.gavin.cloud.common.base.exception.ProblemType;

import java.net.URI;

import static com.gavin.cloud.common.base.exception.HttpStatus.*;

public enum SysProblemType implements ProblemType {

    USER_NOT_FOUND_TYPE(BAD_REQUEST, "No user was found", "/user-not-found"),
    EMAIL_NOT_FOUND_TYPE(BAD_REQUEST, "Email address not registered", "/email-not-found"),

    ;

    private static final String DEFAULT_TYPE = "/problem-with-message";

    private final HttpStatus status;
    private final String title;
    private final URI type;

    SysProblemType(HttpStatus status, String title) {
        this(status, title, DEFAULT_TYPE);
    }

    SysProblemType(HttpStatus status, String title, String type) {
        this(status, title, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    SysProblemType(HttpStatus status, String title, URI type) {
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
