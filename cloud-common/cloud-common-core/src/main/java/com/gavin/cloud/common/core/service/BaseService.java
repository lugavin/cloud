package com.gavin.cloud.common.core.service;

import com.gavin.cloud.common.base.model.Model;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T extends Model> {

    int create(T model);

    int create(List<T> models);

    int update(T model);

    int delete(Serializable id);

    int delete(Serializable[] ids);

    T getById(Serializable id);

}
