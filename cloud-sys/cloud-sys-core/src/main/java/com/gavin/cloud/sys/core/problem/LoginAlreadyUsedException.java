package com.gavin.cloud.sys.core.problem;

import com.gavin.cloud.common.base.problem.BadRequestAlertException;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class LoginAlreadyUsedException extends BadRequestAlertException {

    private static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");

    public LoginAlreadyUsedException() {
        super(LOGIN_ALREADY_USED_TYPE, "Login already in use", "userManagement", "userexists");
    }

}
