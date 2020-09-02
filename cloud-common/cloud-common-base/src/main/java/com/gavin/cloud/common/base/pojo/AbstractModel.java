package com.gavin.cloud.common.base.pojo;

import java.io.Serializable;

public abstract class AbstractModel implements Model {

    protected Serializable id;

    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

}