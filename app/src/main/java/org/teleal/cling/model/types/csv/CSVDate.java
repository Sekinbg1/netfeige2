package org.teleal.cling.model.types.csv;

import java.util.Date;
import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class CSVDate extends CSV<Date> {
    public CSVDate() {
    }

    public CSVDate(String str) throws InvalidValueException {
        super(str);
    }
}

