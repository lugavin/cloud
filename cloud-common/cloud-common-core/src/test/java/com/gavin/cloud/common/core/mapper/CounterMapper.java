package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.model.Counter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Objects;

@Mapper
public interface CounterMapper {

    default Counter selectByPrimaryKey(Long id) {
        return selectByPrimaryKey(id, Objects.hash(id) % 2);
    }

    @Select("SELECT * FROM counter_${tableIndex} WHERE id=#{id}")
    Counter selectByPrimaryKey(@Param("id") Long id, @Param("tableIndex") int tableIndex);

}
