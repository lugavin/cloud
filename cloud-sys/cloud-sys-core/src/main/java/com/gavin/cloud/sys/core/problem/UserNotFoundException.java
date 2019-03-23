package com.gavin.cloud.sys.core.problem;

import com.gavin.cloud.common.base.problem.AbstractThrowableProblem;
import com.gavin.cloud.common.base.problem.Status;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class UserNotFoundException extends AbstractThrowableProblem {

    private static final URI USER_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/user-not-found");

    public UserNotFoundException() {
        super(USER_NOT_FOUND_TYPE, "No user was found", Status.BAD_REQUEST);
    }

}
