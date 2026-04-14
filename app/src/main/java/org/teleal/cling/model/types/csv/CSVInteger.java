package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class CSVInteger extends CSV<Integer> {
    public CSVInteger() {
    }

    public CSVInteger(String str) throws InvalidValueException {
        super(str);
    }
}

