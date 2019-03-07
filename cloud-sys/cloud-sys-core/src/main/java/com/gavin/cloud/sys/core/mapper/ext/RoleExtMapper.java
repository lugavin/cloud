package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.Role;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.Pageable;

import java.util.List;
import java.util.Map;

public interface RoleExtMapper {

    List<Role> getByUserId(String userId);

    List<Role> getList(Map<String, Object> param);

    Page<Role> getPage(Pageable<Map<String, Object>> pageable);

}
