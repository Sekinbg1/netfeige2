package org.teleal.cling.protocol;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;

/* JADX INFO: loaded from: classes.dex */
public abstract class ReceivingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends ReceivingAsync<IN> {
    protected OUT outputMessage;

    protected abstract OUT executeSync();

    public void responseException(Throwable th) {
    }

    public void responseSent(StreamResponseMessage streamResponseMessage) {
    }

    protected ReceivingSync(UpnpService upnpService, IN in) {
        super(upnpService, in);
    }

    public OUT getOutputMessage() {
        return this.outputMessage;
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected final void execute() {
        this.outputMessage = (OUT) executeSync();
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}

