package com.gavin.cloud.auth.core.mapper;

import com.gavin.cloud.auth.pojo.AuthToken;
import com.gavin.cloud.auth.pojo.AuthTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthTokenMapper {
    long countByExample(AuthTokenExample example);

    int deleteByExample(AuthTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AuthToken record);

    int insertSelective(AuthToken record);

    List<AuthToken> selectByExample(AuthTokenExample example);

    AuthToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AuthToken record, @Param("example") AuthTokenExample example);

    int updateByExample(@Param("record") AuthToken record, @Param("example") AuthTokenExample example);

    int updateByPrimaryKeySelective(AuthToken record);

    int updateByPrimaryKey(AuthToken record);
}