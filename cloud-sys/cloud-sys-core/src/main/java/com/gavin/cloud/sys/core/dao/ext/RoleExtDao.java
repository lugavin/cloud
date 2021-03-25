package com.gavin.cloud.sys.core.dao.ext;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.page.Pageable;
import com.gavin.cloud.sys.core.dao.RoleDao;
import com.gavin.cloud.sys.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleExtDao extends RoleDao {

    List<Role> getByUserId(Long userId);

    List<Role> getList(Map<String, Object> param);

    Page<Role> getPage(Pageable<Map<String, Object>> pageable);

    default Page<Role> getPage(Map<String, Object> param, int page, int pageSize) {
        return getPage(new PageRequest<>(param, page, pageSize));
    }

}
