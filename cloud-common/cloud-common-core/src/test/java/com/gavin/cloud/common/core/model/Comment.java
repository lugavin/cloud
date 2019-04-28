package com.gavin.cloud.common.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private Long id;
    private Long pid;
    private String nick;
    private String mail;
    private String link;
    private String ua;
    private String comment;
    private String url;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

}
