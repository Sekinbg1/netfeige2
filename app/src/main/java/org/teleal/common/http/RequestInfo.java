package org.teleal.common.http;

import java.util.Enumeration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/* JADX INFO: loaded from: classes.dex */
public class RequestInfo {
    public static void reportRequest(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("Request: ");
        sb.append(httpServletRequest.getMethod());
        sb.append(' ');
        sb.append(httpServletRequest.getRequestURL());
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            sb.append('?');
            sb.append(queryString);
        }
        sb.append(" - ");
        String requestedSessionId = httpServletRequest.getRequestedSessionId();
        if (requestedSessionId != null) {
            sb.append("\nSession ID: ");
        }
        if (requestedSessionId == null) {
            sb.append("No Session");
            return;
        }
        if (httpServletRequest.isRequestedSessionIdValid()) {
            sb.append(requestedSessionId);
            sb.append(" (from ");
            if (httpServletRequest.isRequestedSessionIdFromCookie()) {
                sb.append("cookie)\n");
                return;
            } else if (httpServletRequest.isRequestedSessionIdFromURL()) {
                sb.append("url)\n");
                return;
            } else {
                sb.append("unknown)\n");
                return;
            }
        }
        sb.append("Invalid Session ID\n");
    }

    public static void reportParameters(StringBuilder sb, HttpServletRequest httpServletRequest) {
        Enumeration parameterNames = httpServletRequest.getParameterNames();
        if (parameterNames != null && parameterNames.hasMoreElements()) {
            sb.append("Parameters:\n");
            while (parameterNames.hasMoreElements()) {
                String str = (String) parameterNames.nextElement();
                String[] parameterValues = httpServletRequest.getParameterValues(str);
                if (parameterValues != null) {
                    for (String str2 : parameterValues) {
                        sb.append("    ");
                        sb.append(str);
                        sb.append(" = ");
                        sb.append(str2);
                        sb.append('\n');
                    }
                }
            }
        }
    }

    public static void reportHeaders(StringBuilder sb, HttpServletRequest httpServletRequest) {
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        if (headerNames != null && headerNames.hasMoreElements()) {
            sb.append("Headers:\n");
            while (headerNames.hasMoreElements()) {
                String str = (String) headerNames.nextElement();
                String header = httpServletRequest.getHeader(str);
                sb.append("    ");
                sb.append(str);
                sb.append(": ");
                sb.append(header);
                sb.append('\n');
            }
        }
    }

    public static void reportCookies(StringBuilder sb, HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null && (cookies.length) > 0) {
            sb.append("Cookies:\n");
            for (Cookie cookie : cookies) {
                sb.append("    ");
                sb.append(cookie.getName());
                sb.append(" = ");
                sb.append(cookie.getValue());
                sb.append('\n');
            }
        }
    }

    public static void reportClient(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("Remote Address: ");
        sb.append(httpServletRequest.getRemoteAddr());
        sb.append("\n");
        if (!httpServletRequest.getRemoteAddr().equals(httpServletRequest.getRemoteHost())) {
            sb.append("Remote Host: ");
            sb.append(httpServletRequest.getRemoteHost());
            sb.append("\n");
        }
        sb.append("Remote Port: ");
        sb.append(httpServletRequest.getRemotePort());
        sb.append("\n");
        if (httpServletRequest.getRemoteUser() != null) {
            sb.append("Remote User: ");
            sb.append(httpServletRequest.getRemoteUser());
            sb.append("\n");
        }
    }
}

