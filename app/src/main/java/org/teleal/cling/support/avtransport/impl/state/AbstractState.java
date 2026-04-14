package org.teleal.cling.support.avtransport.impl.state;

import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.TransportAction;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractState<T extends AVTransport> {
    private T transport;

    public abstract TransportAction[] getCurrentTransportActions();

    public AbstractState(T t) {
        this.transport = t;
    }

    public T getTransport() {
        return this.transport;
    }
}

