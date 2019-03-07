package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.sys.api.model.User;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.Pageable;

import java.util.List;
import java.util.Map;

public interface UserExtMapper {

    List<User> getList(Map<String, Object> param);

    Page<User> getPage(Pageable<Map<String, Object>> pageable);

}
