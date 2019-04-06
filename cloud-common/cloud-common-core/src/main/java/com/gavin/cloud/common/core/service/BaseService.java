package com.gavin.cloud.common.core.service;

import com.gavin.cloud.common.base.model.AbstractModel;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T extends AbstractModel> {

    int create(T model);

    int create(List<T> models);

    int update(Serializable id, T model);

    int delete(Serializable id);

    int delete(Serializable[] ids);

    T getById(Serializable id);

}
