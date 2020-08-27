package com.gavin.cloud.gateway.filter;

import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.AuthProperties;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        return !isAnonUrl(RequestContext.getCurrentContext().getRequest().getRequestURI());
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();
        Optional<ActiveUser> optional = resolveSubject(request);
        if (!optional.isPresent()) {
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setSendZuulResponse(Boolean.FALSE);
            log.debug("Access Control: filtered unauthorized access on endpoint {}", requestURI);
            return null;
        }
        ActiveUser activeUser = optional.get();
        if (!isUserUrl(requestURI)) {
            // TODO 授权访问地址
        }
        // 公共访问地址
        return activeUser;
    }

    private Optional<ActiveUser> resolveSubject(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(c -> jwtProperties.getCookie().getName().equals(c.getName()))
                .findFirst()
                .map(c -> {
                    try {
                        return JwtHelper.verifyToken(c.getValue(), jwtProperties.getPublicKey());
                    } catch (Exception e) {
                        log.error("The token is illegal.", e);
                        return null;
                    }
                });
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
