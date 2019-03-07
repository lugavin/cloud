package com.gavin.cloud.common.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "app.auth", ignoreUnknownFields = false)
public class AppAuthProperties {

    private static final String ANON_KEY = "anon";
    private static final String USER_KEY = "user";
    private static final String AUTHC_KEY = "authc";

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final Set<String> anonUrls;     // 匿名访问地址(不用登录即可访问)
    private final Set<String> userUrls;     // 公共访问地址(登录后就可访问)
    private final Set<String> authcUrls;    // 授权访问地址(需要授权方可访问)

    private Map<String, String> accessUrls;

    public AppAuthProperties() {
        this.accessUrls = new LinkedHashMap<>();
        this.anonUrls = new HashSet<>();
        this.userUrls = new HashSet<>();
        this.authcUrls = new HashSet<>();
    }

    public Map<String, String> getAccessUrls() {
        return accessUrls;
    }

    public void setAccessUrls(Map<String, String> accessUrls) {
        this.accessUrls = accessUrls;
    }

    public Set<String> getAnonUrls() {
        return anonUrls;
    }

    public Set<String> getUserUrls() {
        return userUrls;
    }

    public Set<String> getAuthcUrls() {
        return authcUrls;
    }

    public boolean isAnonUrl(String url) {
        return match(url, anonUrls);
    }

    public boolean isUserUrl(String url) {
        return match(url, userUrls);
    }

    public boolean isAuthcUrl(String url) {
        return match(url, authcUrls);
    }

    @PostConstruct
    public void initAccessUrls() throws IllegalArgumentException {
        for (Map.Entry<String, String> entry : accessUrls.entrySet()) {
            String key = entry.getValue();
            if (ANON_KEY.equalsIgnoreCase(key)) {
                anonUrls.add(entry.getKey());
            } else if (USER_KEY.equalsIgnoreCase(key)) {
                userUrls.add(entry.getKey());
            } else if (AUTHC_KEY.equalsIgnoreCase(key)) {
                authcUrls.add(entry.getKey());
            } else {
                throw new IllegalArgumentException("The value [ " + key + " ] is illegal!");
            }
        }
    }

    private boolean match(String path, Set<String> patterns) {
        return patterns.stream().anyMatch(p -> pathMatcher.match(p, path));
    }
}
