package com.gavin.cloud.common.web.util;

import com.gavin.cloud.common.base.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public abstract class RequestUtils {

    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final String X_REAL_IP_HEADER = "X-Real-IP";

    private static final String UNKNOWN_ADDR = "unKnown";
    private static final String COMMA = Constants.SEPARATOR_COMMA;

    private static UrlPathHelper urlPathHelper = new UrlPathHelper();

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (StringUtils.isNotEmpty(ip) && !UNKNOWN_ADDR.equalsIgnoreCase(ip)) {
            int index = ip.indexOf(COMMA);
            return index != -1 ? ip.substring(0, index) : ip;
        }
        ip = request.getHeader(X_REAL_IP_HEADER);
        if (StringUtils.isNotEmpty(ip) && !UNKNOWN_ADDR.equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static String getAccessToken(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        }
        String token = request.getHeader(name);
        return StringUtils.isNotBlank(token) ? token : request.getParameter(name);
    }

    public static String getPathWithinApplication(HttpServletRequest request) {
        return urlPathHelper.getPathWithinApplication(request);
    }

}
