package com.gavin.cloud.common.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class Counter {

    private String id;
    private int time;
    private String title;
    private String url;
    private Date createAt;
    private Date updateAt;

}
