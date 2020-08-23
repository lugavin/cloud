package com.gavin.cloud.sys.core.enums;

import com.gavin.cloud.common.base.problem.AlertType;
import com.gavin.cloud.common.base.problem.Problem;

import java.net.URI;

public enum SysAlertType implements AlertType {

    LOGIN_ALREADY_USED_TYPE("Login already in use", "userManagement", "userexists", "/login-already-used"),
    EMAIL_ALREADY_USED_TYPE("Email address already in use", "userManagement", "emailexists", "/email-already-used"),
    PHONE_ALREADY_USED_TYPE("Phone address already in use", "userManagement", "phoneexists", "/phone-already-used"),
    ROLE_ALREADY_USED_TYPE("Role address already in use", "roleManagement", "roleexists", "/role-already-used"),

    ;

    private final String title;
    private final String entityName;
    private final String errorKey;
    private final URI type;

    SysAlertType(String title, String entityName, String errorKey) {
        this(title, entityName, errorKey, Problem.DEFAULT_TYPE);
    }

    SysAlertType(String title, String entityName, String errorKey, String type) {
        this(title, entityName, errorKey, URI.create(Problem.PROBLEM_BASE_URL + type));
    }

    SysAlertType(String title, String entityName, String errorKey, URI type) {
        this.title = title;
        this.entityName = entityName;
        this.errorKey = errorKey;
        this.type = type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public String getErrorKey() {
        return errorKey;
    }

    @Override
    public URI getType() {
        return type;
    }

}
