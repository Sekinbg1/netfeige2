package org.teleal.cling.model.message.header;

import java.net.URI;
import org.teleal.cling.model.types.UDAServiceType;

/* JADX INFO: loaded from: classes.dex */
public class UDAServiceTypeHeader extends ServiceTypeHeader {
    public UDAServiceTypeHeader() {
    }

    public UDAServiceTypeHeader(URI uri) {
        super(uri);
    }

    public UDAServiceTypeHeader(UDAServiceType uDAServiceType) {
        super(uDAServiceType);
    }

    @Override // org.teleal.cling.model.message.header.ServiceTypeHeader, org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(UDAServiceType.valueOf(str));
        } catch (Exception e) {
            throw new InvalidHeaderException("Invalid UDA service type header value, " + e.getMessage());
        }
    }
}

