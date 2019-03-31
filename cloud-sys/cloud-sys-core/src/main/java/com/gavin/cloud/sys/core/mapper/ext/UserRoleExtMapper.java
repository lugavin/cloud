package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleExtMapper {

    int insertBatch(List<UserRole> list);

}
