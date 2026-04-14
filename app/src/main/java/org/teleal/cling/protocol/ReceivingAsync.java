package org.teleal.cling.protocol;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.header.UpnpHeader;

/* JADX INFO: loaded from: classes.dex */
public abstract class ReceivingAsync<M extends UpnpMessage> implements Runnable {
    private M inputMessage;
    private final UpnpService upnpService;

    protected abstract void execute();

    protected boolean waitBeforeExecution() throws InterruptedException {
        return true;
    }

    protected ReceivingAsync(UpnpService upnpService, M m) {
        this.upnpService = upnpService;
        this.inputMessage = m;
    }

    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    public M getInputMessage() {
        return this.inputMessage;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean zWaitBeforeExecution;
        try {
            zWaitBeforeExecution = waitBeforeExecution();
        } catch (InterruptedException unused) {
            zWaitBeforeExecution = false;
        }
        if (zWaitBeforeExecution) {
            execute();
        }
    }

    protected <H extends UpnpHeader> H getFirstHeader(UpnpHeader.Type type, Class<H> cls) {
        return (H) getInputMessage().getHeaders().getFirstHeader(type, cls);
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}

