package com.gavin.cloud.auth.core.mapper.ext;

import com.gavin.cloud.auth.core.mapper.AuthTokenMapper;
import com.gavin.cloud.auth.pojo.AuthToken;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.page.Pageable;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface AuthTokenMapperExt extends AuthTokenMapper {

    Page<AuthToken> getPage(Pageable<Map<String, Object>> pageable);

    default Page<AuthToken> getPage(Map<String, Object> param, int page, int pageSize) {
        return getPage(new PageRequest<>(param, page, pageSize));
    }

}