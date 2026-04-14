package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class CSVBytes extends CSV<Byte[]> {
    public CSVBytes() {
    }

    public CSVBytes(String str) throws InvalidValueException {
        super(str);
    }
}

