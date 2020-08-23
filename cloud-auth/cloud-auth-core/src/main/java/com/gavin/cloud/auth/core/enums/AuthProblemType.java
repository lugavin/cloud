package com.gavin.cloud.auth.core.enums;

import com.gavin.cloud.common.base.problem.StatusType;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.ProblemType;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.HttpStatus.BAD_REQUEST;

public enum AuthProblemType implements ProblemType {

    ACCOUNT_NOT_FOUND_TYPE(BAD_REQUEST, "Account was not registered", "/account-not-found"),
    ACCOUNT_NOT_ACTIVATED_TYPE(BAD_REQUEST, "Account was not activated", "/account-not-activated"),
    INVALID_PASSWORD_TYPE(BAD_REQUEST, "Incorrect password", "/invalid-password");

    private static final String DEFAULT_TYPE = "/problem-with-message";

    private final StatusType status;
    private final String title;
    private final URI type;

    AuthProblemType(StatusType status, String title) {
        this(status, title, DEFAULT_TYPE);
    }

    AuthProblemType(StatusType status, String title, String type) {
        this(status, title, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    AuthProblemType(StatusType status, String title, URI type) {
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
