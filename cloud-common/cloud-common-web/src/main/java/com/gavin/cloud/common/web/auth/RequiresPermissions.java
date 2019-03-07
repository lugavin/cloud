package com.gavin.cloud.common.web.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于资源的权限控制(即使角色名称发生改变, 程序代码也无需修改, 能很好适应变化, 建议采用此种方案)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    String[] value();

    Logical logical() default Logical.AND;

}
