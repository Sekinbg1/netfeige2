package org.teleal.cling.model.types;

import org.teleal.cling.model.types.UnsignedVariableInteger;

/* JADX INFO: loaded from: classes.dex */
public final class UnsignedIntegerFourBytes extends UnsignedVariableInteger {
    public UnsignedIntegerFourBytes(long j) throws NumberFormatException {
        super(j);
    }

    public UnsignedIntegerFourBytes(String str) throws NumberFormatException {
        super(str);
    }

    @Override // org.teleal.cling.model.types.UnsignedVariableInteger
    public UnsignedVariableInteger.Bits getBits() {
        return UnsignedVariableInteger.Bits.THIRTYTWO;
    }
}

