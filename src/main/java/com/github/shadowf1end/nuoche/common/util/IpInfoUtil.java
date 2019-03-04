package com.github.shadowf1end.nuoche.common.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author Exrickx
 */
public class IpInfoUtil {

    private static final String LOCAL_IPV4_ADDRESS = "127.0.0.1";
    private static final String LOCAL_IPV6_ADDRESS = "0:0:0:0:0:0:0:1";
    private static final String UNKNOWN_ADDRESS = "unknown";
    private static final String PROXY_SEPARATOR = ",";
    private static final Integer MAX_IP_AMOUNT = 15;

    /**
     * 获取客户端IP地址
     *
     * @param request 请求
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN_ADDRESS.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_ADDRESS.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_ADDRESS.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOCAL_IPV4_ADDRESS.equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > MAX_IP_AMOUNT) {
            if (ip.indexOf(PROXY_SEPARATOR) > 0) {
                ip = ip.substring(0, ip.indexOf(PROXY_SEPARATOR));
            }
        }
        if (LOCAL_IPV6_ADDRESS.equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
