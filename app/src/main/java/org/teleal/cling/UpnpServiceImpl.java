package org.teleal.cling;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.controlpoint.ControlPointImpl;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.protocol.ProtocolFactoryImpl;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryImpl;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.RouterImpl;

/* JADX INFO: loaded from: classes.dex */
public class UpnpServiceImpl implements UpnpService {
    private static Logger log = Logger.getLogger(UpnpServiceImpl.class.getName());
    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final Router router;

    public UpnpServiceImpl() {
        this(new DefaultUpnpServiceConfiguration(), new RegistryListener[0]);
    }

    public UpnpServiceImpl(RegistryListener... registryListenerArr) {
        this(new DefaultUpnpServiceConfiguration(), registryListenerArr);
    }

    public UpnpServiceImpl(UpnpServiceConfiguration upnpServiceConfiguration, RegistryListener... registryListenerArr) {
        this.configuration = upnpServiceConfiguration;
        log.info(">>> Starting UPnP service...");
        log.info("Using configuration: " + getConfiguration().getClass().getName());
        ProtocolFactory protocolFactoryCreateProtocolFactory = createProtocolFactory();
        this.protocolFactory = protocolFactoryCreateProtocolFactory;
        this.registry = createRegistry(protocolFactoryCreateProtocolFactory);
        for (RegistryListener registryListener : registryListenerArr) {
            this.registry.addListener(registryListener);
        }
        this.router = createRouter(this.protocolFactory, this.registry);
        this.controlPoint = createControlPoint(this.protocolFactory, this.registry);
        log.info("<<< UPnP service started successfully");
    }

    protected ProtocolFactory createProtocolFactory() {
        return new ProtocolFactoryImpl(this);
    }

    protected Registry createRegistry(ProtocolFactory protocolFactory) {
        return new RegistryImpl(this);
    }

    protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
        return new RouterImpl(getConfiguration(), protocolFactory);
    }

    protected ControlPoint createControlPoint(ProtocolFactory protocolFactory, Registry registry) {
        return new ControlPointImpl(getConfiguration(), protocolFactory, registry);
    }

    @Override // org.teleal.cling.UpnpService
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.UpnpService
    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    @Override // org.teleal.cling.UpnpService
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.UpnpService
    public Registry getRegistry() {
        return this.registry;
    }

    @Override // org.teleal.cling.UpnpService
    public Router getRouter() {
        return this.router;
    }

    @Override // org.teleal.cling.UpnpService
    public synchronized void shutdown() {
        log.info(">>> Shutting down UPnP service...");
        getRegistry().shutdown();
        getRouter().shutdown();
        getConfiguration().shutdown();
        log.info("<<< UPnP service shutdown completed");
    }
}

