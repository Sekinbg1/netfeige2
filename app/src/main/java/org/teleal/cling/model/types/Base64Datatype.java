package org.teleal.cling.model.types;

import org.teleal.common.util.Base64Coder;
import org.teleal.common.util.ByteArray;

/* JADX INFO: loaded from: classes.dex */
public class Base64Datatype extends AbstractDatatype<Byte[]> {
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
            return ByteArray.toWrapper(Base64Coder.decode(str));
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
            return new String(Base64Coder.encode(ByteArray.toPrimitive(bArr)));
        } catch (Exception e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }
}

