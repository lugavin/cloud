package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.pojo.Role;
import com.gavin.cloud.common.base.page.Page;

import java.util.List;
import java.util.Map;

public interface RoleService {

    Role createRole(Role role);

    Role getRole(Long id);

    List<Role> getRoles(Long userId);

    List<Role> getRoles(Map<String, Object> param);

    Page<Role> getRoles(Map<String, Object> param, int page, int pageSize);

    void assignRoles(Long uid, Long[] roleIds);

}
