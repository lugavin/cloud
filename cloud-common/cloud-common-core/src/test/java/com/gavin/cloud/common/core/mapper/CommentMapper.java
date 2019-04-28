package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.mapper.provider.CommentProvider;
import com.gavin.cloud.common.core.model.Comment;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface CommentMapper {

    @InsertProvider(type = CommentProvider.class, method = "insert")
    int insert(Comment record);

    @SelectProvider(type = CommentProvider.class, method = "selectByPrimaryKey")
    Comment selectByPrimaryKey(Long id);

}
