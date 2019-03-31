package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.Pageable;
import com.gavin.cloud.sys.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserExtMapper {

    List<User> getList(Map<String, Object> param);

    Page<User> getPage(Pageable<Map<String, Object>> pageable);

}
