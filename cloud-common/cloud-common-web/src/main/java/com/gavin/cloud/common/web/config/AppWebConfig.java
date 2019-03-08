package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.subject.SubjectService;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.web.properties.AuthProperties;
import com.gavin.cloud.common.web.properties.SwaggerProperties;
import com.gavin.cloud.common.web.support.SubjectMethodProcessor;
import com.gavin.cloud.common.web.interceptor.AuthInterceptor;
import com.gavin.cloud.common.web.service.SubjectServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 继承WebMvcConfigurerAdapter既保留了SpringBoot提供的默认配置也可扩展自定义的额外配置(无需使用@EnableWebMvc注解).
 * 注意:当添加@EnableWebMvc注解后, WebMvcAutoConfiguration中的配置就不会生效, 会自动覆盖默认静态资源存放的目录而将静态资源定位在src/main/webapp目录.
 */
@Configuration
@EnableConfigurationProperties({AuthProperties.class, SwaggerProperties.class})
public class AppWebConfig extends WebMvcConfigurerAdapter {

    private static final String[] EXCLUDE_PATH_PATTERNS = {
            "/v2/api-docs",
            "/swagger-resources/**",
    };

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT));
        // registrar.setUseIsoFormat(Boolean.TRUE);
        registrar.registerFormatters(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(subjectService())).excludePathPatterns(EXCLUDE_PATH_PATTERNS);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SubjectMethodProcessor(subjectService()));
    }

    @Bean
    public SubjectService subjectService() {
        return new SubjectServiceImpl();
    }
}
