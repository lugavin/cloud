package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.Role;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.Pageable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleExtMapper {

    List<Role> getByUserId(String userId);

    List<Role> getList(Map<String, Object> param);

    Page<Role> getPage(Pageable<Map<String, Object>> pageable);

}
