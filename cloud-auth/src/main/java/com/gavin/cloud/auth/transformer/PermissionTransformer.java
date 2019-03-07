package com.gavin.cloud.auth.transformer;

import com.gavin.cloud.common.base.subject.Permission;

import java.util.Objects;

public class PermissionTransformer {

    public static Permission transform(com.gavin.cloud.sys.api.model.Permission permission) {
        Objects.requireNonNull(permission);
        Permission perm = new Permission();
        perm.setId(permission.getId());
        perm.setCode(permission.getCode());
        perm.setName(permission.getName());
        perm.setType(permission.getType());
        perm.setIcon(permission.getIcon());
        perm.setUrl(permission.getUrl());
        perm.setSeq(permission.getSeq());
        perm.setIsParent(permission.getIsParent());
        perm.setParentId(permission.getParentId());
        return perm;
    }

}
