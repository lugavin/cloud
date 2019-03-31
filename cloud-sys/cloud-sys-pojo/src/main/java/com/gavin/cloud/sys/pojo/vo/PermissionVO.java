package com.gavin.cloud.sys.pojo.vo;

import com.gavin.cloud.sys.pojo.Permission;

public class PermissionVO extends Permission {

    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

}
