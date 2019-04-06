package com.gavin.cloud.common.base.model;

import java.io.Serializable;

public abstract class AbstractModel implements Model {

    private Serializable id;

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

}