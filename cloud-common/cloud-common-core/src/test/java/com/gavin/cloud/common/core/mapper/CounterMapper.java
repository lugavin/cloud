package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.model.Counter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CounterMapper {

    default Counter selectByPrimaryKey(Long id) {
        return selectByPrimaryKey(id, false);
    }

    @Select("SELECT * FROM ${hist?'counter_hist':'counter'} WHERE id=#{id}")
    Counter selectByPrimaryKey(@Param("id") Long id, @Param("hist") boolean hist);

}
