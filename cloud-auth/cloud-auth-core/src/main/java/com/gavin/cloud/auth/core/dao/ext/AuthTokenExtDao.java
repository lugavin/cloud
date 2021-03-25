package com.gavin.cloud.auth.core.dao.ext;

import com.gavin.cloud.auth.core.dao.AuthTokenDao;
import com.gavin.cloud.auth.pojo.AuthToken;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.page.Pageable;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface AuthTokenExtDao extends AuthTokenDao {

    Page<AuthToken> getPage(Pageable<Map<String, Object>> pageable);

    default Page<AuthToken> getPage(Map<String, Object> param, int page, int pageSize) {
        return getPage(new PageRequest<>(param, page, pageSize));
    }

}