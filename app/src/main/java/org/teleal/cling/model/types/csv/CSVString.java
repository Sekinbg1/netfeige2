package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class CSVString extends CSV<String> {
    public CSVString() {
    }

    public CSVString(String str) throws InvalidValueException {
        super(str);
    }
}

