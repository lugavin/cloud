package com.gavin.cloud.common.base.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 各种状态码产生的时机: Request(网络异常或超时) => 非法请求(4XX) => 服务异常(5XX) => 正常返回(200)
 * <p>
 * 错误消息可以通过HTTP响应头返回, 例如:
 * <pre>{@code
 * HttpHeaders headers = new HttpHeaders();
 * headers.add("X-App-Error", "error." + errorKey);
 * headers.add("X-App-Params", entityName);
 * return new ResponseEntity(body, headers, HttpStatus.BAD_REQUEST);
 * }</pre>
 */
@Data
@Deprecated
public class Responses<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer retCode; // 状态码
    private String retMsg;   // 提示消息
    private T body;          // 返回的业务数据

}
