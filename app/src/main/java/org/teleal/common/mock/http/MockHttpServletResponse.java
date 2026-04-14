package org.teleal.common.mock.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/* JADX INFO: loaded from: classes.dex */
public class MockHttpServletResponse implements HttpServletResponse {
    private static final String CHARSET_PREFIX = "charset=";
    public static final int DEFAULT_SERVER_PORT = 80;
    private boolean committed;
    private String contentType;
    private String forwardedUrl;
    private String includedUrl;
    private String redirectedUrl;
    private String statusMessage;
    private PrintWriter writer;
    private boolean outputStreamAccessAllowed = true;
    private boolean writerAccessAllowed = true;
    private String characterEncoding = "ISO-8859-1";
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private final ServletOutputStream outputStream = new ResponseServletOutputStream(this.content);
    private int contentLength = 0;
    private int bufferSize = 4096;
    private Locale locale = Locale.getDefault();
    private final List cookies = new ArrayList();
    private final Map headers = new HashMap();
    private int status = 200;

    public String encodeURL(String str) {
        return str;
    }

    public void setOutputStreamAccessAllowed(boolean z) {
        this.outputStreamAccessAllowed = z;
    }

    public boolean isOutputStreamAccessAllowed() {
        return this.outputStreamAccessAllowed;
    }

    public void setWriterAccessAllowed(boolean z) {
        this.writerAccessAllowed = z;
    }

    public boolean isWriterAccessAllowed() {
        return this.writerAccessAllowed;
    }

    public void setCharacterEncoding(String str) {
        this.characterEncoding = str;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public ServletOutputStream getOutputStream() {
        if (!this.outputStreamAccessAllowed) {
            throw new IllegalStateException("OutputStream access not allowed");
        }
        return this.outputStream;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (!this.writerAccessAllowed) {
            throw new IllegalStateException("Writer access not allowed");
        }
        if (this.writer == null) {
            this.writer = new ResponsePrintWriter(this.characterEncoding != null ? new OutputStreamWriter(this.content, this.characterEncoding) : new OutputStreamWriter(this.content));
        }
        return this.writer;
    }

    public byte[] getContentAsByteArray() {
        flushBuffer();
        return this.content.toByteArray();
    }

    public String getContentAsString() {
        flushBuffer();
        try {
            return this.characterEncoding != null ? this.content.toString(this.characterEncoding) : this.content.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setContentLength(int i) {
        this.contentLength = i;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public void setContentType(String str) {
        int iIndexOf;
        this.contentType = str;
        if (str == null || (iIndexOf = str.toLowerCase().indexOf(CHARSET_PREFIX)) == -1) {
            return;
        }
        setCharacterEncoding(str.substring(iIndexOf + 8));
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setBufferSize(int i) {
        this.bufferSize = i;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void flushBuffer() {
        setCommitted(true);
    }

    public void resetBuffer() {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot reset buffer - response is already committed");
        }
        this.content.reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCommittedIfBufferSizeExceeded() {
        int bufferSize = getBufferSize();
        if (bufferSize <= 0 || this.content.size() <= bufferSize) {
            return;
        }
        setCommitted(true);
    }

    public void setCommitted(boolean z) {
        this.committed = z;
    }

    public boolean isCommitted() {
        return this.committed;
    }

    public void reset() {
        resetBuffer();
        this.characterEncoding = null;
        this.contentLength = 0;
        this.contentType = null;
        this.locale = null;
        this.cookies.clear();
        this.headers.clear();
        this.status = 200;
        this.statusMessage = null;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public Cookie[] getCookies() {
        List list = this.cookies;
        return (Cookie[]) list.toArray(new Cookie[list.size()]);
    }

    public Cookie getCookie(String str) {
        for (Cookie cookie : this.cookies) {
            if (str.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public boolean containsHeader(String str) {
        return HeaderValueHolder.getByName(this.headers, str) != null;
    }

    public Set getHeaderNames() {
        return this.headers.keySet();
    }

    public Object getHeader(String str) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        if (byName != null) {
            return byName.getValue();
        }
        return null;
    }

    public List getHeaders(String str) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        return byName != null ? byName.getValues() : Collections.EMPTY_LIST;
    }

    public String encodeRedirectURL(String str) {
        return encodeURL(str);
    }

    public String encodeUrl(String str) {
        return encodeURL(str);
    }

    public String encodeRedirectUrl(String str) {
        return encodeRedirectURL(str);
    }

    public void sendError(int i, String str) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        this.status = i;
        this.statusMessage = str;
        setCommitted(true);
    }

    public void sendError(int i) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        this.status = i;
        setCommitted(true);
    }

    public void sendRedirect(String str) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }
        this.redirectedUrl = str;
        setCommitted(true);
    }

    public String getRedirectedUrl() {
        return this.redirectedUrl;
    }

    public void setDateHeader(String str, long j) {
        setHeaderValue(str, new Long(j));
    }

    public void addDateHeader(String str, long j) {
        addHeaderValue(str, new Long(j));
    }

    public void setHeader(String str, String str2) {
        setHeaderValue(str, str2);
    }

    public void addHeader(String str, String str2) {
        addHeaderValue(str, str2);
    }

    public void setIntHeader(String str, int i) {
        setHeaderValue(str, new Integer(i));
    }

    public void addIntHeader(String str, int i) {
        addHeaderValue(str, new Integer(i));
    }

    private void setHeaderValue(String str, Object obj) {
        doAddHeaderValue(str, obj, true);
    }

    private void addHeaderValue(String str, Object obj) {
        doAddHeaderValue(str, obj, false);
    }

    private void doAddHeaderValue(String str, Object obj, boolean z) {
        HeaderValueHolder byName = HeaderValueHolder.getByName(this.headers, str);
        if (byName == null) {
            byName = new HeaderValueHolder();
            this.headers.put(str, byName);
        }
        if (z) {
            byName.setValue(obj);
        } else {
            byName.addValue(obj);
        }
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public void setStatus(int i, String str) {
        this.status = i;
        this.statusMessage = str;
    }

    public int getStatus() {
        return this.status;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setForwardedUrl(String str) {
        this.forwardedUrl = str;
    }

    public String getForwardedUrl() {
        return this.forwardedUrl;
    }

    public void setIncludedUrl(String str) {
        this.includedUrl = str;
    }

    public String getIncludedUrl() {
        return this.includedUrl;
    }

    private class ResponseServletOutputStream extends DelegatingServletOutputStream {
        public ResponseServletOutputStream(OutputStream outputStream) {
            super(outputStream);
        }

        @Override // org.teleal.common.mock.http.DelegatingServletOutputStream
        public void write(int i) throws IOException {
            super.write(i);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override // org.teleal.common.mock.http.DelegatingServletOutputStream
        public void flush() throws IOException {
            super.flush();
            MockHttpServletResponse.this.setCommitted(true);
        }
    }

    private class ResponsePrintWriter extends PrintWriter {
        public ResponsePrintWriter(Writer writer) {
            super(writer, true);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] cArr, int i, int i2) {
            super.write(cArr, i, i2);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String str, int i, int i2) {
            super.write(str, i, i2);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(int i) {
            super.write(i);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Flushable
        public void flush() {
            super.flush();
            MockHttpServletResponse.this.setCommitted(true);
        }
    }
}

