package org.teleal.cling.model.message.header;

import org.teleal.cling.model.types.NotificationSubtype;

/* JADX INFO: loaded from: classes.dex */
public class STAllHeader extends UpnpHeader<NotificationSubtype> {
    public STAllHeader() {
        setValue(NotificationSubtype.ALL);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (str.equals(NotificationSubtype.ALL.getHeaderString())) {
            return;
        }
        throw new InvalidHeaderException("Invalid ST header value (not " + NotificationSubtype.ALL + "): " + str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().getHeaderString();
    }
}

