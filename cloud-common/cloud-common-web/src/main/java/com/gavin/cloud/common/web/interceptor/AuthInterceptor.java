package com.gavin.cloud.common.web.interceptor;

import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.web.annotation.Logical;
import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import com.gavin.cloud.common.web.context.SubjectContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.gavin.cloud.common.base.exception.DefaultProblemType.AUTHENTICATION_FAILED_TYPE;

/**
 * 权限设计思路
 * (1)登录
 * 服务端: DB => RefreshToken & Redis => {roleCode: permCode: permission}
 * 客户端: LocalStorage => AccessToken & RefreshToken
 * (2)认证
 * 会话验证: 验证Token是否合法, 并支持是否需要会话的一个URL配置, 如[忘记密码]并不需要会话.
 * 权限验证: 通过AOP实现, 在Controller相应方法上添加自定义注解{@link RequiresPermissions}来进行权限验证.
 * (3)基于URL的权限校验
 * 缺点: 对于RESTful风格的API不适用, 如 http://api.domain.com/users 可以是新增(POST)或更新(PUT)操作
 * 解决方案: 通过AOP实现, 在Controller相应方法上添加自定义注解{@link RequiresPermissions}来进行权限验证
 */
@Slf4j
public class AuthInterceptor extends AbstractInterceptor {

    private final JwtProperties jwtProperties;

    public AuthInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        // 判断是否为匿名访问地址(不用登录即可访问)
        RequiresPermissions permissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
        if (permissions == null) {
            return true;
        }

        // 需要登录才可访问
        try {
            Cookie cookie = Arrays.stream(request.getCookies())
                    .filter(c -> jwtProperties.getCookie().getName().equals(c.getName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(AUTHENTICATION_FAILED_TYPE));
            ActiveUser activeUser = JwtHelper.verifyToken(cookie.getValue(), jwtProperties.getPublicKey());
            SubjectContextHolder.getContext().setSubject(activeUser);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.debug("Access Control: filtered unauthorized access on endpoint {}", request.getRequestURI());
            return false;
        }

        // 判断是否为公共访问地址(登录后均可访问)

        if (permissions.value().length == 0) {
            return true;
        }

        // 判断是否为授权访问地址(登录后需要授权才可访问)
        ActiveUser subject = SubjectContextHolder.getContext().getSubject();
        if (!isPermitted(subject, permissions.value(), permissions.logical())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            log.debug("Access Control: filtered denied access on endpoint {}", request.getRequestURI());
            return false;
        }

        return true;
    }

    /**
     * 由于所有的Servlet容器(如Tomcat)都采用了线程池, 因此, 在请求处理完成后, 需要将ThreadLocal保存的数据清空, 否则可能出现意想不到的情况.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SubjectContextHolder.getContext().clearContext();
    }

    private boolean isPermitted(ActiveUser subject, String[] permissions, Logical logical) {
        // Set<String> perms = sysApi.getPermCodes(subject.getRoles().toArray(new String[0]));
        Set<String> perms = new HashSet<>(); // TODO 从授权服务获取
        return logical == Logical.AND
                ? perms.containsAll(Arrays.asList(permissions))
                : Arrays.stream(permissions).anyMatch(perms::contains);
    }

}

