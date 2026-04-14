package org.teleal.common.xml;

import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

/* JADX INFO: loaded from: classes.dex */
public abstract class NamespaceContextMap extends HashMap<String, String> implements NamespaceContext {
    protected abstract String getDefaultNamespaceURI();

    @Override // javax.xml.namespace.NamespaceContext
    public String getPrefix(String str) {
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public Iterator getPrefixes(String str) {
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getNamespaceURI(String str) {
        if (str == null) {
            throw new IllegalArgumentException("No prefix provided!");
        }
        if (str.equals("")) {
            return getDefaultNamespaceURI();
        }
        return get(str) != null ? get(str) : "";
    }
}

