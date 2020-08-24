package com.gavin.cloud.common.web.interceptor;

import com.gavin.cloud.common.web.context.SubjectContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // TODO 服务鉴权: 设置Token请求头
        log.info("====== Feign: {} {} ======", template.request().method(), template.request().url());
        log.info("====== Subject: {} ======", SubjectContextHolder.getContext().getSubject());
    }

}
