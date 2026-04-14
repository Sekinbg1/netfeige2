package org.teleal.cling.model.message.gena;

import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.header.SubscriptionIdHeader;
import org.teleal.cling.model.message.header.TimeoutHeader;
import org.teleal.cling.model.message.header.UpnpHeader;

/* JADX INFO: loaded from: classes.dex */
public class IncomingSubscribeResponseMessage extends StreamResponseMessage {
    public IncomingSubscribeResponseMessage(StreamResponseMessage streamResponseMessage) {
        super(streamResponseMessage);
    }

    public boolean isVaildHeaders() {
        return (getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class) == null || getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class) == null) ? false : true;
    }

    public String getSubscriptionId() {
        return ((SubscriptionIdHeader) getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class)).getValue();
    }

    public int getSubscriptionDurationSeconds() {
        return ((TimeoutHeader) getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class)).getValue().intValue();
    }
}

