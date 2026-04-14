package org.teleal.cling.model.message;

import java.net.InetAddress;
import org.teleal.cling.model.message.UpnpOperation;

/* JADX INFO: loaded from: classes.dex */
public class IncomingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {
    private InetAddress localAddress;
    private InetAddress sourceAddress;
    private int sourcePort;

    public IncomingDatagramMessage(O o, InetAddress inetAddress, int i, InetAddress inetAddress2) {
        super(o);
        this.sourceAddress = inetAddress;
        this.sourcePort = i;
        this.localAddress = inetAddress2;
    }

    protected IncomingDatagramMessage(IncomingDatagramMessage<O> incomingDatagramMessage) {
        super(incomingDatagramMessage);
        this.sourceAddress = incomingDatagramMessage.getSourceAddress();
        this.sourcePort = incomingDatagramMessage.getSourcePort();
        this.localAddress = incomingDatagramMessage.getLocalAddress();
    }

    public InetAddress getSourceAddress() {
        return this.sourceAddress;
    }

    public int getSourcePort() {
        return this.sourcePort;
    }

    public InetAddress getLocalAddress() {
        return this.localAddress;
    }
}

