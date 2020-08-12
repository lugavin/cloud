package com.gavin.cloud.gateway.filter;

import com.gavin.cloud.common.base.auth.AuthProperties;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.common.base.problem.AuthenticationException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;

    public AuthFilter(AuthProperties authProperties, JwtProperties jwtProperties) {
        this.authProperties = authProperties;
        this.jwtProperties = jwtProperties;
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
        // (1)判断是否为匿名访问地址(不用登录即可访问)
        return !isAnonUrl(RequestContext.getCurrentContext().getRequest().getRequestURI());
    }

    @Override
    public Object run() {
        // (2)判断Token是否有效
        RequestContext ctx = RequestContext.getCurrentContext();

        try {
            Cookie cookie = Arrays.stream(ctx.getRequest().getCookies())
                    .filter(c -> jwtProperties.getCookie().getName().equals(c.getName()))
                    .findFirst()
                    .orElseThrow(AuthenticationException::new);
            return JwtHelper.verifyToken(cookie.getValue(), jwtProperties.getPublicKey());
            // (3)判断是否为公共访问地址(登录后均可访问)
            // (4)判断是否为授权访问地址(登录后需要授权才可访问)
        } catch (Exception e) {
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setSendZuulResponse(Boolean.FALSE);
            log.debug("Access Control: filtered unauthorized access on endpoint {}", ctx.getRequest().getRequestURI());
        }
        return null;
    }

    private boolean isAnonUrl(String url) {
        return match(url, authProperties.getAnonUrls());
    }

    private boolean isUserUrl(String url) {
        return match(url, authProperties.getUserUrls());
    }

    private boolean isAuthUrl(String url) {
        return match(url, authProperties.getAuthUrls());
    }

    private boolean match(String path, List<String> patterns) {
        return patterns.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

}
