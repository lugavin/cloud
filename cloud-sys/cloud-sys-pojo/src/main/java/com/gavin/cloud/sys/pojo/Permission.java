package com.gavin.cloud.sys.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Permission implements Serializable {
    private Long id;

    private String code;

    private String name;

    private String type;

    private String url;

    private String method;

    private Short seq;

    private String icon;

    private Boolean isParent;

    private Long parentId;

    private static final long serialVersionUID = 1L;
}