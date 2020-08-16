package com.gavin.cloud.common.web.problem;

public class AccessDeniedException extends AbstractThrowableProblem {

    public AccessDeniedException() {
        this(Status.FORBIDDEN.getReasonPhrase());
    }

    public AccessDeniedException(String message) {
        super(DEFAULT_TYPE, message, Status.FORBIDDEN);
    }

}
