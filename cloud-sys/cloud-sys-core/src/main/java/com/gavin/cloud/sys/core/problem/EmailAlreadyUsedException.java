package com.gavin.cloud.sys.core.problem;

import com.gavin.cloud.common.base.problem.BadRequestAlertException;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");

    public EmailAlreadyUsedException() {
        super(EMAIL_ALREADY_USED_TYPE, "Email address already in use", "userManagement", "emailexists");
    }

}
