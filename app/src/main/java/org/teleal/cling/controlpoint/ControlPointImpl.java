package org.teleal.cling.controlpoint;

import java.util.logging.Logger;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.message.header.MXHeader;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;

/* JADX INFO: loaded from: classes.dex */
public class ControlPointImpl implements ControlPoint {
    private static Logger log = Logger.getLogger(ControlPointImpl.class.getName());
    protected final UpnpServiceConfiguration configuration;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;

    public ControlPointImpl(UpnpServiceConfiguration upnpServiceConfiguration, ProtocolFactory protocolFactory, Registry registry) {
        log.fine("Creating ControlPoint: " + getClass().getName());
        this.configuration = upnpServiceConfiguration;
        this.protocolFactory = protocolFactory;
        this.registry = registry;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public Registry getRegistry() {
        return this.registry;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search() {
        search(new STAllHeader(), MXHeader.DEFAULT_VALUE.intValue());
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search(UpnpHeader upnpHeader) {
        search(upnpHeader, MXHeader.DEFAULT_VALUE.intValue());
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search(int i) {
        search(new STAllHeader(), i);
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search(UpnpHeader upnpHeader, int i) {
        log.fine("Sending asynchronous search for: " + upnpHeader.getString());
        getConfiguration().getAsyncProtocolExecutor().execute(getProtocolFactory().createSendingSearch(upnpHeader, i));
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void execute(ActionCallback actionCallback) {
        log.fine("Invoking action in background: " + actionCallback);
        actionCallback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutor().execute(actionCallback);
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void execute(SubscriptionCallback subscriptionCallback) {
        log.fine("Invoking subscription in background: " + subscriptionCallback);
        subscriptionCallback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutor().execute(subscriptionCallback);
    }
}

