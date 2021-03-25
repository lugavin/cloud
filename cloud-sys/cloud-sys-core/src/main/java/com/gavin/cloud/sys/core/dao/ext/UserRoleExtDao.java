package com.gavin.cloud.sys.core.dao.ext;

import com.gavin.cloud.sys.core.dao.UserRoleDao;
import com.gavin.cloud.sys.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleExtDao extends UserRoleDao {

    int insertBatch(List<UserRole> list);

}
