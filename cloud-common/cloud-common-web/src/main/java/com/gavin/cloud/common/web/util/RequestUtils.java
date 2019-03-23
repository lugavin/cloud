package com.gavin.cloud.common.web.util;

import com.gavin.cloud.common.base.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

public abstract class RequestUtils {

    private static final String EMPTY = "";

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

    public static String getPathWithinApplication(HttpServletRequest request) {
        return urlPathHelper.getPathWithinApplication(request);
    }

    public static String getDomainName(HttpServletRequest request) {
        String domainName;
        String serverName = request.getRequestURL().toString();
        if (EMPTY.equals(serverName)) {
            domainName = EMPTY;
        } else {
            serverName = serverName.toLowerCase().substring(7);
            final int end = serverName.indexOf("/");
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn
                domainName = domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName.indexOf(":") > 0) {
            domainName = domainName.split(":")[0];
        }

        return domainName;
    }

}
