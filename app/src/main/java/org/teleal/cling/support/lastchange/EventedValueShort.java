package org.teleal.cling.support.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;

/* JADX INFO: loaded from: classes.dex */
public class EventedValueShort extends EventedValue<Short> {
    public EventedValueShort(Short sh) {
        super(sh);
    }

    public EventedValueShort(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return Datatype.Builtin.I2_SHORT.getDatatype();
    }
}

