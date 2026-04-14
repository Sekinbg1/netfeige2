package org.teleal.cling.model.message.header;

import com.netfeige.common.Public_MsgID;
import org.teleal.common.util.HexBin;

/* JADX INFO: loaded from: classes.dex */
public class InterfaceMacHeader extends UpnpHeader<byte[]> {
    public InterfaceMacHeader() {
    }

    public InterfaceMacHeader(byte[] bArr) {
        setValue(bArr);
    }

    public InterfaceMacHeader(String str) {
        setString(str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        byte[] bArrStringToBytes = HexBin.stringToBytes(str, Public_MsgID.PRO_SPACE);
        setValue(bArrStringToBytes);
        if (bArrStringToBytes.length == 6) {
            return;
        }
        throw new InvalidHeaderException("Invalid MAC address: " + str);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return HexBin.bytesToString(getValue(), Public_MsgID.PRO_SPACE);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getString() + "'";
    }
}

