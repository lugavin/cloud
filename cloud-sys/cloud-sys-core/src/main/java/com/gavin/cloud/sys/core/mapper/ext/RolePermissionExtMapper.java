package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.RolePermission;

import java.util.List;

public interface RolePermissionExtMapper {

    int insertBatch(List<RolePermission> list);

}
