package com.gavin.cloud.sys.core.enums;

import com.gavin.cloud.common.base.problem.StatusType;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.ProblemType;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.HttpStatus.*;

public enum SysProblemType implements ProblemType {

    USER_NOT_FOUND_TYPE(BAD_REQUEST, "No user was found", "/user-not-found"),
    ROLE_NOT_FOUND_TYPE(BAD_REQUEST, "No role was found", "/role-not-found"),
    EMAIL_NOT_FOUND_TYPE(BAD_REQUEST, "Email address not registered", "/email-not-found"),

    ;

    private static final String DEFAULT_TYPE = "/problem-with-message";

    private final StatusType status;
    private final String title;
    private final URI type;

    SysProblemType(StatusType status, String title) {
        this(status, title, DEFAULT_TYPE);
    }

    SysProblemType(StatusType status, String title, String type) {
        this(status, title, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    SysProblemType(StatusType status, String title, URI type) {
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
