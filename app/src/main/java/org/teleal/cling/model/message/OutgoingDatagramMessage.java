package org.teleal.cling.model.message;

import java.net.InetAddress;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpOperation;

/* JADX INFO: loaded from: classes.dex */
public abstract class OutgoingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {
    private InetAddress destinationAddress;
    private int destinationPort;

    protected OutgoingDatagramMessage(O o, InetAddress inetAddress, int i) {
        super(o);
        this.destinationAddress = inetAddress;
        this.destinationPort = i;
    }

    protected OutgoingDatagramMessage(O o, UpnpMessage.BodyType bodyType, Object obj, InetAddress inetAddress, int i) {
        super(o, bodyType, obj);
        this.destinationAddress = inetAddress;
        this.destinationPort = i;
    }

    public InetAddress getDestinationAddress() {
        return this.destinationAddress;
    }

    public int getDestinationPort() {
        return this.destinationPort;
    }
}

