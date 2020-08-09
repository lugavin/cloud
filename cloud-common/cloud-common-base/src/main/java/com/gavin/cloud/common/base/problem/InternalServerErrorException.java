package com.gavin.cloud.common.base.problem;

/**
 * Simple exception with a message, that returns an Internal Server Error code.
 *
 * @see <a href="https://docs.nestjs.com/exception-filters#base-exceptions">Built-in HTTP exceptions</a>
 */
public class InternalServerErrorException extends AbstractThrowableProblem {

    public InternalServerErrorException(String message) {
        super(ProblemType.DEFAULT_TYPE, message, Status.INTERNAL_SERVER_ERROR);
    }

}