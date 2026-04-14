package org.teleal.common.mock.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/* JADX INFO: loaded from: classes.dex */
public class MockHttpSession implements HttpSession {
    private Map<String, Object> attributes = new HashMap();
    private boolean isInvalid;
    private int maxInactiveInterval;
    private ServletContext servletContext;

    public long getCreationTime() {
        return 0L;
    }

    public String getId() {
        return null;
    }

    public long getLastAccessedTime() {
        return 0L;
    }

    public boolean isNew() {
        return false;
    }

    public MockHttpSession() {
    }

    public MockHttpSession(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public boolean isInvalid() {
        return this.isInvalid;
    }

    public void setMaxInactiveInterval(int i) {
        this.maxInactiveInterval = i;
    }

    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    public Object getAttribute(String str) {
        return this.attributes.get(str);
    }

    public Object getValue(String str) {
        return getAttribute(str);
    }

    public Enumeration getAttributeNames() {
        return new IteratorEnumeration(this.attributes.keySet().iterator());
    }

    public String[] getValueNames() {
        return (String[]) this.attributes.keySet().toArray(new String[0]);
    }

    public void setAttribute(String str, Object obj) {
        if (obj == null) {
            this.attributes.remove(str);
        } else {
            this.attributes.put(str, obj);
        }
    }

    public void putValue(String str, Object obj) {
        setAttribute(str, obj);
    }

    public void removeAttribute(String str) {
        this.attributes.remove(str);
    }

    public void removeValue(String str) {
        removeAttribute(str);
    }

    public void invalidate() {
        this.attributes.clear();
        this.isInvalid = true;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }
}

