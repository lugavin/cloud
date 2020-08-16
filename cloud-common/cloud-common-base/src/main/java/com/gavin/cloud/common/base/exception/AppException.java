package com.gavin.cloud.common.base.exception;

import java.util.Map;

public class AppException extends AbstractThrowableProblem {

    public AppException(ProblemType problemType) {
        this(problemType, null);
    }

    public AppException(ProblemType problemType, String detail) {
        this(problemType, detail, null);
    }

    public AppException(ProblemType problemType, String detail, ThrowableProblem cause) {
        this(problemType, detail, cause, null);
    }

    public AppException(ProblemType problemType, String detail, ThrowableProblem cause, Map<String, Object> parameters) {
        super(problemType.getType(), problemType.getStatus(), problemType.getTitle(), detail, cause, parameters);
    }

}
