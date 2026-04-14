package org.teleal.cling.model.message;

import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.common.util.MimeType;

/* JADX INFO: loaded from: classes.dex */
public class StreamResponseMessage extends UpnpMessage<UpnpResponse> {
    public StreamResponseMessage(StreamResponseMessage streamResponseMessage) {
        super(streamResponseMessage);
    }

    public StreamResponseMessage(UpnpResponse.Status status) {
        super(new UpnpResponse(status));
    }

    public StreamResponseMessage(UpnpResponse upnpResponse) {
        super(upnpResponse);
    }

    public StreamResponseMessage(UpnpResponse upnpResponse, String str) {
        super(upnpResponse, UpnpMessage.BodyType.STRING, str);
    }

    public StreamResponseMessage(String str) {
        super(new UpnpResponse(UpnpResponse.Status.OK), UpnpMessage.BodyType.STRING, str);
    }

    public StreamResponseMessage(UpnpResponse upnpResponse, byte[] bArr) {
        super(upnpResponse, UpnpMessage.BodyType.BYTES, bArr);
    }

    public StreamResponseMessage(byte[] bArr) {
        super(new UpnpResponse(UpnpResponse.Status.OK), UpnpMessage.BodyType.BYTES, bArr);
    }

    public StreamResponseMessage(String str, ContentTypeHeader contentTypeHeader) {
        this(str);
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentTypeHeader);
    }

    public StreamResponseMessage(String str, MimeType mimeType) {
        this(str, new ContentTypeHeader(mimeType));
    }

    public StreamResponseMessage(byte[] bArr, ContentTypeHeader contentTypeHeader) {
        this(bArr);
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentTypeHeader);
    }

    public StreamResponseMessage(byte[] bArr, MimeType mimeType) {
        this(bArr, new ContentTypeHeader(mimeType));
    }
}

