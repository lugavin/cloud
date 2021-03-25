package com.gavin.cloud.sys.core.dao.ext;

import com.gavin.cloud.sys.core.dao.RolePermissionDao;
import com.gavin.cloud.sys.pojo.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RolePermissionExtDao extends RolePermissionDao {

    int insertBatch(List<RolePermission> list);

}
