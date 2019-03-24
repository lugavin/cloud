package com.gavin.cloud.common.web.util;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import javax.servlet.http.HttpServletRequest;

/**
 * @see <a href="https://github.com/alibaba/druid/blob/master/src/main/java/com/alibaba/druid/util/DruidWebUtils.java">DruidWebUtils</a>
 * @see <a href="https://github.com/jhipster/generator-jhipster/blob/master/generators/server/templates/src/main/java/package/security/oauth2/OAuth2CookieHelper.java.ejs">OAuth2CookieHelper</a>
 */
public abstract class WebUtils {

    /**
     * Returns the top level domain of the server from the request. This is used to limit the Cookie
     * to the top domain instead of the full domain name.
     * <p>
     * A lot of times, individual gateways of the same domain get their own subdomain but authentication
     * shall work across all subdomains of the top level domain.
     * <p>
     * For example, when sending a request to <code>api.domain.com</code>, this returns <code>domain.com</code>.
     *
     * @param request the HTTP request we received from the client.
     * @return the top level domain to set the cookies for.
     * Returns null if the domain is not under a public suffix (.com, .co.uk), e.g. for localhost.
     */
    public static String getCookieDomain(HttpServletRequest request) {
        //if not explicitly defined, use top-level domain
        String domain = request.getServerName().toLowerCase();
        //strip off leading www.
        if (domain.startsWith("www.")) {
            domain = domain.substring(4);
        }
        //if it isn't an IP address
        if (!InetAddresses.isInetAddress(domain)) {
            //strip off subdomains, leaving the top level domain only
            InternetDomainName domainName = InternetDomainName.from(domain);
            if (domainName.isUnderPublicSuffix() && !domainName.isTopPrivateDomain()) {
                return domainName.topPrivateDomain().toString();
            }
        }
        //no top-level domain, stick with default domain
        return null;
    }

    /**
     * 通过{@link HttpServletRequest#getRemoteAddr}方法获取客户端IP地址适用于大多数情况,
     * 但在通过Apache或Nginx等软件反向代理后, 用该方法获取的IP地址是127.0.0.1, 并不是客户端的真实IP.
     * 经过代理后, 由于客户端和服务之间增加了中间层, 因此服务器无法直接拿到客户端的IP,
     * 但可通过在转发请求的HTTP头信息中添加X-Forwarded-For信息用以跟踪原有的客户端IP地址和原来客户端请求的服务器地址.
     *
     * @param request HttpServletRequest
     * @return ClientIP
     */
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!isValidAddress(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!isValidAddress(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!isValidAddress(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For: client1, proxy1, proxy2
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }

    private static boolean isValidAddress(String ip) {
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        for (int i = 0, j = ip.length(); i < j; ++i) {
            char ch = ip.charAt(i);
            if ((ch >= '0' && ch <= '9')
                    || (ch >= 'A' && ch <= 'F')
                    || (ch >= 'a' && ch <= 'f')
                    || (ch == '.' || ch == ':')) {
                continue;
            }
            return false;
        }
        return true;
    }

}
