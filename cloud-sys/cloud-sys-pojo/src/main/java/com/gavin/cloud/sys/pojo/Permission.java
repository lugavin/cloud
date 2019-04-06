package com.gavin.cloud.sys.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class Permission implements Serializable {
    private Long id;

    private String code;

    private String name;

    private String type;

    private String icon;

    private String url;

    private Short seq;

    private Boolean isParent;

    private Long parentId;

    private static final long serialVersionUID = 1L;
}