package org.teleal.cling.model.message.header;

import java.net.URI;
import org.teleal.cling.model.types.ServiceType;

/* JADX INFO: loaded from: classes.dex */
public class ServiceTypeHeader extends UpnpHeader<ServiceType> {
    public ServiceTypeHeader() {
    }

    public ServiceTypeHeader(URI uri) {
        setString(uri.toString());
    }

    public ServiceTypeHeader(ServiceType serviceType) {
        setValue(serviceType);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(ServiceType.valueOf(str));
        } catch (RuntimeException e) {
            throw new InvalidHeaderException("Invalid service type header value, " + e.getMessage());
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}

