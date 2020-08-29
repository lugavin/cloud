package com.gavin.cloud.gateway.filter;

import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.AclProperties;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.sys.api.PermissionApi;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 用户鉴权过滤器
 */
@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    private final PermissionApi permissionApi;
    private final PathMatcher pathMatcher;
    private final JwtProperties jwtProperties;
    private final AclProperties aclProperties;
    private final ZuulProperties zuulProperties;

    public AuthFilter(ObjectProvider<PermissionApi> permissionProvider, JwtProperties jwtProperties,
                      AclProperties aclProperties, ZuulProperties zuulProperties) {
        this.permissionApi = Objects.requireNonNull(permissionProvider.getIfAvailable());
        this.pathMatcher = new AntPathMatcher();
        this.jwtProperties = jwtProperties;
        this.aclProperties = aclProperties;
        this.zuulProperties = zuulProperties;

    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        // 匿名访问地址
        return !isAnonUrl(stripPathPrefix(RequestContext.getCurrentContext().getRequest().getRequestURI()));
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = stripPathPrefix(request.getRequestURI());
        Optional<ActiveUser> optional = resolveSubject(request);
        if (!optional.isPresent()) {
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setSendZuulResponse(Boolean.FALSE);
            log.warn("Access Control: filtered unauthorized access on endpoint {}", requestURI);
            return false;
        }
        ActiveUser subject = optional.get();
        log.info("Access: {}, Subject: {}", requestURI, subject);
        if (isAuthUrl(requestURI)) {
            // 授权访问地址
            if (!isPermitted(subject, requestURI, request.getMethod())) {
                ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                ctx.setSendZuulResponse(Boolean.FALSE);
                log.warn("Access Control: filtered denied access on endpoint {}", requestURI);
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

    private boolean isPermitted(ActiveUser subject, String url, String method) {
        return permissionApi.getPerms(subject.getRoles().toArray(new String[0])).stream()
                .anyMatch(p -> pathMatcher.match(p.getUrl(), url) && p.getMethod().equalsIgnoreCase(method));
    }

    private String stripPathPrefix(String url) {
        return url.startsWith(zuulProperties.getPrefix()) ? url.substring(zuulProperties.getPrefix().length()) : url;
    }

    private boolean isAnonUrl(String url) {
        return matchPath(aclProperties.getAnonUrls(), url);
    }

    private boolean isUserUrl(String url) {
        return matchPath(aclProperties.getUserUrls(), url);
    }

    private boolean isAuthUrl(String url) {
        return matchPath(aclProperties.getAuthUrls(), url);
    }

    private boolean matchPath(Set<String> acl, String url) {
        return acl.stream().anyMatch(p -> pathMatcher.match(p, url));
    }

}
