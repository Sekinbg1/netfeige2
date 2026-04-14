package org.teleal.cling.support.model;

/* JADX INFO: loaded from: classes.dex */
public enum TransportStatus {
    OK,
    ERROR_OCCURED,
    CUSTOM;

    String value = name();

    TransportStatus() {
    }

    public String getValue() {
        return this.value;
    }

    public TransportStatus setValue(String str) {
        this.value = str;
        return this;
    }

    public static TransportStatus valueOrCustomOf(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException unused) {
            return CUSTOM.setValue(str);
        }
    }
}

