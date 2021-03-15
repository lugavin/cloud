package com.gavin.cloud.common.web.interceptor;

import com.gavin.cloud.common.web.context.SubjectContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Optional;

public class SubjectFeignInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        Optional.ofNullable(SubjectContext.getContext().getSubject())
                .ifPresent(s -> template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER, s)));
    }

}