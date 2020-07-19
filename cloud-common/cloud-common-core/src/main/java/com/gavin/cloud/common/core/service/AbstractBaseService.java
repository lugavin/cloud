package com.gavin.cloud.common.core.service;

import com.gavin.cloud.common.base.pojo.AbstractModel;
import com.gavin.cloud.common.core.mapper.CrudMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractBaseService<T extends AbstractModel> implements BaseService<T> {

    @Autowired
    private CrudMapper<T> crudMapper;

    public AbstractBaseService() {
        // 获取泛型类型
        // Class<?> rawClass = ResolvableType.forClass(AbstractBaseService.class, this.getClass()).getGeneric(0).getRawClass();
    }

    @Override
    @Transactional
    public int create(@NonNull T model) {
        return crudMapper.insertSelective(model);
    }

    @Override
    @Transactional
    public int create(@NonNull List<T> models) {
        return crudMapper.insertBatch(models);
    }

    @Override
    @Transactional
    public int update(@NonNull Serializable id, @NonNull T model) {
        model.setId(id);
        return crudMapper.updateByPrimaryKeySelective(model);
    }

    @Override
    @Transactional
    public int delete(@NonNull Serializable id) {
        return crudMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int delete(@NonNull Serializable[] ids) {
        return crudMapper.deleteBatch(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public T getById(@NonNull Serializable id) {
        return crudMapper.selectByPrimaryKey(id);
    }

}