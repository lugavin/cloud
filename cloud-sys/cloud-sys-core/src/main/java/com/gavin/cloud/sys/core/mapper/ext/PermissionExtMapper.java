package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.Permission;

import java.util.List;

public interface PermissionExtMapper {

    List<Permission> getMenus(String userId);

    List<Permission> getFuncs(String userId);

}
