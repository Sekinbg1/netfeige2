package org.teleal.cling.model.types;

/* JADX INFO: loaded from: classes.dex */
public class DoubleDatatype extends AbstractDatatype<Double> {
    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public boolean isHandlingJavaType(Class cls) {
        return cls == Double.TYPE || Double.class.isAssignableFrom(cls);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Double valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        try {
            return Double.valueOf(Double.parseDouble(str));
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Can't convert string to number: " + str, e);
        }
    }
}

