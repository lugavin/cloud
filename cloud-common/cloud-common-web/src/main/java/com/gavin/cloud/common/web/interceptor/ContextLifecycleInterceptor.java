package com.gavin.cloud.common.web.interceptor;

import com.gavin.cloud.common.web.context.SubjectContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 由于所有的Servlet容器(如Tomcat)都采用了线程池, 因此, 在请求处理完成后, 需要将ThreadLocal保存的数据清空, 否则可能出现意想不到的情况.
 *
 * @author Gavin Lu
 * @see com.netflix.zuul.context.ContextLifecycleFilter
 */
public class ContextLifecycleInterceptor extends AbstractInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SubjectContextHolder.getContext().clearContext();
    }

}
