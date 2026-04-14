package org.teleal.cling.model.types;

import org.teleal.common.util.ByteArray;
import org.teleal.common.util.HexBin;

/* JADX INFO: loaded from: classes.dex */
public class BinHexDatatype extends AbstractDatatype<Byte[]> {
    @Override // org.teleal.cling.model.types.AbstractDatatype
    public Class<Byte[]> getValueType() {
        return Byte[].class;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Byte[] valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        try {
            return ByteArray.toWrapper(HexBin.stringToBytes(str));
        } catch (Exception e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String getString(Byte[] bArr) throws InvalidValueException {
        if (bArr == null) {
            return "";
        }
        try {
            return HexBin.bytesToString(ByteArray.toPrimitive(bArr));
        } catch (Exception e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }
}

