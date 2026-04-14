package org.teleal.cling.model;

import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class VariableValue {
    private final Datatype datatype;
    private final Object value;

    public VariableValue(Datatype datatype, Object obj) throws InvalidValueException {
        this.datatype = datatype;
        this.value = obj instanceof String ? datatype.valueOf((String) obj) : obj;
        if (ModelUtil.ANDROID_RUNTIME) {
            return;
        }
        if (!getDatatype().isValid(getValue())) {
            throw new InvalidValueException("Invalid value for " + getDatatype() + ": " + getValue());
        }
        if (isValidXMLString(toString())) {
            return;
        }
        throw new InvalidValueException("Invalid characters in string value (XML 1.0, section 2.2) produced by " + getDatatype() + "");
    }

    public Datatype getDatatype() {
        return this.datatype;
    }

    public Object getValue() {
        return this.value;
    }

    protected boolean isValidXMLString(String str) {
        int iCharCount = 0;
        while (iCharCount < str.length()) {
            int iCodePointAt = str.codePointAt(iCharCount);
            if (iCodePointAt != 9 && iCodePointAt != 10 && iCodePointAt != 13 && ((iCodePointAt < 32 || iCodePointAt > 55295) && ((iCodePointAt < 57344 || iCodePointAt > 65533) && (iCodePointAt < 65536 || iCodePointAt > 1114111)))) {
                return false;
            }
            iCharCount += Character.charCount(iCodePointAt);
        }
        return true;
    }

    public String toString() {
        return getDatatype().getString(getValue());
    }
}

