package org.teleal.cling.support.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* JADX INFO: loaded from: classes.dex */
public class EventedValueUnsignedIntegerFourBytes extends EventedValue<UnsignedIntegerFourBytes> {
    public EventedValueUnsignedIntegerFourBytes(UnsignedIntegerFourBytes unsignedIntegerFourBytes) {
        super(unsignedIntegerFourBytes);
    }

    public EventedValueUnsignedIntegerFourBytes(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI4.getDatatype();
    }
}

