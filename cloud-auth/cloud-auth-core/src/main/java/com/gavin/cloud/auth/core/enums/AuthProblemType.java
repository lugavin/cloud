package com.gavin.cloud.auth.core.enums;

import com.gavin.cloud.common.base.exception.HttpStatus;
import com.gavin.cloud.common.base.exception.Problem;
import com.gavin.cloud.common.base.exception.ProblemType;

import java.net.URI;

import static com.gavin.cloud.common.base.exception.HttpStatus.BAD_REQUEST;

public enum AuthProblemType implements ProblemType {

    ACCOUNT_NOT_FOUND_TYPE(BAD_REQUEST, "Account was not registered", "/account-not-found"),
    ACCOUNT_NOT_ACTIVATED_TYPE(BAD_REQUEST, "Account was not activated", "/account-not-activated"),
    INVALID_PASSWORD_TYPE(BAD_REQUEST, "Incorrect password", "/invalid-password");

    private static final String DEFAULT_TYPE = "/problem-with-message";

    private final HttpStatus status;
    private final String title;
    private final URI type;

    AuthProblemType(HttpStatus status, String title) {
        this(status, title, DEFAULT_TYPE);
    }

    AuthProblemType(HttpStatus status, String title, String type) {
        this(status, title, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    AuthProblemType(HttpStatus status, String title, URI type) {
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
