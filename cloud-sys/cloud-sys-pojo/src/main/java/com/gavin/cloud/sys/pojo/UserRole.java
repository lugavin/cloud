package com.gavin.cloud.sys.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserRole implements Serializable {
    private Long id;

    private Long userId;

    private Long roleId;

    private static final long serialVersionUID = 1L;
}