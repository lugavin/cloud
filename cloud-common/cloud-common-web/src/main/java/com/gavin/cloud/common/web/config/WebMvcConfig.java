package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.AuthProperties;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.web.context.SubjectContextHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
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

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;

    public WebMvcConfig(AuthProperties authProperties, JwtProperties jwtProperties) {
        this.authProperties = authProperties;
        this.jwtProperties = jwtProperties;
    }

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
        //registry.addInterceptor(new ContextLifecycleInterceptor());
        //registry.addInterceptor(new AuthInterceptor(jwtProperties));
    }

    /**
     * 自定义参数注入
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //argumentResolvers.add(new SubjectHandlerMethodArgumentResolver());
    }

    private static class SubjectHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(ActiveUser.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return SubjectContextHolder.getContext().getSubject();
        }

    }

}
