package com.gavin.cloud.common.base.dto;

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
@Deprecated
public class Result<T> {

    // 错误码
    private String retCode;

    // 错误消息
    private String retMsg;

    // 返回的业务数据
    private T data;

    // 业务处理成功标志
    private boolean success = Boolean.TRUE;

    public String getRetCode() {
        return retCode;
    }

    public Result<T> setRetCode(String retCode) {
        this.retCode = retCode;
        return this;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public Result<T> setRetMsg(String retMsg) {
        this.retMsg = retMsg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public Result<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public static <T> Result<T> newSuccess() {
        return new Result<>();
    }

    public static <T> Result<T> newSuccess(T data) {
        return new Result<T>().setData(data);
    }

    public static <T> Result<T> newError(String retCode, String retMsg) {
        return new Result<T>()
                .setRetCode(retCode)
                .setRetMsg(retMsg)
                .setSuccess(Boolean.FALSE);
    }

}
