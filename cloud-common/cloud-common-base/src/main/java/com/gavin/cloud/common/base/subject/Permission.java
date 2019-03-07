package com.gavin.cloud.common.base.subject;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private String type;
    private String icon;
    private String url;
    private Integer seq;
    private Boolean isParent;
    private String parentId;

}
