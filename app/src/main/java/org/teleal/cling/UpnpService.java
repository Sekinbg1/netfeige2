package org.teleal.cling;

import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.transport.Router;

/* JADX INFO: loaded from: classes.dex */
public interface UpnpService {
    UpnpServiceConfiguration getConfiguration();

    ControlPoint getControlPoint();

    ProtocolFactory getProtocolFactory();

    Registry getRegistry();

    Router getRouter();

    void shutdown();
}

