package org.teleal.cling.model.message;

import java.net.URI;
import java.net.URL;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpRequest;

/* JADX INFO: loaded from: classes.dex */
public class StreamRequestMessage extends UpnpMessage<UpnpRequest> {
    public StreamRequestMessage(StreamRequestMessage streamRequestMessage) {
        super(streamRequestMessage);
    }

    public StreamRequestMessage(UpnpRequest upnpRequest) {
        super(upnpRequest);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URI uri) {
        super(new UpnpRequest(method, uri));
    }

    public StreamRequestMessage(UpnpRequest.Method method, URL url) {
        super(new UpnpRequest(method, url));
    }

    public StreamRequestMessage(UpnpRequest upnpRequest, String str) {
        super(upnpRequest, UpnpMessage.BodyType.STRING, str);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URI uri, String str) {
        super(new UpnpRequest(method, uri), UpnpMessage.BodyType.STRING, str);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URL url, String str) {
        super(new UpnpRequest(method, url), UpnpMessage.BodyType.STRING, str);
    }

    public StreamRequestMessage(UpnpRequest upnpRequest, byte[] bArr) {
        super(upnpRequest, UpnpMessage.BodyType.BYTES, bArr);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URI uri, byte[] bArr) {
        super(new UpnpRequest(method, uri), UpnpMessage.BodyType.BYTES, bArr);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URL url, byte[] bArr) {
        super(new UpnpRequest(method, url), UpnpMessage.BodyType.BYTES, bArr);
    }

    public URI getUri() {
        return getOperation().getURI();
    }
}

