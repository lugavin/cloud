package com.gavin.cloud.auth.core.problem;

import com.gavin.cloud.common.base.problem.AbstractThrowableProblem;
import com.gavin.cloud.common.base.problem.Status;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class InvalidPasswordException extends AbstractThrowableProblem {

    private static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");

    public InvalidPasswordException() {
        super(INVALID_PASSWORD_TYPE, "Incorrect password", Status.BAD_REQUEST);
    }

}
