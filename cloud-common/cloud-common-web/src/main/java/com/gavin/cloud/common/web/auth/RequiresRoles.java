package com.gavin.cloud.common.web.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于角色的权限控制(由于角色名称可能会发生改变, 程序代码也要随之修改, 因此基于角色的权限控制不灵活, 可扩展性不强)
 */
@Deprecated
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRoles {

    String[] value();

    Logical logical() default Logical.AND;
}
