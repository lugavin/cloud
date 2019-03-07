package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.sys.api.model.Permission;
import com.gavin.cloud.sys.api.model.PermissionExample;
import com.gavin.cloud.sys.api.model.RolePermission;
import com.gavin.cloud.sys.api.model.RolePermissionExample;
import com.gavin.cloud.sys.core.mapper.RolePermissionMapper;
import com.gavin.cloud.sys.core.mapper.ext.PermissionExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.RolePermissionExtMapper;
import com.gavin.cloud.sys.core.service.PermissionService;
import com.gavin.cloud.common.base.util.RandomUtils;
import com.gavin.cloud.sys.core.mapper.PermissionMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionExtMapper permissionExtMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private RolePermissionExtMapper rolePermissionExtMapper;

    @Override
    public Permission createPermission(Permission permission) {
        permission.setId(RandomUtils.randomUUID());
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    public Permission updatePermission(Permission permission) {
        permissionMapper.updateByPrimaryKey(permission);
        return permission;
    }

    @Override
    public void deletePermission(String id) {
        permissionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deletePermissions(String[] ids) {
        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        permissionMapper.deleteByExample(example);
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermission(String id) {
        return permissionMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissions() {
        return permissionMapper.selectByExample(new PermissionExample());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getMenuPermissions(String userId) {
        return permissionExtMapper.getMenus(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getFuncPermissions(String userId) {
        return permissionExtMapper.getFuncs(userId);
    }

    @Override
    public void addPermissions(String roleId, String[] permIds) {
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        rolePermissionMapper.deleteByExample(example);
        if (ArrayUtils.isNotEmpty(permIds)) {
            List<RolePermission> list = new ArrayList<>();
            for (String permId : permIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(RandomUtils.randomUUID());
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permId);
                list.add(rolePermission);
            }
            rolePermissionExtMapper.insertBatch(list);
        }
    }

}
