package com.gavin.cloud.sys.core.problem;

import com.gavin.cloud.common.base.problem.BadRequestAlertException;

import java.net.URI;

import static com.gavin.cloud.common.base.problem.ProblemType.PROBLEM_BASE_URL;

public class RoleAlreadyUsedException extends BadRequestAlertException {

    private static final URI ROLE_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/role-already-used");

    public RoleAlreadyUsedException() {
        super(ROLE_ALREADY_USED_TYPE, "Role address already in use", "roleManagement", "roleexists");
    }


}