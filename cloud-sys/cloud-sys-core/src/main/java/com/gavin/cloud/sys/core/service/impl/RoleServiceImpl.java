package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.mapper.RoleMapper;
import com.gavin.cloud.sys.core.mapper.UserRoleMapper;
import com.gavin.cloud.sys.core.mapper.ext.RoleExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.UserRoleExtMapper;
import com.gavin.cloud.sys.core.problem.RoleAlreadyUsedException;
import com.gavin.cloud.sys.core.service.RoleService;
import com.gavin.cloud.sys.pojo.Role;
import com.gavin.cloud.sys.pojo.RoleExample;
import com.gavin.cloud.sys.pojo.UserRole;
import com.gavin.cloud.sys.pojo.UserRoleExample;
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
            throw new RoleAlreadyUsedException();
        }
        role.setId(SnowflakeIdWorker.getInstance().nextId());
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRole(Long id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoles(Long userId) {
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
    public void assignRoles(Long uid, Long[] roleIds) {
        if (ArrayUtils.isEmpty(roleIds)) {
            UserRoleExample example = new UserRoleExample();
            example.createCriteria().andUserIdEqualTo(uid);
            userRoleMapper.deleteByExample(example);
            return;
        }

        List<Long> newRoles = Arrays.asList(roleIds);
        List<Long> oldRoles = getRoleIds(uid);

        List<Long> deleteRoles = new ArrayList<>(oldRoles);
        deleteRoles.removeAll(newRoles);

        List<Long> insertRoles = new ArrayList<>(newRoles);
        insertRoles.removeAll(oldRoles);

        if (!CollectionUtils.isEmpty(deleteRoles)) {
            UserRoleExample example = new UserRoleExample();
            example.createCriteria().andIdIn(deleteRoles);
            userRoleMapper.deleteByExample(example);
        }

        if (!CollectionUtils.isEmpty(insertRoles)) {
            List<UserRole> list = new ArrayList<>();
            for (Long roleId : insertRoles) {
                UserRole userRole = new UserRole();
                userRole.setId(SnowflakeIdWorker.getInstance().nextId());
                userRole.setUserId(uid);
                userRole.setRoleId(roleId);
                list.add(userRole);
            }
            userRoleExtMapper.insertBatch(list);
        }
    }

    private List<Long> getRoleIds(Long uid) {
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(uid);
        List<UserRole> userRoles = userRoleMapper.selectByExample(example);
        return userRoles.stream().map(UserRole::getId).collect(Collectors.toList());
    }

}
