package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.core.enums.ResourceType;
import com.gavin.cloud.sys.core.mapper.PermissionMapper;
import com.gavin.cloud.sys.pojo.Permission;
import com.gavin.cloud.sys.pojo.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionExtMapper extends PermissionMapper {

    List<Permission> getPermsByRole(String... role);

    /**
     * EnumTypeHandler是MyBatis默认的枚举类型转换器, 如果pojo类中使用了枚举类型, 而配置文件没有指定类型转换类,
     * MyBatis将使用EnumTypeHandler处理枚举属性, 即将枚举类的name进行存储.
     */
    List<Permission> getPermsByUid(@Param("uid") Long uid, @Param("type") ResourceType type);

    PermissionVO selectByPrimaryKey(Long id);

}
