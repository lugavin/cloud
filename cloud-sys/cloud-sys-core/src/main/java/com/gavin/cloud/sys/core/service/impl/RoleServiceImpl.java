package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.sys.api.model.Role;
import com.gavin.cloud.sys.api.model.RoleExample;
import com.gavin.cloud.sys.api.model.UserRole;
import com.gavin.cloud.sys.api.model.UserRoleExample;
import com.gavin.cloud.sys.core.mapper.RoleMapper;
import com.gavin.cloud.sys.core.mapper.UserRoleMapper;
import com.gavin.cloud.sys.core.mapper.ext.RoleExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.UserRoleExtMapper;
import com.gavin.cloud.sys.core.service.RoleService;
import com.gavin.cloud.sys.core.enums.SysMessageType;
import com.gavin.cloud.common.base.problem.AppException;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.util.RandomUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleExtMapper roleExtMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserRoleExtMapper userRoleExtMapper;

    @Override
    public Role createRole(Role role) {
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();
        criteria.andCodeEqualTo(role.getCode());
        long rows = roleMapper.countByExample(example);
        if (rows > 0) {
            throw new AppException(SysMessageType.ERR_ROLE_ALREADY_USED);
        }
        role.setId(RandomUtils.randomUUID());
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRole(String id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoles(String userId) {
        return roleExtMapper.getByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoles(Map<String, Object> param) {
        return roleExtMapper.getList(param);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Role> getRoles(Map<String, Object> param, int page, int pageSize) {
        return roleExtMapper.getPage(new PageRequest<>(param, page, pageSize));
    }

    @Override
    public void addRoles(String uid, String[] roleIds) {
        if (ArrayUtils.isEmpty(roleIds)) {
            UserRoleExample example = new UserRoleExample();
            example.createCriteria().andUserIdEqualTo(uid);
            userRoleMapper.deleteByExample(example);
            return;
        }

        List<String> newRoles = Arrays.asList(roleIds);
        List<String> oldRoles = getRoleIds(uid);

        List<String> deleteRoles = new ArrayList<>(oldRoles);
        deleteRoles.removeAll(newRoles);

        List<String> insertRoles = new ArrayList<>(newRoles);
        insertRoles.removeAll(oldRoles);

        if (!CollectionUtils.isEmpty(deleteRoles)) {
            UserRoleExample example = new UserRoleExample();
            example.createCriteria().andIdIn(deleteRoles);
            userRoleMapper.deleteByExample(example);
        }

        if (!CollectionUtils.isEmpty(insertRoles)) {
            List<UserRole> list = new ArrayList<>();
            for (String roleId : insertRoles) {
                UserRole userRole = new UserRole();
                userRole.setId(RandomUtils.randomUUID());
                userRole.setUserId(uid);
                userRole.setRoleId(roleId);
                list.add(userRole);
            }
            userRoleExtMapper.insertBatch(list);
        }
    }

    private List<String> getRoleIds(String uid) {
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(uid);
        List<UserRole> userRoles = userRoleMapper.selectByExample(example);
        return userRoles.stream().map(UserRole::getId).collect(Collectors.toList());
    }

}
