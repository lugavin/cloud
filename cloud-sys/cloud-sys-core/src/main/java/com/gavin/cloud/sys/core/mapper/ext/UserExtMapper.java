package com.gavin.cloud.sys.core.mapper.ext;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.page.Pageable;
import com.gavin.cloud.sys.core.mapper.UserMapper;
import com.gavin.cloud.sys.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserExtMapper extends UserMapper {

    List<User> getList(Map<String, Object> param);

    Page<User> getPage(Pageable<Map<String, Object>> pageable);

    default Page<User> getPage(Map<String, Object> param, int page, int pageSize) {
        return getPage(new PageRequest<>(param, page, pageSize));
    }

}
