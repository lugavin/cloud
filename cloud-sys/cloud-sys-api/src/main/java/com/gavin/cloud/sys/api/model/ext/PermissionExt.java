package com.gavin.cloud.sys.api.model.ext;

import com.gavin.cloud.sys.api.model.Permission;

public class PermissionExt extends Permission {

    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

}
