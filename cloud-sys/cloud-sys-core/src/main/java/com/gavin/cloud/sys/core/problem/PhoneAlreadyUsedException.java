package com.gavin.cloud.sys.core.problem;

import com.gavin.cloud.common.base.problem.BadRequestAlertException;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class PhoneAlreadyUsedException extends BadRequestAlertException {

    private static final URI PHONE_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/phone-already-used");

    public PhoneAlreadyUsedException() {
        super(PHONE_ALREADY_USED_TYPE, "Phone address already in use", "userManagement", "phoneexists");
    }


}