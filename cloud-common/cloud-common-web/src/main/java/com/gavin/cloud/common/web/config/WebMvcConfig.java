package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.util.Constants;
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
public class WebMvcConfig extends WebMvcConfigurerAdapter {

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
        // registry.addInterceptor(new AuthInterceptor(subjectService())).excludePathPatterns(EXCLUDE_PATH_PATTERNS);
    }

    /**
     * 自定义参数注入
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // argumentResolvers.add(new HandlerMethodArgumentResolver() {
        //     @Override
        //     public boolean supportsParameter(MethodParameter parameter) {
        //         return parameter.getParameterType().isAssignableFrom(ActiveUser.class);
        //     }

        //     @Override
        //     public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //         return SubjectContextHolder.getContext().getSubject();
        //     }
        // });
    }

}
