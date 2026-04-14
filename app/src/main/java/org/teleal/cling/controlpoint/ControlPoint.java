package org.teleal.cling.controlpoint;

import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;

/* JADX INFO: loaded from: classes.dex */
public interface ControlPoint {
    void execute(ActionCallback actionCallback);

    void execute(SubscriptionCallback subscriptionCallback);

    UpnpServiceConfiguration getConfiguration();

    ProtocolFactory getProtocolFactory();

    Registry getRegistry();

    void search();

    void search(int i);

    void search(UpnpHeader upnpHeader);

    void search(UpnpHeader upnpHeader, int i);
}

