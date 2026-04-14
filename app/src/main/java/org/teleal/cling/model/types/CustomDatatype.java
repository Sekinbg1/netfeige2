package org.teleal.cling.model.types;

/* JADX INFO: loaded from: classes.dex */
public class CustomDatatype extends AbstractDatatype<String> {
    private String name;

    public CustomDatatype(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        return str;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getName() + "'";
    }
}

