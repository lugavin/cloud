package com.gavin.cloud.sys.api.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Permission implements Serializable {
    private String id;

    private String code;

    private String name;

    private String type;

    private String icon;

    private String url;

    private Integer seq;

    private Boolean isParent;

    private String parentId;

    private static final long serialVersionUID = 1L;
}