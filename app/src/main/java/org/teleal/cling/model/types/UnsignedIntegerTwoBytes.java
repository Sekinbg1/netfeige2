package org.teleal.cling.model.types;

import org.teleal.cling.model.types.UnsignedVariableInteger;

/* JADX INFO: loaded from: classes.dex */
public final class UnsignedIntegerTwoBytes extends UnsignedVariableInteger {
    public UnsignedIntegerTwoBytes(long j) throws NumberFormatException {
        super(j);
    }

    public UnsignedIntegerTwoBytes(String str) throws NumberFormatException {
        super(str);
    }

    @Override // org.teleal.cling.model.types.UnsignedVariableInteger
    public UnsignedVariableInteger.Bits getBits() {
        return UnsignedVariableInteger.Bits.SIXTEEN;
    }
}

