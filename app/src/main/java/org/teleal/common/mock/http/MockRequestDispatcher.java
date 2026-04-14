package org.teleal.common.mock.http;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/* JADX INFO: loaded from: classes.dex */
public class MockRequestDispatcher implements RequestDispatcher {
    private final Logger log = Logger.getLogger(MockRequestDispatcher.class.getName());
    private final String url;

    public MockRequestDispatcher(String str) {
        this.url = str;
    }

    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) {
        if (servletResponse.isCommitted()) {
            throw new IllegalStateException("Cannot perform forward - response is already committed");
        }
        getMockHttpServletResponse(servletResponse).setForwardedUrl(this.url);
        if (this.log.isLoggable(Level.FINE)) {
            this.log.fine("MockRequestDispatcher: forwarding to URL [" + this.url + "]");
        }
    }

    public void include(ServletRequest servletRequest, ServletResponse servletResponse) {
        getMockHttpServletResponse(servletResponse).setIncludedUrl(this.url);
        if (this.log.isLoggable(Level.FINE)) {
            this.log.fine("MockRequestDispatcher: including URL [" + this.url + "]");
        }
    }

    protected MockHttpServletResponse getMockHttpServletResponse(ServletResponse servletResponse) {
        if (servletResponse instanceof MockHttpServletResponse) {
            return (MockHttpServletResponse) servletResponse;
        }
        if (servletResponse instanceof HttpServletResponseWrapper) {
            return getMockHttpServletResponse(((HttpServletResponseWrapper) servletResponse).getResponse());
        }
        throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
    }
}

