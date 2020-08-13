package com.gavin.cloud.auth.core.problem;

import com.gavin.cloud.common.base.problem.AbstractThrowableProblem;
import com.gavin.cloud.common.base.problem.Status;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class AuthException extends AbstractThrowableProblem {

    private static final URI AUTH_FAILED_TYPE = URI.create(PROBLEM_BASE_URL + "/auth-failed");

    public AuthException(String title) {
        super(AUTH_FAILED_TYPE, title, Status.UNAUTHORIZED);
    }

}
