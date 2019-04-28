package com.gavin.cloud.common.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class Counter {

    private Long id;
    private Integer time;
    private String title;
    private String url;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

}
