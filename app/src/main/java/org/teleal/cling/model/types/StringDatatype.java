package org.teleal.cling.model.types;

/* JADX INFO: loaded from: classes.dex */
public class StringDatatype extends AbstractDatatype<String> {
    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        return str;
    }
}

