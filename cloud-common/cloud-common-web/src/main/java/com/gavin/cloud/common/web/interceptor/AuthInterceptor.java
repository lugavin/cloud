package com.gavin.cloud.common.web.interceptor;

import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.common.web.annotation.Logical;
import com.gavin.cloud.common.web.annotation.RequiresPermissions;
import com.gavin.cloud.common.web.context.SubjectContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 用户鉴权拦截器(使用Zuul网关AuthFilter过滤器代替)
 * (1)登录
 * 服务端: DB => RefreshToken & Redis => {roleCode:permCode:permission}
 * 客户端: LocalStorage => AccessToken & RefreshToken
 * (2)认证
 * 会话验证: 验证Token是否合法, 并支持是否需要会话的一个URL配置, 如[忘记密码]并不需要会话.
 * 权限验证: 通过AOP实现, 在Controller相应方法上添加自定义注解{@link RequiresPermissions}来进行权限验证.
 * <p>
 * {@link HandlerInterceptor#preHandle} 在进入controller方法之前执行(应用场景：用户身份认证、权限校验)
 * {@link HandlerInterceptor#postHandle} 在进入controller方法之后、返回modelAndView之前执行(应用场景：统一指定公用的模型视图数据, 如菜单导航)
 * {@link HandlerInterceptor#afterCompletion} 在controller方法执行完成之后执行(应用场景：统一异常处理和日志处理)
 */
@Slf4j
@Deprecated
public class AuthInterceptor implements HandlerInterceptor {

    private final PermissionApi permissionApi;
    private final JwtProperties jwtProperties;

    public AuthInterceptor(ObjectProvider<PermissionApi> permissionProvider, JwtProperties jwtProperties) {
        this.permissionApi = Objects.requireNonNull(permissionProvider.getIfAvailable());
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return !(handler instanceof HandlerMethod) || doPreHandle(request, response, (HandlerMethod) handler);
    }

    protected boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        RequiresPermissions permissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
        if (permissions == null) {
            // 匿名访问地址
            return true;
        }
        Optional<ActiveUser> optional = resolveSubject(request);
        if (!optional.isPresent()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.warn("Access Control: filtered unauthorized access on endpoint {}", request.getRequestURI());
            return false;
        }
        ActiveUser subject = optional.get();
        log.info("Access: {}, Subject: {}", request.getRequestURI(), subject);
        SubjectContext.getContext().setSubject(subject);
        if (permissions.value().length > 0) {
            // 授权访问地址
            if (!isPermitted(subject, permissions.value(), permissions.logical())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                log.warn("Access Control: filtered denied access on endpoint {}", request.getRequestURI());
                return false;
            }
        }
        // 公共访问地址
        return true;
    }

    private Optional<ActiveUser> resolveSubject(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> jwtProperties.getCookie().getName().equals(c.getName()))
                .findFirst()
                .map(c -> {
                    try {
                        return JwtHelper.verifyToken(c.getValue(), jwtProperties.getPublicKey());
                    } catch (Exception e) {
                        log.error("The access token is illegal.", e);
                        return null;
                    }
                });
    }

    private boolean isPermitted(ActiveUser subject, String[] permissions, Logical logical) {
        Set<String> perms = permissionApi.getPermCodes(subject.getRoles().toArray(new String[0]));
        return logical == Logical.AND ? perms.containsAll(Arrays.asList(permissions)) : Arrays.stream(permissions).anyMatch(perms::contains);
    }

    /**
     * TODO 仅占位使用避免编译失败, 后续需通过 cloud-sys 服务API获取
     */
    private interface PermissionApi {
        Set<String> getPermCodes(String[] roles);
    }

}

