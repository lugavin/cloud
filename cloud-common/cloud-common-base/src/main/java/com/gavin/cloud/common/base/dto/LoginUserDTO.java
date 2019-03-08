package com.gavin.cloud.common.base.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String uid;
    private final String username;
    private final String clientIP;

}
