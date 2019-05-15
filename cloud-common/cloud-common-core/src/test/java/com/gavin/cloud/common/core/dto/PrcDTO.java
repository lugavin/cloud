package com.gavin.cloud.common.core.dto;

import lombok.Data;

@Data
public class PrcDTO {

    private String bizTable; // 输入参数: 业务表表名
    private String tmpTable; // 输入参数: 临时表表名
    private String hisTable; // 输入参数: 历史表表名
    private Integer remDays; // 输入参数: 保留天数
    private String retCode;  // 输出参数: 错误码
    private String retMsg;   // 输出参数: 错误消息

}
