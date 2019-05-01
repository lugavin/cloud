package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.PageRequest;
import com.gavin.cloud.common.base.page.Pageable;

import java.util.List;
import java.util.Map;

public interface PagingMapper {

    <T> List<T> getList(Map<String, Object> param);

    <T, E> Page<T> getPage(Pageable<E> pageable);

    default <T, E> Page<T> getPage(E param, int page, int pageSize) {
        return getPage(new PageRequest<>(param, page, pageSize));
    }

}
