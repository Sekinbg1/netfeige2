package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;

/* JADX INFO: loaded from: classes.dex */
public class CSVUnsignedIntegerTwoBytes extends CSV<UnsignedIntegerTwoBytes> {
    public CSVUnsignedIntegerTwoBytes() {
    }

    public CSVUnsignedIntegerTwoBytes(String str) throws InvalidValueException {
        super(str);
    }
}

