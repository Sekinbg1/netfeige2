package org.teleal.cling.registry;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ProtocolFactory;

/* JADX INFO: loaded from: classes.dex */
public class RegistryImpl implements Registry {
    private static Logger log = Logger.getLogger(Registry.class.getName());
    protected RegistryMaintainer registryMaintainer;
    protected final UpnpService upnpService;
    protected ReentrantLock remoteSubscriptionsLock = new ReentrantLock(true);
    protected final Set<RegistryListener> registryListeners = new HashSet();
    protected final Set<RegistryItem<URI, Resource>> resourceItems = new HashSet();
    protected final List<Runnable> pendingExecutions = new ArrayList();
    protected final RemoteItems remoteItems = new RemoteItems(this);
    protected final LocalItems localItems = new LocalItems(this);

    public RegistryImpl(UpnpService upnpService) {
        log.fine("Creating Registry: " + getClass().getName());
        this.upnpService = upnpService;
        log.fine("Starting registry background maintenance...");
        RegistryMaintainer registryMaintainerCreateRegistryMaintainer = createRegistryMaintainer();
        this.registryMaintainer = registryMaintainerCreateRegistryMaintainer;
        if (registryMaintainerCreateRegistryMaintainer != null) {
            getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override // org.teleal.cling.registry.Registry
    public UpnpServiceConfiguration getConfiguration() {
        return getUpnpService().getConfiguration();
    }

    @Override // org.teleal.cling.registry.Registry
    public ProtocolFactory getProtocolFactory() {
        return getUpnpService().getProtocolFactory();
    }

    protected RegistryMaintainer createRegistryMaintainer() {
        return new RegistryMaintainer(this, getConfiguration().getRegistryMaintenanceIntervalMillis());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addListener(RegistryListener registryListener) {
        this.registryListeners.add(registryListener);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeListener(RegistryListener registryListener) {
        this.registryListeners.remove(registryListener);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<RegistryListener> getListeners() {
        return Collections.unmodifiableCollection(this.registryListeners);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean notifyDiscoveryStart(final RemoteDevice remoteDevice) {
        if (getUpnpService().getRegistry().getRemoteDevice(remoteDevice.getIdentity().getUdn(), true) != null) {
            log.finer("Not notifying listeners, already registered: " + remoteDevice);
            return false;
        }
        for (final RegistryListener registryListener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RegistryImpl.1
                @Override // java.lang.Runnable
                public void run() {
                    registryListener.remoteDeviceDiscoveryStarted(RegistryImpl.this, remoteDevice);
                }
            });
        }
        return true;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void notifyDiscoveryFailure(final RemoteDevice remoteDevice, final Exception exc) {
        for (final RegistryListener registryListener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RegistryImpl.2
                @Override // java.lang.Runnable
                public void run() {
                    registryListener.remoteDeviceDiscoveryFailed(RegistryImpl.this, remoteDevice, exc);
                }
            });
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addDevice(LocalDevice localDevice) {
        this.localItems.add(localDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addDevice(RemoteDevice remoteDevice) {
        this.remoteItems.add(remoteDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean update(RemoteDeviceIdentity remoteDeviceIdentity) {
        return this.remoteItems.update(remoteDeviceIdentity);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeDevice(LocalDevice localDevice) {
        return this.localItems.remove(localDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeDevice(RemoteDevice remoteDevice) {
        return this.remoteItems.remove(remoteDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeAllLocalDevices() {
        this.localItems.removeAll();
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeAllRemoteDevices() {
        this.remoteItems.removeAll();
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeDevice(UDN udn) {
        Device device = getDevice(udn, true);
        if (device != null && (device instanceof LocalDevice)) {
            return removeDevice((LocalDevice) device);
        }
        if (device == null || !(device instanceof RemoteDevice)) {
            return false;
        }
        return removeDevice((RemoteDevice) device);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Device getDevice(UDN udn, boolean z) {
        LocalDevice localDevice = this.localItems.get(udn, z);
        if (localDevice != null) {
            return localDevice;
        }
        RemoteDevice remoteDevice = this.remoteItems.get(udn, z);
        if (remoteDevice != null) {
            return remoteDevice;
        }
        return null;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized LocalDevice getLocalDevice(UDN udn, boolean z) {
        return this.localItems.get(udn, z);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized RemoteDevice getRemoteDevice(UDN udn, boolean z) {
        return this.remoteItems.get(udn, z);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<LocalDevice> getLocalDevices() {
        return Collections.unmodifiableCollection(this.localItems.get());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<RemoteDevice> getRemoteDevices() {
        return Collections.unmodifiableCollection(this.remoteItems.get());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Device> getDevices() {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.localItems.get());
        hashSet.addAll(this.remoteItems.get());
        return Collections.unmodifiableCollection(hashSet);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Device> getDevices(DeviceType deviceType) {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.localItems.get(deviceType));
        hashSet.addAll(this.remoteItems.get(deviceType));
        return Collections.unmodifiableCollection(hashSet);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Device> getDevices(ServiceType serviceType) {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.localItems.get(serviceType));
        hashSet.addAll(this.remoteItems.get(serviceType));
        return Collections.unmodifiableCollection(hashSet);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Service getService(ServiceReference serviceReference) {
        Device device = getDevice(serviceReference.getUdn(), false);
        if (device == null) {
            return null;
        }
        return device.findService(serviceReference.getServiceId());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Resource getResource(URI uri) throws IllegalArgumentException {
        if (uri.isAbsolute()) {
            throw new IllegalArgumentException("Resource URI can not be absolute, only path and query:" + uri);
        }
        Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (it.hasNext()) {
            Resource item = it.next().getItem();
            if (item.matches(uri)) {
                return item;
            }
        }
        if (uri.getPath().endsWith(ServiceReference.DELIMITER)) {
            URI uriCreate = URI.create(uri.toString().substring(0, uri.toString().length() - 1));
            Iterator<RegistryItem<URI, Resource>> it2 = this.resourceItems.iterator();
            while (it2.hasNext()) {
                Resource item2 = it2.next().getItem();
                if (item2.matches(uriCreate)) {
                    return item2;
                }
            }
        }
        return null;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized <T extends Resource> T getResource(Class<T> cls, URI uri) throws IllegalArgumentException {
        T t = (T) getResource(uri);
        if (t != null) {
            if (cls.isAssignableFrom(t.getClass())) {
                return t;
            }
        }
        return null;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Resource> getResources() {
        HashSet hashSet;
        hashSet = new HashSet();
        Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (it.hasNext()) {
            hashSet.add(it.next().getItem());
        }
        return hashSet;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized <T extends Resource> Collection<T> getResources(Class<T> cls) {
        HashSet hashSet;
        hashSet = new HashSet();
        for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
            if (cls.isAssignableFrom(registryItem.getItem().getClass())) {
                hashSet.add(registryItem.getItem());
            }
        }
        return hashSet;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addResource(Resource resource) {
        addResource(resource, 0);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addResource(Resource resource, int i) {
        RegistryItem<URI, Resource> registryItem = new RegistryItem<>(resource.getPathQuery(), resource, i);
        this.resourceItems.remove(registryItem);
        this.resourceItems.add(registryItem);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeResource(Resource resource) {
        return this.resourceItems.remove(new RegistryItem(resource.getPathQuery()));
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addLocalSubscription(LocalGENASubscription localGENASubscription) {
        this.localItems.addSubscription(localGENASubscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized LocalGENASubscription getLocalSubscription(String str) {
        return this.localItems.getSubscription(str);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean updateLocalSubscription(LocalGENASubscription localGENASubscription) {
        return this.localItems.updateSubscription(localGENASubscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeLocalSubscription(LocalGENASubscription localGENASubscription) {
        return this.localItems.removeSubscription(localGENASubscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        this.remoteItems.addSubscription(remoteGENASubscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized RemoteGENASubscription getRemoteSubscription(String str) {
        return this.remoteItems.getSubscription(str);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void updateRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        this.remoteItems.updateSubscription(remoteGENASubscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        this.remoteItems.removeSubscription(remoteGENASubscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void shutdown() {
        log.fine("Shutting down registry...");
        if (this.registryMaintainer != null) {
            this.registryMaintainer.stop();
        }
        log.finest("Executing final pending operations on shutdown: " + this.pendingExecutions.size());
        runPendingExecutions(false);
        Iterator<RegistryListener> it = this.registryListeners.iterator();
        while (it.hasNext()) {
            it.next().beforeShutdown(this);
        }
        for (RegistryItem registryItem : (RegistryItem[]) this.resourceItems.toArray(new RegistryItem[this.resourceItems.size()])) {
            ((Resource) registryItem.getItem()).shutdown();
        }
        this.remoteItems.shutdown();
        this.localItems.shutdown();
        Iterator<RegistryListener> it2 = this.registryListeners.iterator();
        while (it2.hasNext()) {
            it2.next().afterShutdown();
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void pause() {
        if (this.registryMaintainer != null) {
            log.fine("Pausing registry maintenance");
            runPendingExecutions(true);
            this.registryMaintainer.stop();
            this.registryMaintainer = null;
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void resume() {
        if (this.registryMaintainer == null) {
            log.fine("Resuming registry maintenance");
            this.remoteItems.resume();
            RegistryMaintainer registryMaintainerCreateRegistryMaintainer = createRegistryMaintainer();
            this.registryMaintainer = registryMaintainerCreateRegistryMaintainer;
            if (registryMaintainerCreateRegistryMaintainer != null) {
                getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
            }
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean isPaused() {
        return this.registryMaintainer == null;
    }

    synchronized void maintain() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Maintaining registry...");
        }
        Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (it.hasNext()) {
            RegistryItem<URI, Resource> next = it.next();
            if (next.getExpirationDetails().hasExpired()) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Removing expired resource: " + next);
                }
                it.remove();
            }
        }
        for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
            registryItem.getItem().maintain(this.pendingExecutions, registryItem.getExpirationDetails());
        }
        this.remoteItems.maintain();
        this.localItems.maintain();
        runPendingExecutions(true);
    }

    synchronized void executeAsyncProtocol(Runnable runnable) {
        this.pendingExecutions.add(runnable);
    }

    synchronized void runPendingExecutions(boolean z) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Executing pending operations: " + this.pendingExecutions.size());
        }
        for (Runnable runnable : this.pendingExecutions) {
            if (z) {
                getConfiguration().getAsyncProtocolExecutor().execute(runnable);
            } else {
                runnable.run();
            }
        }
        if (this.pendingExecutions.size() > 0) {
            this.pendingExecutions.clear();
        }
    }

    public void printDebugLog() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("====================================    REMOTE   ================================================");
            Iterator<RemoteDevice> it = this.remoteItems.get().iterator();
            while (it.hasNext()) {
                log.fine(it.next().toString());
            }
            log.fine("====================================    LOCAL    ================================================");
            Iterator<LocalDevice> it2 = this.localItems.get().iterator();
            while (it2.hasNext()) {
                log.fine(it2.next().toString());
            }
            log.fine("====================================  RESOURCES  ================================================");
            Iterator<RegistryItem<URI, Resource>> it3 = this.resourceItems.iterator();
            while (it3.hasNext()) {
                log.fine(it3.next().toString());
            }
            log.fine("=================================================================================================");
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public void lockRemoteSubscriptions() {
        this.remoteSubscriptionsLock.lock();
    }

    @Override // org.teleal.cling.registry.Registry
    public void unlockRemoteSubscriptions() {
        this.remoteSubscriptionsLock.unlock();
    }
}

