package com.gavin.cloud.common.core.service;

import com.gavin.cloud.common.base.model.Model;
import com.gavin.cloud.common.core.mapper.CrudMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractBaseService<T extends Model> implements BaseService<T> {

    @Autowired
    private CrudMapper<T> crudMapper;

    @Override
    @Transactional
    public int create(T model) {
        return crudMapper.insertSelective(model);
    }

    @Override
    @Transactional
    public int create(List<T> models) {
        return crudMapper.insertBatch(models);
    }

    @Override
    @Transactional
    public int update(T model) {
        return crudMapper.updateByPrimaryKeySelective(model);
    }

    @Override
    @Transactional
    public int delete(Serializable id) {
        return crudMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int delete(Serializable[] ids) {
        return crudMapper.deleteBatch(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public T getById(Serializable id) {
        return crudMapper.selectByPrimaryKey(id);
    }

}