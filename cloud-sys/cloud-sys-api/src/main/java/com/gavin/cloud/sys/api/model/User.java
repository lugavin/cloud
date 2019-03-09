package com.gavin.cloud.sys.api.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class User implements Serializable {
    private String id;

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

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private static final long serialVersionUID = 1L;
}