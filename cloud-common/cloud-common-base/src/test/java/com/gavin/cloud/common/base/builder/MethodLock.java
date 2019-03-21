package com.gavin.cloud.common.base.builder;

import lombok.ToString;

import java.util.Date;

@ToString
public class MethodLock {

    private final String id;
    private final String methodName;
    private final Date updatedAt;

    public MethodLock(String id, String methodName, Date updatedAt) {
        this.id = id;
        this.methodName = methodName;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getMethodName() {
        return methodName;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public static MethodLockBuilder builder() {
        return new MethodLockBuilder();
    }

    public static class MethodLockBuilder {

        private String id;
        private String methodName;
        private Date updatedAt;

        private MethodLockBuilder() {
        }

        public MethodLockBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MethodLockBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public MethodLockBuilder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MethodLock build() {
            return new MethodLock(this.id, this.methodName, this.updatedAt);
        }

    }

}