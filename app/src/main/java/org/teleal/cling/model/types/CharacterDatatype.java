package org.teleal.cling.model.types;

/* JADX INFO: loaded from: classes.dex */
public class CharacterDatatype extends AbstractDatatype<Character> {
    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public boolean isHandlingJavaType(Class cls) {
        return cls == Character.TYPE || Character.class.isAssignableFrom(cls);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Character valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        return Character.valueOf(str.charAt(0));
    }
}

