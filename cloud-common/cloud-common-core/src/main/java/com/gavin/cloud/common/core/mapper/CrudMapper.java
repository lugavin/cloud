package com.gavin.cloud.common.core.mapper;

import com.gavin.cloud.common.base.model.AbstractModel;

import java.io.Serializable;
import java.util.List;

public interface CrudMapper<T extends AbstractModel> {

    int insert(T model);

    int insertSelective(T model);

    int deleteByPrimaryKey(Serializable id);

    int updateByPrimaryKey(T model);

    int updateByPrimaryKeySelective(T model);

    T selectByPrimaryKey(Serializable id);

    int insertBatch(List<T> models);

    int deleteBatch(Serializable[] ids);

}
