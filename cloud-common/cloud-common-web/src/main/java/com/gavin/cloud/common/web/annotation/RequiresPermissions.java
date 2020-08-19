package com.gavin.cloud.common.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * (1)基于资源的权限控制(即使角色名称发生改变, 程序代码也无需修改, 能很好适应变化, 建议采用此种方案)
 * (2)基于角色的权限控制(由于角色名称可能会发生改变, 程序代码也要随之修改, 因此基于角色的权限控制不灵活, 可扩展性不强)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    String[] value() default {};

    Logical logical() default Logical.AND;

}
