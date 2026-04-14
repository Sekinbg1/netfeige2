package org.teleal.common.mock.http;

import java.util.Enumeration;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class IteratorEnumeration implements Enumeration {
    private Iterator iterator;

    public IteratorEnumeration() {
    }

    public IteratorEnumeration(Iterator it) {
        this.iterator = it;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    @Override // java.util.Enumeration
    public Object nextElement() {
        return this.iterator.next();
    }

    public Iterator getIterator() {
        return this.iterator;
    }

    public void setIterator(Iterator it) {
        this.iterator = it;
    }
}

