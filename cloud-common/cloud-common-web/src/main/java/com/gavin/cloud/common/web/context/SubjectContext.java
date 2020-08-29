package com.gavin.cloud.common.web.context;

import com.gavin.cloud.common.base.auth.ActiveUser;

import java.util.Optional;

/**
 * one-request-per-thread: 一个请求就是一个线程, 每个线程维护各自实例对象的数据.
 * 注意: 因为所有的Servlet容器(如Tomcat)都采用了线程池, 因此, 在请求处理完成后, 需要将ThreadLocal保存的数据清空, 否则可能出现意想不到的情况.
 *
 * @author Gavin Lu
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @see org.springframework.security.core.context.ThreadLocalSecurityContextHolderStrategy
 * @see com.netflix.zuul.context.ContextLifecycleFilter
 */
public final class SubjectContext {

    private static final ThreadLocal<SubjectContext> contextHolder = new ThreadLocal<>();

    private SubjectContext() {
    }

    public static SubjectContext getContext() {
        return Optional.ofNullable(contextHolder.get()).orElseGet(() -> {
            SubjectContext context = new SubjectContext();
            contextHolder.set(context);
            return context;
        });
    }

    public void clearContext() {
        contextHolder.remove();
    }

    private ActiveUser subject;

    public ActiveUser getSubject() {
        return subject;
    }

    public void setSubject(ActiveUser subject) {
        this.subject = subject;
    }

}