package org.teleal.cling.model;

import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDN;

/* JADX INFO: loaded from: classes.dex */
public class ServiceReference {
    public static final String DELIMITER = "/";
    private final ServiceId serviceId;
    private final UDN udn;

    public ServiceReference(String str) {
        String[] strArrSplit = str.split(DELIMITER);
        if (strArrSplit.length == 2) {
            this.udn = UDN.valueOf(strArrSplit[0]);
            this.serviceId = ServiceId.valueOf(strArrSplit[1]);
        } else {
            this.udn = null;
            this.serviceId = null;
        }
    }

    public ServiceReference(UDN udn, ServiceId serviceId) {
        this.udn = udn;
        this.serviceId = serviceId;
    }

    public UDN getUdn() {
        return this.udn;
    }

    public ServiceId getServiceId() {
        return this.serviceId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ServiceReference serviceReference = (ServiceReference) obj;
        return this.serviceId.equals(serviceReference.serviceId) && this.udn.equals(serviceReference.udn);
    }

    public int hashCode() {
        return (this.udn.hashCode() * 31) + this.serviceId.hashCode();
    }

    public String toString() {
        if (this.udn == null || this.serviceId == null) {
            return "";
        }
        return this.udn.toString() + DELIMITER + this.serviceId.toString();
    }
}

