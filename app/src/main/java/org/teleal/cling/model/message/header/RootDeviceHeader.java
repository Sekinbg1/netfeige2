package org.teleal.cling.model.message.header;

/* JADX INFO: loaded from: classes.dex */
public class RootDeviceHeader extends UpnpHeader<String> {
    public RootDeviceHeader() {
        setValue("upnp:rootdevice");
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (str.toLowerCase().equals(getValue())) {
            return;
        }
        throw new InvalidHeaderException("Invalid root device NT header value: " + str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue();
    }
}

