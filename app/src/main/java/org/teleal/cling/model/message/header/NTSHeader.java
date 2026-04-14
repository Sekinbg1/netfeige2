package org.teleal.cling.model.message.header;

import org.teleal.cling.model.types.NotificationSubtype;

/* JADX INFO: loaded from: classes.dex */
public class NTSHeader extends UpnpHeader<NotificationSubtype> {
    public NTSHeader() {
    }

    public NTSHeader(NotificationSubtype notificationSubtype) {
        setValue(notificationSubtype);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        NotificationSubtype[] notificationSubtypeArrValues = NotificationSubtype.values();
        int length = notificationSubtypeArrValues.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            NotificationSubtype notificationSubtype = notificationSubtypeArrValues[i];
            if (str.equals(notificationSubtype.getHeaderString())) {
                setValue(notificationSubtype);
                break;
            }
            i++;
        }
        if (getValue() != null) {
            return;
        }
        throw new InvalidHeaderException("Invalid NTS header value: " + str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().getHeaderString();
    }
}

