package com.gavin.cloud.common.base.subject;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String username;

}
