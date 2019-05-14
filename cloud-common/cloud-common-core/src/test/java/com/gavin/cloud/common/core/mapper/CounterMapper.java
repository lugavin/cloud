package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.model.Counter;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CounterMapper {

    @InsertProvider(type = CounterProvider.class, method = "insert")
    int insert(Counter record);

}
