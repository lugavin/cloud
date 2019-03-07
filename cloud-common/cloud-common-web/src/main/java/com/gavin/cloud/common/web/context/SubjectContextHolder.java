package com.gavin.cloud.common.web.context;

import com.gavin.cloud.common.base.subject.Subject;

/**
 * 一个请求就是一个线程, 每个线程维护各自实例对象的数据
 *
 * @author Gavin Lu
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @see org.springframework.security.core.context.ThreadLocalSecurityContextHolderStrategy
 */
@Deprecated
public final class SubjectContextHolder {

    private static final ThreadLocal<SubjectContextHolder> contextHolder = new ThreadLocal<>();

    private SubjectContextHolder() {
    }

    public static SubjectContextHolder getContext() {
        SubjectContextHolder context = contextHolder.get();
        if (context == null) {
            context = new SubjectContextHolder();
            contextHolder.set(context);
        }
        return context;
    }

    public void clearContext() {
        contextHolder.remove();
    }

    private Subject subject;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

}
