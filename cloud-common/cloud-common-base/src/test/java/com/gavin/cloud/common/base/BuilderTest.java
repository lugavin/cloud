package com.gavin.cloud.common.base;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

@Slf4j
public class BuilderTest {

    @Test
    public void testBuild() {
        MethodLock methodLock = MethodLock.builder()
                .id(UUID.randomUUID().toString())
                .methodName("CreateUser")
                .updatedAt(new Date())
                .build();
        log.debug("====== {} ======", methodLock);
    }


    @ToString
    private static class MethodLock {

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

            /**
             * JDK默认会把当前实例传入到非静态方法中, 参数名为this, 参数位置是第一个:
             * <pre>{@code
             *   public MethodLock build(MethodLockBuilder this) {
             *     return new MethodLock(this.id, this.methodName, this.updatedAt);
             *   }
             * }</pre>
             */
            public MethodLock build() {
                return new MethodLock(this.id, this.methodName, this.updatedAt);
            }

        }

    }

}
