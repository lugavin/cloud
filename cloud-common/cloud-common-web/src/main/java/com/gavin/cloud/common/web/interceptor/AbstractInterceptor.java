package com.gavin.cloud.common.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractInterceptor extends HandlerInterceptorAdapter {

    /**
     * @see org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
     */
    private static final String X_FORWARDED_PREFIX_HEADER = "X-Forwarded-Prefix";

    /**
     * 判断是否是内部服务调用(不经过Zuul网关)
     */
    protected boolean isInvokeInternal(HttpServletRequest request) {
        return StringUtils.isBlank(request.getHeader(X_FORWARDED_PREFIX_HEADER));
    }

    protected boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return true;
    }

    /**
     * 进入handler方法之前执行
     * 应用场景：用户身份认证、权限校验
     * 返回值：true(放行) false(拦截)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return !(handler instanceof HandlerMethod) || isInvokeInternal(request) || doPreHandle(request, response, (HandlerMethod) handler);
    }

    /**
     * 进入handler方法之后、返回modelAndView之前执行
     * 应用场景：统一指定公用的模型视图数据(如菜单导航)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * handler方法执行完成之后执行
     * 应用场景：统一异常处理和日志处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }

}
