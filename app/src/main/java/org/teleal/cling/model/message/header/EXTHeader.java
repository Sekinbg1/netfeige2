package org.teleal.cling.model.message.header;

/* JADX INFO: loaded from: classes.dex */
public class EXTHeader extends UpnpHeader<String> {
    public static final String DEFAULT_VALUE = "";

    public EXTHeader() {
        setValue("");
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (str == null || str.length() <= 0) {
            return;
        }
        throw new InvalidHeaderException("Invalid EXT header, it has no value: " + str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue();
    }
}

