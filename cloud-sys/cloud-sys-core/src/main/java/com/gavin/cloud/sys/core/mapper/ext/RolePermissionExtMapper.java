package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.pojo.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RolePermissionExtMapper {

    int insertBatch(List<RolePermission> list);

}
