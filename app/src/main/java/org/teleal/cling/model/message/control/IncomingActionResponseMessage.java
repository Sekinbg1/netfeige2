package org.teleal.cling.model.message.control;

import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;

/* JADX INFO: loaded from: classes.dex */
public class IncomingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage {
    @Override // org.teleal.cling.model.message.control.ActionMessage
    public String getActionNamespace() {
        return null;
    }

    public IncomingActionResponseMessage(StreamResponseMessage streamResponseMessage) {
        super(streamResponseMessage);
    }

    public IncomingActionResponseMessage(UpnpResponse upnpResponse) {
        super(upnpResponse);
    }

    public boolean isFailedNonRecoverable() {
        int statusCode = getOperation().getStatusCode();
        return (!getOperation().isFailed() || statusCode == UpnpResponse.Status.METHOD_NOT_SUPPORTED.getStatusCode() || (statusCode == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode() && hasBody())) ? false : true;
    }

    public boolean isFailedRecoverable() {
        return hasBody() && getOperation().getStatusCode() == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
}

