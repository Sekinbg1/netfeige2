package org.teleal.cling.model.message.header;

import com.netfeige.dlna.ContentTree;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* JADX INFO: loaded from: classes.dex */
public class EventSequenceHeader extends UpnpHeader<UnsignedIntegerFourBytes> {
    public EventSequenceHeader() {
    }

    public EventSequenceHeader(long j) {
        setValue(new UnsignedIntegerFourBytes(j));
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (!ContentTree.ROOT_ID.equals(str)) {
            while (str.startsWith(ContentTree.ROOT_ID)) {
                str = str.substring(1);
            }
        }
        try {
            setValue(new UnsignedIntegerFourBytes(str));
        } catch (NumberFormatException e) {
            throw new InvalidHeaderException("Invalid event sequence, " + e.getMessage());
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}

