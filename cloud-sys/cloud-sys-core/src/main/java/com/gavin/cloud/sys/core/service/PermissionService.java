package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.api.model.Permission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(Permission permission);

    Permission updatePermission(Permission permission);

    void deletePermission(String id);

    void deletePermissions(String[] ids);

    Permission getPermission(String id);

    List<Permission> getPermissions();

    List<Permission> getMenuPermissions(String userId);

    List<Permission> getFuncPermissions(String userId);

    void addPermissions(String roleId, String[] permIds);

}
