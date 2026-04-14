package org.teleal.cling.model.message.header;

import org.teleal.cling.model.types.NamedServiceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;

/* JADX INFO: loaded from: classes.dex */
public class ServiceUSNHeader extends UpnpHeader<NamedServiceType> {
    public ServiceUSNHeader() {
    }

    public ServiceUSNHeader(UDN udn, ServiceType serviceType) {
        setValue(new NamedServiceType(udn, serviceType));
    }

    public ServiceUSNHeader(NamedServiceType namedServiceType) {
        setValue(namedServiceType);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(NamedServiceType.valueOf(str));
        } catch (Exception e) {
            throw new InvalidHeaderException("Invalid service USN header value, " + e.getMessage());
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}

