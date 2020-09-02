package com.gavin.cloud.common.base.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 各种状态码产生的时机: Request(网络异常或超时) => 非法请求(4XX) => 服务异常(5XX) => 正常返回(200)
 * <p>
 * 错误消息国际化可以通过HTTP响应头返回错误消息KEY值, 例如:
 * <pre>{@code
 * HttpHeaders headers = new HttpHeaders();
 * headers.add("X-App-Error", "error." + errorKey);
 * headers.add("X-App-Params", entityName);
 * return new ResponseEntity(body, headers, HttpStatus.BAD_REQUEST);
 * }</pre>
 */
@Data
public class Responses<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer retCode; // 状态码(可对HTTP状态码进行扩展, 如400101)
    private final String retMsg;   // 提示消息
    private final T retData;       // 返回的业务数据

    // public static <T> Responses<T> ok(T retData) {
    //     return new Responses<>(HttpStatus.OK.getStatusCode(), HttpStatus.OK.getReasonPhrase(), retData);
    // }

}
