package org.teleal.cling.support.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;

/* JADX INFO: loaded from: classes.dex */
public class EventedValueString extends EventedValue<String> {
    public EventedValueString(String str) {
        super(str);
    }

    public EventedValueString(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return Datatype.Builtin.STRING.getDatatype();
    }
}

