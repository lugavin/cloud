package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionExtMapper {

    List<Permission> getMenus(String userId);

    List<Permission> getFuncs(String userId);

}
