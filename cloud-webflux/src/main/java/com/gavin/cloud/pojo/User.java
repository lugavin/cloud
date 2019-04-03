package com.gavin.cloud.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String login;
    private String phone;
    private String email;
    private String createdBy;
    private Date createdAt;
    private String updatedBy;
    private Date updatedAt;

}
