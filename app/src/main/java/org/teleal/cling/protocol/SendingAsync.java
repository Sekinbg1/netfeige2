package org.teleal.cling.protocol;

import org.teleal.cling.UpnpService;

/* JADX INFO: loaded from: classes.dex */
public abstract class SendingAsync implements Runnable {
    private final UpnpService upnpService;

    protected abstract void execute();

    protected SendingAsync(UpnpService upnpService) {
        this.upnpService = upnpService;
    }

    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override // java.lang.Runnable
    public void run() {
        execute();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}

