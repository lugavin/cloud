package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.web.interceptor.SubjectFeignInterceptor;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Feign.class})
public class FeignConfig {

    @Bean
    @ConditionalOnMissingBean
    RequestInterceptor subjectFeignInterceptor() {
        return new SubjectFeignInterceptor();
    }

}