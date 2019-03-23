package com.gavin.cloud.common.base.problem;

public class AccessDeniedException extends AbstractThrowableProblem {

    public AccessDeniedException(String message) {
        super(DEFAULT_TYPE, message, Status.FORBIDDEN);
    }

}
