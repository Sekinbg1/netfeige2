package org.teleal.cling.model.message.header;

/* JADX INFO: loaded from: classes.dex */
public class SubscriptionIdHeader extends UpnpHeader<String> {
    public static final String PREFIX = "uuid:";

    public SubscriptionIdHeader() {
    }

    public SubscriptionIdHeader(String str) {
        setValue(str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (!str.startsWith("uuid:")) {
            throw new InvalidHeaderException("Invalid subscription ID header value, must start with 'uuid:': " + str);
        }
        setValue(str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue();
    }
}

