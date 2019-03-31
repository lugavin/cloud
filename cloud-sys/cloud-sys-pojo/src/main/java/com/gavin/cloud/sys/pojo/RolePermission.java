package com.gavin.cloud.sys.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class RolePermission implements Serializable {
    private String id;

    private String roleId;

    private String permissionId;

    private static final long serialVersionUID = 1L;
}