package com.gavin.cloud.common.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private String id;
    private String pid;
    private String nick;
    private String mail;
    private String link;
    private String ua;
    private String comment;
    private String url;
    private Date createAt;
    private Date updateAt;

}
