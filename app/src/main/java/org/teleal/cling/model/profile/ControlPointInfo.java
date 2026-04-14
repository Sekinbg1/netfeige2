package org.teleal.cling.model.profile;

import org.teleal.cling.model.message.UpnpHeaders;

/* JADX INFO: loaded from: classes.dex */
public class ControlPointInfo {
    UpnpHeaders headers;

    public ControlPointInfo() {
        this(new UpnpHeaders());
    }

    public ControlPointInfo(UpnpHeaders upnpHeaders) {
        this.headers = upnpHeaders;
    }

    public UpnpHeaders getHeaders() {
        return this.headers;
    }
}

