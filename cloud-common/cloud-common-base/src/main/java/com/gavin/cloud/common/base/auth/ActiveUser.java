package com.gavin.cloud.common.base.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long uid;
    private final String username;
    private final String clientIP;
    private final List<String> roles;

}
