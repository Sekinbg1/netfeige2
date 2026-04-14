package org.teleal.cling.transport;

import org.teleal.cling.transport.spi.InitializationException;

/* JADX INFO: loaded from: classes.dex */
public interface SwitchableRouter extends Router {
    boolean disable();

    boolean enable();

    void handleStartFailure(InitializationException initializationException);

    boolean isEnabled();
}

