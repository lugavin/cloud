package com.gavin.cloud.common.web.support;

import com.gavin.cloud.common.base.subject.Subject;
import com.gavin.cloud.common.base.subject.SubjectService;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 自定义方法参数注入
 */
public class SubjectMethodProcessor implements HandlerMethodArgumentResolver {

    private final SubjectService subjectService;

    public SubjectMethodProcessor(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Subject.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return subjectService.getSubject();
    }

}
