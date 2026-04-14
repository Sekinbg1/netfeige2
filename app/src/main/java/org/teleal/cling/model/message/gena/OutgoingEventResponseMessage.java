package org.teleal.cling.model.message.gena;

import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;

/* JADX INFO: loaded from: classes.dex */
public class OutgoingEventResponseMessage extends StreamResponseMessage {
    public OutgoingEventResponseMessage() {
        super(new UpnpResponse(UpnpResponse.Status.OK));
    }

    public OutgoingEventResponseMessage(UpnpResponse upnpResponse) {
        super(upnpResponse);
    }
}

