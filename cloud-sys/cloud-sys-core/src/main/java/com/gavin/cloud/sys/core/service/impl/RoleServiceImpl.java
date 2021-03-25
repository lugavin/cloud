package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.problem.AppAlertException;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.dao.ext.RoleExtDao;
import com.gavin.cloud.sys.core.dao.ext.UserRoleExtDao;
import com.gavin.cloud.sys.core.service.RoleService;
import com.gavin.cloud.sys.pojo.Role;
import com.gavin.cloud.sys.pojo.RoleExample;
import com.gavin.cloud.sys.pojo.UserRole;
import com.gavin.cloud.sys.pojo.UserRoleExample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gavin.cloud.sys.core.enums.SysAlertType.ROLE_ALREADY_USED_TYPE;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleExtDao roleExtMapper;
    private final UserRoleExtDao userRoleExtMapper;

    @Override
    public Role createRole(Role role) {
        RoleExample example = new RoleExample();
        example.createCriteria().andCodeEqualTo(role.getCode());
        long rows = roleExtMapper.countByExample(example);
        if (rows > 0) {
            throw new AppAlertException(ROLE_ALREADY_USED_TYPE);
        }
        role.setId(SnowflakeIdWorker.getInstance().nextId());
        roleExtMapper.insert(role);
        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRole(Long id) {
        return roleExtMapper.selectByPrimaryKey(id);
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
        return roleExtMapper.getPage(param, page, pageSize);
    }

    @Override
    public void assignRoles(Long uid, Long[] roleIds) {
        if (Array.getLength(roleIds) < 1) {
            UserRoleExample example = new UserRoleExample();
            example.createCriteria().andUserIdEqualTo(uid);
            userRoleExtMapper.deleteByExample(example);
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
            userRoleExtMapper.deleteByExample(example);
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
        List<UserRole> userRoles = userRoleExtMapper.selectByExample(example);
        return userRoles.stream().map(UserRole::getId).collect(Collectors.toList());
    }

}
