package com.gavin.cloud.auth.core.problem;

import com.gavin.cloud.common.base.problem.AbstractThrowableProblem;
import com.gavin.cloud.common.base.problem.Status;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class AccountNotActivatedException extends AbstractThrowableProblem {

    private static final URI ACCOUNT_NOT_ACTIVATED_TYPE = URI.create(PROBLEM_BASE_URL + "/account-not-activated");

    public AccountNotActivatedException() {
        super(ACCOUNT_NOT_ACTIVATED_TYPE, "Account was not activated", Status.BAD_REQUEST);
    }

}
