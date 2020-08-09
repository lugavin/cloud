package com.gavin.cloud.auth.core.problem;

import com.gavin.cloud.common.base.problem.AbstractThrowableProblem;
import com.gavin.cloud.common.base.problem.Status;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class AccountNotFoundException extends AbstractThrowableProblem {

    private static final URI ACCOUNT_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/account-not-found");

    public AccountNotFoundException() {
        super(ACCOUNT_NOT_FOUND_TYPE, "Account was not registered", Status.BAD_REQUEST);
    }

}
