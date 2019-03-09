package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.page.Pageable;

import java.util.List;
import java.util.Map;

public interface PagingMapper extends Mapper {

    <T> List<T> getList(Map<String, Object> param);

    <T, E> Page<T> getPage(Pageable<E> pageable);

}
