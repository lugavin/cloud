package com.gavin.cloud.common.base.problem;

import java.util.Map;

public class AppBizException extends AbstractThrowableProblem {

    private final ProblemType problemType;

    public AppBizException(ProblemType problemType) {
        this(problemType, null);
    }

    public AppBizException(ProblemType problemType, String detail) {
        this(problemType, detail, null);
    }

    public AppBizException(ProblemType problemType, String detail, ThrowableProblem cause) {
        this(problemType, detail, cause, null);
    }

    public AppBizException(ProblemType problemType, String detail, ThrowableProblem cause, Map<String, Object> parameters) {
        super(problemType.getType(), problemType.getStatus(), problemType.getTitle(), detail, cause, parameters);
        this.problemType = problemType;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

}
