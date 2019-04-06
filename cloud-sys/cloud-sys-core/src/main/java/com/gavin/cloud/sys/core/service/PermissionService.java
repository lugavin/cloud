package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.pojo.Permission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(Permission permission);

    Permission updatePermission(Long id, Permission permission);

    void deletePermission(Long id);

    void deletePermissions(Long[] ids);

    /**
     * 给角色授权
     *
     * @param roleId  角色ID
     * @param permIds 权限集合
     */
    void assignPermissions(Long roleId, Long[] permIds);

    Permission getPermission(Long id);

    List<Permission> getPermissions();

    List<Permission> getPermissions(Long userId, ResourceType type);

}
