package com.gavin.cloud.common.web.service;

import com.gavin.cloud.common.base.subject.Subject;
import com.gavin.cloud.common.base.subject.SubjectService;
import com.gavin.cloud.common.base.util.Constants;
import org.springframework.web.context.request.RequestContextHolder;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;

public class SubjectServiceImpl implements SubjectService {

    @Override
    public void setSubject(Subject subject) {
        SubjectContext.set(subject);
    }

    @Override
    public Subject getSubject() {
        return SubjectContext.get();
    }

    @Override
    public void removeSubject() {
        SubjectContext.remove();
    }

    /**
     * @see org.springframework.web.context.request.RequestContextHolder
     */
    private static class SubjectContext {

        static void set(Subject subject) {
            setAttribute(Constants.SESSION_NAMESPACE, subject);
        }

        static Subject get() {
            return (Subject) getAttribute(Constants.SESSION_NAMESPACE);
        }

        static void remove() {
            removeAttribute(Constants.SESSION_NAMESPACE);
        }

        private static Object getAttribute(String attributeName) {
            return RequestContextHolder.getRequestAttributes().getAttribute(attributeName, SCOPE_SESSION);
        }

        private static void setAttribute(String attributeName, Object attributeValue) {
            if (attributeValue != null) {
                RequestContextHolder.getRequestAttributes().setAttribute(attributeName, attributeValue, SCOPE_SESSION);
            } else {
                removeAttribute(attributeName);
            }
        }

        private static void removeAttribute(String attributeName) {
            RequestContextHolder.getRequestAttributes().removeAttribute(attributeName, SCOPE_SESSION);
        }

    }

}
