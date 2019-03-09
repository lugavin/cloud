package com.gavin.cloud.sys.api.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserRole implements Serializable {
    private String id;

    private String userId;

    private String roleId;

    private static final long serialVersionUID = 1L;
}