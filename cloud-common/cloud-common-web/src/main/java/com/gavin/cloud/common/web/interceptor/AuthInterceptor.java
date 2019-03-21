package com.gavin.cloud.common.web.interceptor;

import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限设计思路
 * (1)登录
 * 服务端: Redis => {Prefix+Token: Subject} Cookie => {X-Access-Token: Token}
 * 客户端: LocalStorage => {Permission: [Permissions]}
 * (2)认证
 * 会话验证: 验证Token是否合法, 并支持是否需要会话的一个URL配置, 如[忘记密码]并不需要会话.
 * 权限验证: 通过AOP实现, 在Controller相应方法前添加自定义注解{@link RequiresPermissions}来进行权限验证.
 * (权限动态更新: 管理员更新指定用户权限后, 需要对Redis中的该用户的权限数据进行同步, 然后通过WebSocket更新浏览器LocalStorage中的权限数据)
 */
public class AuthInterceptor extends AbstractInterceptor {

//    private final SubjectService subjectService;
//
//    public AuthInterceptor(SubjectService subjectService) {
//        this.subjectService = subjectService;
//    }

    @Override
    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

/*        // 判断是否为匿名访问地址(不用登录即可访问)
        if (handlerMethod.hasMethodAnnotation(RequiresGuest.class)) {
            return true;
        }

        // 判断是否为公共访问地址(登录后均可访问)
        Subject subject = subjectService.getSubject();
        if (subject == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        if (handlerMethod.hasMethodAnnotation(RequiresUser.class)) {
            return true;
        }

        // 判断是否为授权访问地址(登录后需要授权才可访问)
        RequiresPermissions permissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
        if (permissions != null) {
            if (!isPermitted(subject, permissions.value(), permissions.logical())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }*/

        return true;

    }

    // private boolean isPermitted(Subject subject, String[] permissions, Logical logical) {
    //     return logical == Logical.AND ? subject.isPermittedAll(permissions) : subject.isPermittedAny(permissions);
    // }

}

