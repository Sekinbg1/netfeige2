package org.teleal.common.mock.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/* JADX INFO: loaded from: classes.dex */
public class MockHttpServletRequest implements HttpServletRequest {
    public static final String DEFAULT_PROTOCOL = "http";
    public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
    public static final String DEFAULT_REMOTE_HOST = "localhost";
    public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";
    public static final String DEFAULT_SERVER_NAME = "localhost";
    public static final int DEFAULT_SERVER_PORT = 80;
    private boolean active;
    private final Hashtable attributes;
    private String authType;
    private String characterEncoding;
    private byte[] content;
    private String contentType;
    private String contextPath;
    private Cookie[] cookies;
    private final Hashtable headers;
    private String localAddr;
    private String localName;
    private int localPort;
    private final Vector locales;
    private String method;
    private final Map parameters;
    private String pathInfo;
    private String protocol;
    private Map<String, String> queryParameters;
    private String queryString;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String remoteUser;
    private String requestURI;
    private boolean requestedSessionIdFromCookie;
    private boolean requestedSessionIdFromURL;
    private boolean requestedSessionIdValid;
    private String scheme;
    private boolean secure;
    private String serverName;
    private int serverPort;
    private final ServletContext servletContext;
    private String servletPath;
    private HttpSession session;
    private Principal userPrincipal;
    private Set<String> userRoles;

    public boolean isAllParametersInQueryString() {
        return true;
    }

    public MockHttpServletRequest() {
        this((ServletContext) null, "", "");
    }

    public MockHttpServletRequest(String str, String str2) {
        this((ServletContext) null, str, str2);
    }

    public MockHttpServletRequest(ServletContext servletContext) {
        this(servletContext, "", "");
    }

    public MockHttpServletRequest(ServletContext servletContext, String str, String str2) {
        this.active = true;
        this.attributes = new Hashtable();
        this.parameters = new LinkedHashMap(16);
        this.protocol = DEFAULT_PROTOCOL;
        this.scheme = DEFAULT_PROTOCOL;
        this.serverName = "localhost";
        this.serverPort = 80;
        this.remoteAddr = "127.0.0.1";
        this.remoteHost = "localhost";
        this.locales = new Vector();
        this.secure = false;
        this.remotePort = 80;
        this.localName = "localhost";
        this.localAddr = "127.0.0.1";
        this.localPort = 80;
        this.headers = new Hashtable();
        this.contextPath = "";
        this.queryParameters = new HashMap();
        this.userRoles = new HashSet();
        this.servletPath = "";
        this.requestedSessionIdValid = true;
        this.requestedSessionIdFromCookie = true;
        this.requestedSessionIdFromURL = false;
        this.servletContext = servletContext == null ? new MockServletContext() : servletContext;
        this.method = str;
        this.requestURI = str2;
        this.locales.add(Locale.ENGLISH);
        addHeader("Accept", new String[0]);
    }

    public MockHttpServletRequest(HttpSession httpSession) {
        this(httpSession, (String) null, new HashSet());
    }

    public MockHttpServletRequest(HttpSession httpSession, String str, Set<String> set) {
        this(httpSession, str, set, new Cookie[0], null);
    }

    public MockHttpServletRequest(HttpSession httpSession, final String str, Set<String> set, Cookie[] cookieArr, String str2) {
        this((ServletContext) null, str2, "");
        this.session = httpSession;
        this.userPrincipal = str != null ? new Principal() { // from class: org.teleal.common.mock.http.MockHttpServletRequest.1
            @Override // java.security.Principal
            public String getName() {
                return str;
            }
        } : null;
        this.userRoles = set;
        this.cookies = cookieArr;
        addHeader("Accept", new String[0]);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public boolean isActive() {
        return this.active;
    }

    public void close() {
        this.active = false;
    }

    public void invalidate() {
        close();
        clearAttributes();
    }

    protected void checkActive() throws IllegalStateException {
        if (!this.active) {
            throw new IllegalStateException("Request is not active anymore");
        }
    }

    public Object getAttribute(String str) {
        checkActive();
        return this.attributes.get(str);
    }

    public Enumeration getAttributeNames() {
        checkActive();
        return this.attributes.keys();
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String str) {
        this.characterEncoding = str;
    }

    public void setContent(byte[] bArr) {
        this.content = bArr;
    }

    public int getContentLength() {
        byte[] bArr = this.content;
        if (bArr != null) {
            return bArr.length;
        }
        return -1;
    }

    public void setContentType(String str) {
        this.contentType = str;
    }

    public String getContentType() {
        return this.contentType;
    }

    public ServletInputStream getInputStream() throws IOException {
        if (this.content != null) {
            return new DelegatingServletInputStream(new ByteArrayInputStream(this.content));
        }
        return null;
    }

    public void setParameter(String str, String str2) {
        setParameter(str, new String[]{str2});
    }

    public void setParameter(String str, String[] strArr) {
        this.parameters.put(str, strArr);
    }

    public void setParameters(Map map) {
        for (Object obj : map.keySet()) {
            Object obj2 = map.get(obj);
            if (obj2 instanceof String) {
                setParameter((String) obj, (String) obj2);
            } else if (obj2 instanceof String[]) {
                setParameter((String) obj, (String[]) obj2);
            } else {
                throw new IllegalArgumentException("Parameter map value must be single value  or array of type [" + String.class.getName() + "]");
            }
        }
    }

    public void addParameter(String str, String str2) {
        addParameter(str, new String[]{str2});
    }

    public void addParameter(String str, String[] strArr) {
        String[] strArr2 = (String[]) this.parameters.get(str);
        if (strArr2 != null) {
            String[] strArr3 = new String[strArr2.length + strArr.length];
            System.arraycopy(strArr2, 0, strArr3, 0, strArr2.length);
            System.arraycopy(strArr, 0, strArr3, strArr2.length, strArr.length);
            this.parameters.put(str, strArr3);
            return;
        }
        this.parameters.put(str, strArr);
    }

    public void addParameters(Map map) {
        for (Object obj : map.keySet()) {
            Object obj2 = map.get(obj);
            if (obj2 instanceof String) {
                addParameter((String) obj, (String) obj2);
            } else if (obj2 instanceof String[]) {
                addParameter((String) obj, (String[]) obj2);
            } else {
                throw new IllegalArgumentException("Parameter map value must be single value  or array of type [" + String.class.getName() + "]");
            }
        }
    }

    public void removeParameter(String str) {
        this.parameters.remove(str);
    }

    public void removeAllParameters() {
        this.parameters.clear();
    }

    public String getParameter(String str) {
        String[] strArr = (String[]) this.parameters.get(str);
        if (strArr == null || strArr.length <= 0) {
            return null;
        }
        return strArr[0];
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    public String[] getParameterValues(String str) {
        return (String[]) this.parameters.get(str);
    }

    public Map getParameterMap() {
        return this.parameters;
    }

    public Map<String, String[]> getParameters() {
        return this.parameters;
    }

    public void addQueryParameter(String str, String str2) {
        addParameter(str, str2);
        this.queryParameters.put(str, str2);
    }

    public void removeQueryParameter(String str) {
        removeParameter(str);
        this.queryParameters.remove(str);
    }

    public Map<String, String> getQueryParameters() {
        return this.queryParameters;
    }

    public void setProtocol(String str) {
        this.protocol = str;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setScheme(String str) {
        this.scheme = str;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setServerName(String str) {
        this.serverName = str;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerPort(int i) {
        this.serverPort = i;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public BufferedReader getReader() throws UnsupportedEncodingException {
        if (this.content == null) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.content);
        return new BufferedReader(this.characterEncoding != null ? new InputStreamReader(byteArrayInputStream, this.characterEncoding) : new InputStreamReader(byteArrayInputStream));
    }

    public void setRemoteAddr(String str) {
        this.remoteAddr = str;
    }

    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public void setRemoteHost(String str) {
        this.remoteHost = str;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setAttribute(String str, Object obj) {
        checkActive();
        if (obj != null) {
            this.attributes.put(str, obj);
        } else {
            this.attributes.remove(str);
        }
    }

    public void removeAttribute(String str) {
        checkActive();
        this.attributes.remove(str);
    }

    public void clearAttributes() {
        this.attributes.clear();
    }

    public void addPreferredLocale(Locale locale) {
        this.locales.add(0, locale);
    }

    public Locale getLocale() {
        return (Locale) this.locales.get(0);
    }

    public Enumeration getLocales() {
        return this.locales.elements();
    }

    public void setSecure(boolean z) {
        this.secure = z;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public RequestDispatcher getRequestDispatcher(String str) {
        return new MockRequestDispatcher(str);
    }

    public String getRealPath(String str) {
        return this.servletContext.getRealPath(str);
    }

    public void setRemotePort(int i) {
        this.remotePort = i;
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public void setLocalName(String str) {
        this.localName = str;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalAddr(String str) {
        this.localAddr = str;
    }

    public String getLocalAddr() {
        return this.localAddr;
    }

    public void setLocalPort(int i) {
        this.localPort = i;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void setAuthType(String str) {
        this.authType = str;
    }

    public String getAuthType() {
        return this.authType;
    }

    public void setCookies(Cookie[] cookieArr) {
        this.cookies = cookieArr;
    }

    public Cookie[] getCookies() {
        return this.cookies;
    }

    public void addCookie(Cookie cookie) {
        Cookie[] cookieArr = new Cookie[this.cookies.length + 1];
        this.cookies = cookieArr;
        cookieArr[cookieArr.length - 1] = cookie;
    }

    public void addHeader(String str, Object obj) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        if (byName == null) {
            byName = new HeaderValueHolder();
            this.headers.put(str, byName);
        }
        if (obj instanceof Collection) {
            byName.addValues((Collection) obj);
        } else if (obj.getClass().isArray()) {
            byName.addValueArray(obj);
        } else {
            byName.addValue(obj);
        }
    }

    public long getDateHeader(String str) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        Object value = byName != null ? byName.getValue() : null;
        if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value == null) {
            return -1L;
        }
        throw new IllegalArgumentException("Value for header '" + str + "' is neither a Date nor a Number: " + value);
    }

    public String getHeader(String str) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        if (byName == null || byName.getValue() == null) {
            return null;
        }
        return byName.getValue().toString();
    }

    public Map<String, String[]> getHeaders() {
        HashMap map = new HashMap();
        for (Map.Entry entry : this.headers.entrySet()) {
            map.put(entry.getKey(), (String[]) ((HeaderValueHolder) entry.getValue()).getValues().toArray(new String[((HeaderValueHolder) entry.getValue()).getValues().size()]));
        }
        return map;
    }

    public Enumeration getHeaders(String str) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        return Collections.enumeration(byName != null ? byName.getValues() : Collections.EMPTY_LIST);
    }

    public Enumeration getHeaderNames() {
        return this.headers.keys();
    }

    public int getIntHeader(String str) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        Object value = byName != null ? byName.getValue() : null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        if (value == null) {
            return -1;
        }
        throw new NumberFormatException("Value for header '" + str + "' is not a Number: " + value);
    }

    public void setMethod(String str) {
        this.method = str;
    }

    public String getMethod() {
        return this.method;
    }

    public void setPathInfo(String str) {
        this.pathInfo = str;
    }

    public String getPathInfo() {
        return this.pathInfo;
    }

    public String getPathTranslated() {
        String str = this.pathInfo;
        if (str != null) {
            return getRealPath(str);
        }
        return null;
    }

    public void setContextPath(String str) {
        this.contextPath = str;
    }

    public String getContextPath() {
        String str = this.contextPath;
        return str != null ? str : "/project";
    }

    public void setQueryString(String str) {
        this.queryString = str;
    }

    public String getQueryString() {
        if (getQueryParameters().size() > 0) {
            StringBuilder sb = new StringBuilder(this.queryString);
            if (!this.queryString.endsWith("&")) {
                sb.append("&");
            }
            for (Map.Entry<String, String> entry : getQueryParameters().entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }
            if (sb.toString().endsWith("&")) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }
        return this.queryString;
    }

    public void setRemoteUser(String str) {
        this.remoteUser = str;
    }

    public String getRemoteUser() {
        return this.remoteUser;
    }

    public void addRole(String str) {
        addUserRole(str);
    }

    public void addUserRole(String str) {
        this.userRoles.add(str);
    }

    public boolean isUserInRole(String str) {
        return this.userRoles.contains(str);
    }

    public void setUserPrincipal(Principal principal) {
        this.userPrincipal = principal;
    }

    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }

    public String getRequestedSessionId() {
        HttpSession session = getSession();
        if (session != null) {
            return session.getId();
        }
        return null;
    }

    public void setRequestURI(String str) {
        this.requestURI = str;
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    public StringBuffer getRequestURL() {
        StringBuffer stringBuffer = new StringBuffer(this.scheme);
        stringBuffer.append("://");
        stringBuffer.append(this.serverName);
        stringBuffer.append(':');
        stringBuffer.append(this.serverPort);
        stringBuffer.append(getRequestURI());
        return stringBuffer;
    }

    public void setServletPath(String str) {
        this.servletPath = str;
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public void setSession(HttpSession httpSession) {
        this.session = httpSession;
    }

    public HttpSession getSession(boolean z) {
        checkActive();
        HttpSession httpSession = this.session;
        if ((httpSession instanceof MockHttpSession) && ((MockHttpSession) httpSession).isInvalid()) {
            this.session = null;
        }
        if (this.session == null && z) {
            this.session = new MockHttpSession(this.servletContext);
        }
        return this.session;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public void setRequestedSessionIdValid(boolean z) {
        this.requestedSessionIdValid = z;
    }

    public boolean isRequestedSessionIdValid() {
        return this.requestedSessionIdValid;
    }

    public void setRequestedSessionIdFromCookie(boolean z) {
        this.requestedSessionIdFromCookie = z;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return this.requestedSessionIdFromCookie;
    }

    public void setRequestedSessionIdFromURL(boolean z) {
        this.requestedSessionIdFromURL = z;
    }

    public boolean isRequestedSessionIdFromURL() {
        return this.requestedSessionIdFromURL;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }
}

