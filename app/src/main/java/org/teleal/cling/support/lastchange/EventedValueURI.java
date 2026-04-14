package org.teleal.cling.support.lastchange;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class EventedValueURI extends EventedValue<URI> {
    private static final Logger log = Logger.getLogger(EventedValueURI.class.getName());

    public EventedValueURI(URI uri) {
        super(uri);
    }

    public EventedValueURI(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.teleal.cling.support.lastchange.EventedValue
    public URI valueOf(String str) throws InvalidValueException {
        try {
            return (URI) super.valueOf(str);
        } catch (InvalidValueException e) {
            log.info("Ignoring invalid URI in evented value '" + str + "': " + Exceptions.unwrap(e));
            return null;
        }
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return Datatype.Builtin.URI.getDatatype();
    }
}

