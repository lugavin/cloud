package com.gavin.cloud.sys.core.problem;

import com.gavin.cloud.common.base.problem.AbstractThrowableProblem;
import com.gavin.cloud.common.base.problem.Status;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class EmailNotFoundException extends AbstractThrowableProblem {

    private static final URI EMAIL_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/email-not-found");

    public EmailNotFoundException() {
        super(EMAIL_NOT_FOUND_TYPE, "Email address not registered", Status.BAD_REQUEST);
    }

}
