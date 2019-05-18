package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.dto.PrcDTO;
import com.gavin.cloud.common.core.model.Counter;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

@Mapper
public interface CounterMapper {

    @InsertProvider(type = CounterProvider.class, method = "insert")
    int insert(Counter record);

    @Select("{ CALL prc_exchange_partition(#{bizTable,jdbcType=VARCHAR,mode=IN}, #{hisTable,jdbcType=VARCHAR,mode=IN}, #{tmpTable,jdbcType=VARCHAR,mode=IN}, #{remDays,jdbcType=INTEGER,mode=IN}, #{retCode,jdbcType=VARCHAR,mode=OUT}, #{retMsg,jdbcType=VARCHAR,mode=OUT}) }")
    @Options(statementType = StatementType.CALLABLE)
    void callPrc(PrcDTO prcDTO);

}
