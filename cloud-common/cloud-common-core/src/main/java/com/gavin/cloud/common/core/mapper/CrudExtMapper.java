package com.gavin.cloud.common.core.mapper;

import java.util.List;

public interface CrudExtMapper<T> extends Mapper {

    int insertBatch(List<T> list);

}
