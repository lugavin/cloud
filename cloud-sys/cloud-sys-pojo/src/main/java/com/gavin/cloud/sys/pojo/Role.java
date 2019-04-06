package com.gavin.cloud.sys.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class Role implements Serializable {
    private Long id;

    private String code;

    private String name;

    private String remark;

    private static final long serialVersionUID = 1L;
}