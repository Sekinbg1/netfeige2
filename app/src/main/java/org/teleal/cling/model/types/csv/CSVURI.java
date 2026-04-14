package org.teleal.cling.model.types.csv;

import java.net.URI;
import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class CSVURI extends CSV<URI> {
    public CSVURI() {
    }

    public CSVURI(String str) throws InvalidValueException {
        super(str);
    }
}

