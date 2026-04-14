package org.teleal.cling.model.message.gena;

import java.net.URL;
import java.util.Collection;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.EventSequenceHeader;
import org.teleal.cling.model.message.header.NTEventHeader;
import org.teleal.cling.model.message.header.NTSHeader;
import org.teleal.cling.model.message.header.SubscriptionIdHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* JADX INFO: loaded from: classes.dex */
public class OutgoingEventRequestMessage extends StreamRequestMessage {
    private final Collection<StateVariableValue> stateVariableValues;

    public OutgoingEventRequestMessage(GENASubscription gENASubscription, URL url, UnsignedIntegerFourBytes unsignedIntegerFourBytes, Collection<StateVariableValue> collection) {
        super(new UpnpRequest(UpnpRequest.Method.NOTIFY, url));
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader());
        getHeaders().add(UpnpHeader.Type.NT, new NTEventHeader());
        getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.PROPCHANGE));
        getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(gENASubscription.getSubscriptionId()));
        getHeaders().add(UpnpHeader.Type.SEQ, new EventSequenceHeader(unsignedIntegerFourBytes.getValue().longValue()));
        this.stateVariableValues = collection;
    }

    public OutgoingEventRequestMessage(GENASubscription gENASubscription, URL url) {
        this(gENASubscription, url, gENASubscription.getCurrentSequence(), gENASubscription.getCurrentValues().values());
    }

    public Collection<StateVariableValue> getStateVariableValues() {
        return this.stateVariableValues;
    }
}

