package com.gavin.cloud.sys.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RolePermission implements Serializable {
    private Long id;

    private Long roleId;

    private Long permissionId;

    private static final long serialVersionUID = 1L;
}