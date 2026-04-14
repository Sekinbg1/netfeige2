package org.teleal.cling.support.model;

/* JADX INFO: loaded from: classes.dex */
public enum Protocol {
    ALL("*"),
    HTTP_GET("http-get"),
    RTSP_RTP_UDP("rtsp-rtp-udp"),
    INTERNAL("internal"),
    IEC61883("iec61883");

    private String protocolString;

    Protocol(String str) {
        this.protocolString = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.protocolString;
    }

    public static Protocol valueOrNullOf(String str) {
        for (Protocol protocol : values()) {
            if (protocol.toString().equals(str)) {
                return protocol;
            }
        }
        return null;
    }
}

