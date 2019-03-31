package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.pojo.Permission;
import com.gavin.cloud.sys.pojo.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionExtMapper {

    List<Permission> getMenus(String userId);

    List<Permission> getFuncs(String userId);

    PermissionVO getById(String id);

}
