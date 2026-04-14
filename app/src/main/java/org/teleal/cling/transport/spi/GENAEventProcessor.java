package org.teleal.cling.transport.spi;

import org.teleal.cling.model.message.gena.IncomingEventRequestMessage;
import org.teleal.cling.model.message.gena.OutgoingEventRequestMessage;

/* JADX INFO: loaded from: classes.dex */
public interface GENAEventProcessor {
    void readBody(IncomingEventRequestMessage incomingEventRequestMessage) throws UnsupportedDataException;

    void writeBody(OutgoingEventRequestMessage outgoingEventRequestMessage) throws UnsupportedDataException;
}

