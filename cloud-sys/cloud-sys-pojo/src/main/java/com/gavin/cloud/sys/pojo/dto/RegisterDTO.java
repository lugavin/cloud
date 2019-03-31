package com.gavin.cloud.sys.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String email;

}
