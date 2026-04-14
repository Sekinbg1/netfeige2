package org.teleal.cling.transport.impl.apache;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.common.mock.http.MockHttpServletRequest;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class StreamClientImpl implements StreamClient<StreamClientConfigurationImpl> {
    private static final Logger log = Logger.getLogger(StreamClient.class.getName());
    protected final ThreadSafeClientConnManager clientConnectionManager;
    protected final StreamClientConfigurationImpl configuration;
    protected final HttpParams globalParams;
    protected final DefaultHttpClient httpClient;

    public StreamClientImpl(StreamClientConfigurationImpl streamClientConfigurationImpl) throws InitializationException {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        this.globalParams = basicHttpParams;
        this.configuration = streamClientConfigurationImpl;
        ConnManagerParams.setMaxTotalConnections(basicHttpParams, getConfiguration().getMaxTotalConnections());
        HttpConnectionParams.setConnectionTimeout(this.globalParams, getConfiguration().getConnectionTimeoutSeconds() * 1000);
        HttpConnectionParams.setSoTimeout(this.globalParams, getConfiguration().getDataReadTimeoutSeconds() * 1000);
        HttpProtocolParams.setContentCharset(this.globalParams, getConfiguration().getContentCharset());
        if (getConfiguration().getSocketBufferSize() != -1) {
            HttpConnectionParams.setSocketBufferSize(this.globalParams, getConfiguration().getSocketBufferSize());
        }
        HttpConnectionParams.setStaleCheckingEnabled(this.globalParams, getConfiguration().getStaleCheckingEnabled());
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(MockHttpServletRequest.DEFAULT_PROTOCOL, PlainSocketFactory.getSocketFactory(), 80));
        this.clientConnectionManager = new ThreadSafeClientConnManager(this.globalParams, schemeRegistry);
        this.httpClient = new DefaultHttpClient(this.clientConnectionManager, this.globalParams);
        if (getConfiguration().getRequestRetryCount() != -1) {
            this.httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(getConfiguration().getRequestRetryCount(), false));
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public StreamClientConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public StreamResponseMessage sendRequest(StreamRequestMessage streamRequestMessage) {
        UpnpRequest operation = streamRequestMessage.getOperation();
        log.fine("Preparing HTTP request message with method '" + operation.getHttpMethodName() + "': " + streamRequestMessage);
        try {
            HttpUriRequest httpUriRequestCreateHttpRequest = createHttpRequest(streamRequestMessage, operation);
            httpUriRequestCreateHttpRequest.setParams(getRequestParams(streamRequestMessage));
            HeaderUtil.add(httpUriRequestCreateHttpRequest, streamRequestMessage.getHeaders());
            log.fine("Sending HTTP request: " + httpUriRequestCreateHttpRequest.getURI());
            return (StreamResponseMessage) this.httpClient.execute(httpUriRequestCreateHttpRequest, createResponseHandler());
        } catch (MethodNotSupportedException e) {
            log.warning("Request aborted: " + e.toString());
            return null;
        } catch (ClientProtocolException e2) {
            log.warning("HTTP protocol exception executing request: " + streamRequestMessage);
            log.warning("Cause: " + Exceptions.unwrap(e2));
            return null;
        } catch (IOException e3) {
            log.fine("Client connection was aborted: " + e3.getMessage());
            return null;
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public void stop() {
        log.fine("Shutting down HTTP client connection manager/pool");
        this.clientConnectionManager.shutdown();
    }

    /* JADX INFO: renamed from: org.teleal.cling.transport.impl.apache.StreamClientImpl$5, reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method;

        static {
            int[] iArr = new int[UpnpRequest.Method.values().length];
            $SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method = iArr;
            try {
                iArr[UpnpRequest.Method.GET.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method[UpnpRequest.Method.SUBSCRIBE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method[UpnpRequest.Method.UNSUBSCRIBE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method[UpnpRequest.Method.POST.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method[UpnpRequest.Method.NOTIFY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    protected HttpUriRequest createHttpRequest(UpnpMessage upnpMessage, UpnpRequest upnpRequest) throws MethodNotSupportedException {
        int i = AnonymousClass5.$SwitchMap$org$teleal$cling$model$message$UpnpRequest$Method[upnpRequest.getMethod().ordinal()];
        if (i == 1) {
            return new HttpGet(upnpRequest.getURI());
        }
        if (i == 2) {
            return new HttpGet(upnpRequest.getURI()) { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.1
                @Override // org.apache.http.client.methods.HttpGet, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
                public String getMethod() {
                    return UpnpRequest.Method.SUBSCRIBE.getHttpName();
                }
            };
        }
        if (i == 3) {
            return new HttpGet(upnpRequest.getURI()) { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.2
                @Override // org.apache.http.client.methods.HttpGet, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
                public String getMethod() {
                    return UpnpRequest.Method.UNSUBSCRIBE.getHttpName();
                }
            };
        }
        if (i == 4) {
            HttpPost httpPost = new HttpPost(upnpRequest.getURI());
            httpPost.setEntity(createHttpRequestEntity(upnpMessage));
            return httpPost;
        }
        if (i == 5) {
            HttpPost httpPost2 = new HttpPost(upnpRequest.getURI()) { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.3
                @Override // org.apache.http.client.methods.HttpPost, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
                public String getMethod() {
                    return UpnpRequest.Method.NOTIFY.getHttpName();
                }
            };
            httpPost2.setEntity(createHttpRequestEntity(upnpMessage));
            return httpPost2;
        }
        throw new MethodNotSupportedException(upnpRequest.getHttpMethodName());
    }

    protected HttpEntity createHttpRequestEntity(UpnpMessage upnpMessage) {
        if (upnpMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
            log.fine("Preparing HTTP request entity as byte[]");
            return new ByteArrayEntity(upnpMessage.getBodyBytes());
        }
        log.fine("Preparing HTTP request entity as string");
        try {
            String contentTypeCharset = upnpMessage.getContentTypeCharset();
            String bodyString = upnpMessage.getBodyString();
            if (contentTypeCharset == null) {
                contentTypeCharset = "UTF-8";
            }
            return new StringEntity(bodyString, contentTypeCharset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResponseHandler<StreamResponseMessage> createResponseHandler() {
        return new ResponseHandler<StreamResponseMessage>() { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.4
            @Override // org.apache.http.client.ResponseHandler
            public StreamResponseMessage handleResponse(HttpResponse httpResponse) throws IOException {
                StatusLine statusLine = httpResponse.getStatusLine();
                StreamClientImpl.log.fine("Received HTTP response: " + statusLine);
                StreamResponseMessage streamResponseMessage = new StreamResponseMessage(new UpnpResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase()));
                streamResponseMessage.setHeaders(new UpnpHeaders(HeaderUtil.get(httpResponse)));
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null && entity.getContentLength() != 0) {
                    if (streamResponseMessage.isContentTypeMissingOrText()) {
                        StreamClientImpl.log.fine("HTTP response message contains text entity");
                        streamResponseMessage.setBody(UpnpMessage.BodyType.STRING, EntityUtils.toString(entity));
                    } else {
                        StreamClientImpl.log.fine("HTTP response message contains binary entity");
                        streamResponseMessage.setBody(UpnpMessage.BodyType.BYTES, EntityUtils.toByteArray(entity));
                    }
                }
                return streamResponseMessage;
            }
        };
    }

    protected HttpParams getRequestParams(StreamRequestMessage streamRequestMessage) {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        basicHttpParams.setParameter("http.protocol.version", streamRequestMessage.getOperation().getHttpMinorVersion() == 0 ? HttpVersion.HTTP_1_0 : HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(basicHttpParams, getConfiguration().getUserAgentValue(streamRequestMessage.getUdaMajorVersion(), streamRequestMessage.getUdaMinorVersion()));
        return new DefaultedHttpParams(basicHttpParams, this.globalParams);
    }
}

