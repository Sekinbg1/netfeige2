package org.teleal.cling.transport.impl.apache;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

/* JADX INFO: loaded from: classes.dex */
public class UpnpHttpRequestFactory extends DefaultHttpRequestFactory {
    private static final String[] BASIC = {"SUBSCRIBE", "UNSUBSCRIBE"};
    private static final String[] WITH_ENTITY = {"NOTIFY"};

    private static boolean isOneOf(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (str2.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.http.impl.DefaultHttpRequestFactory, org.apache.http.HttpRequestFactory
    public HttpRequest newHttpRequest(RequestLine requestLine) throws MethodNotSupportedException {
        if (requestLine == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        return newHttpRequest(requestLine.getMethod(), requestLine.getUri());
    }

    @Override // org.apache.http.impl.DefaultHttpRequestFactory, org.apache.http.HttpRequestFactory
    public HttpRequest newHttpRequest(String str, String str2) throws MethodNotSupportedException {
        if (isOneOf(BASIC, str)) {
            return new BasicHttpRequest(str, str2);
        }
        if (isOneOf(WITH_ENTITY, str)) {
            return new BasicHttpEntityEnclosingRequest(str, str2);
        }
        return super.newHttpRequest(str, str2);
    }
}

