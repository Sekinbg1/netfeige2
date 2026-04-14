package org.teleal.cling.model.message.header;

import java.net.URI;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;

/* JADX INFO: loaded from: classes.dex */
public class UDADeviceTypeHeader extends DeviceTypeHeader {
    public UDADeviceTypeHeader() {
    }

    public UDADeviceTypeHeader(URI uri) {
        super(uri);
    }

    public UDADeviceTypeHeader(DeviceType deviceType) {
        super(deviceType);
    }

    @Override // org.teleal.cling.model.message.header.DeviceTypeHeader, org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(UDADeviceType.valueOf(str));
        } catch (Exception e) {
            throw new InvalidHeaderException("Invalid UDA device type header value, " + e.getMessage());
        }
    }
}

