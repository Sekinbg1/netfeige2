package org.teleal.cling.model.types;

import com.netfeige.dlna.ContentTree;

/* JADX INFO: loaded from: classes.dex */
public class BooleanDatatype extends AbstractDatatype<Boolean> {
    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public boolean isHandlingJavaType(Class cls) {
        return cls == Boolean.TYPE || Boolean.class.isAssignableFrom(cls);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Boolean valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        if (str.equals(ContentTree.VIDEO_ID) || str.toUpperCase().equals("YES") || str.toUpperCase().equals("TRUE")) {
            return true;
        }
        if (str.equals(ContentTree.ROOT_ID) || str.toUpperCase().equals("NO") || str.toUpperCase().equals("FALSE")) {
            return false;
        }
        throw new InvalidValueException("Invalid boolean value string: " + str);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String getString(Boolean bool) throws InvalidValueException {
        return bool == null ? "" : bool.booleanValue() ? ContentTree.VIDEO_ID : ContentTree.ROOT_ID;
    }
}

