package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.core.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    default List<Comment> selectAll() {
        return selectAll(false);
    }

    @Select("SELECT * FROM ${hist?'comment_hist':'comment'}")
    List<Comment> selectAll(@Param("hist") boolean hist);

    @SelectProvider(type = CommentProvider.class, method = "selectByPrimaryKey")
    Comment selectByPrimaryKey(Long id);

    @InsertProvider(type = CommentProvider.class, method = "insert")
    int insert(Comment record);

}
