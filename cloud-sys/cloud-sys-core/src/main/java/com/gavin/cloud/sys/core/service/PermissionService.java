package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.pojo.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    Permission createPermission(Permission permission);

    Permission updatePermission(Long id, Permission permission);

    void deletePermission(Long id);

    void deletePermissions(Long[] ids);

    Permission getPermission(Long id);

    List<Permission> getPermissions();

    List<Permission> getPermissions(Long userId, ResourceType type);

    /**
     * 给角色授权
     *
     * @param roleId  角色ID
     * @param permIds 权限集合
     */
    void assignPermissions(Long roleId, Long[] permIds);

    List<Permission> getPermissions(String role);

    List<Permission> getPermissions(String... roles);

    Set<String> getPermissionCodes(String role);

    Set<String> getPermissionCodes(String... roles);

}
