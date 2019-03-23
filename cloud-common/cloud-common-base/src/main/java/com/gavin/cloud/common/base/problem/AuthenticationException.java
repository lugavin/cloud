package com.gavin.cloud.common.base.problem;

public class AuthenticationException extends AbstractThrowableProblem {

    public AuthenticationException(String message) {
        super(DEFAULT_TYPE, message, Status.UNAUTHORIZED);
    }

}
