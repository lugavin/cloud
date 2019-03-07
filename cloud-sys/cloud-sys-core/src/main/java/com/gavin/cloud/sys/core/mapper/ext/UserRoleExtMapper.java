package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.UserRole;

import java.util.List;

public interface UserRoleExtMapper {

    int insertBatch(List<UserRole> list);

}
