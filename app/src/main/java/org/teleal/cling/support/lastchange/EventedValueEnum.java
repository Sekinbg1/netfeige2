package org.teleal.cling.support.lastchange;

import java.lang.Enum;
import java.util.Map;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public abstract class EventedValueEnum<E extends Enum> extends EventedValue<E> {
    protected abstract E enumValueOf(String str);

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return null;
    }

    public EventedValueEnum(E e) {
        super(e);
    }

    public EventedValueEnum(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.teleal.cling.support.lastchange.EventedValue
    public E valueOf(String str) throws InvalidValueException {
        return (E) enumValueOf(str);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public String toString() {
        return getValue().name();
    }
}

