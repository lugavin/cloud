package com.gavin.cloud.auth.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AuthToken implements Serializable {
    private Long id;

    private Long uid;

    private String clientIp;

    private String refreshToken;

    private Date createdAt;

    private Date expiredAt;

    private static final long serialVersionUID = 1L;
}