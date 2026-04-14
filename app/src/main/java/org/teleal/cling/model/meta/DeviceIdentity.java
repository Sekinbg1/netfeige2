package org.teleal.cling.model.meta;

import org.teleal.cling.model.types.UDN;

/* JADX INFO: loaded from: classes.dex */
public class DeviceIdentity {
    private final Integer maxAgeSeconds;
    private final UDN udn;

    public DeviceIdentity(UDN udn, DeviceIdentity deviceIdentity) {
        this.udn = udn;
        this.maxAgeSeconds = deviceIdentity.getMaxAgeSeconds();
    }

    public DeviceIdentity(UDN udn) {
        this.udn = udn;
        this.maxAgeSeconds = 1800;
    }

    public DeviceIdentity(UDN udn, Integer num) {
        this.udn = udn;
        this.maxAgeSeconds = num;
    }

    public UDN getUdn() {
        return this.udn;
    }

    public Integer getMaxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.udn.equals(((DeviceIdentity) obj).udn);
    }

    public int hashCode() {
        return this.udn.hashCode();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") UDN: " + getUdn();
    }
}

