package com.gavin.cloud.common.base.problem;

/**
 * Simple exception with a message, that returns an Internal Server Error code.
 */
public class InternalServerErrorException extends AbstractThrowableProblem {

    public InternalServerErrorException(String message) {
        super(ProblemType.DEFAULT_TYPE, message, Status.INTERNAL_SERVER_ERROR);
    }

}