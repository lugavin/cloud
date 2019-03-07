package com.gavin.cloud.common.core.mapper;

import java.util.List;

public interface CrudExtMapper<T> {

    int insertBatch(List<T> list);

}
