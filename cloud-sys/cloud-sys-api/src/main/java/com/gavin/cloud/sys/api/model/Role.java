package com.gavin.cloud.sys.api.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Role implements Serializable {
    private String id;

    private String code;

    private String name;

    private String remark;

    private static final long serialVersionUID = 1L;
}