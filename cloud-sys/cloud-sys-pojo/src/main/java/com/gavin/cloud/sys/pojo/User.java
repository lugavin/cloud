package com.gavin.cloud.sys.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class User implements Serializable {
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String salt;

    private String phone;

    private String email;

    private String avatar;

    private String langKey;

    private Boolean activated;

    private String activationKey;

    private String resetKey;

    private Date resetDate;

    private String createdBy;

    private Date createdAt;

    private String updatedBy;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}