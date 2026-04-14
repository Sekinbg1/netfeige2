package org.teleal.cling.model.message.gena;

import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.header.SubscriptionIdHeader;
import org.teleal.cling.model.message.header.TimeoutHeader;
import org.teleal.cling.model.message.header.UpnpHeader;

/* JADX INFO: loaded from: classes.dex */
public class OutgoingRenewalRequestMessage extends StreamRequestMessage {
    public OutgoingRenewalRequestMessage(RemoteGENASubscription remoteGENASubscription) {
        super(UpnpRequest.Method.SUBSCRIBE, remoteGENASubscription.getEventSubscriptionURL());
        getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(remoteGENASubscription.getSubscriptionId()));
        getHeaders().add(UpnpHeader.Type.TIMEOUT, new TimeoutHeader(remoteGENASubscription.getRequestedDurationSeconds()));
    }
}

