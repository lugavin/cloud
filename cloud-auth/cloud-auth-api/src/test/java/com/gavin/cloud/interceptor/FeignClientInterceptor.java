package com.gavin.cloud.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @see org.springframework.cloud.openfeign.encoding.BaseRequestInterceptor
 */
@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // TODO 服务鉴权: 设置Token请求头
        log.info("====== Feign: {} {} ======", template.request().url(), template.request().httpMethod());
    }

}
