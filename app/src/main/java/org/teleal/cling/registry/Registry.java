package org.teleal.cling.registry;

import java.net.URI;
import java.util.Collection;
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
public interface Registry {
    void addDevice(LocalDevice localDevice) throws RegistrationException;

    void addDevice(RemoteDevice remoteDevice) throws RegistrationException;

    void addListener(RegistryListener registryListener);

    void addLocalSubscription(LocalGENASubscription localGENASubscription);

    void addRemoteSubscription(RemoteGENASubscription remoteGENASubscription);

    void addResource(Resource resource);

    void addResource(Resource resource, int i);

    UpnpServiceConfiguration getConfiguration();

    Device getDevice(UDN udn, boolean z);

    Collection<Device> getDevices();

    Collection<Device> getDevices(DeviceType deviceType);

    Collection<Device> getDevices(ServiceType serviceType);

    Collection<RegistryListener> getListeners();

    LocalDevice getLocalDevice(UDN udn, boolean z);

    Collection<LocalDevice> getLocalDevices();

    LocalGENASubscription getLocalSubscription(String str);

    ProtocolFactory getProtocolFactory();

    RemoteDevice getRemoteDevice(UDN udn, boolean z);

    Collection<RemoteDevice> getRemoteDevices();

    RemoteGENASubscription getRemoteSubscription(String str);

    <T extends Resource> T getResource(Class<T> cls, URI uri) throws IllegalArgumentException;

    Resource getResource(URI uri) throws IllegalArgumentException;

    Collection<Resource> getResources();

    <T extends Resource> Collection<T> getResources(Class<T> cls);

    Service getService(ServiceReference serviceReference);

    UpnpService getUpnpService();

    boolean isPaused();

    void lockRemoteSubscriptions();

    void notifyDiscoveryFailure(RemoteDevice remoteDevice, Exception exc);

    boolean notifyDiscoveryStart(RemoteDevice remoteDevice);

    void pause();

    void removeAllLocalDevices();

    void removeAllRemoteDevices();

    boolean removeDevice(LocalDevice localDevice);

    boolean removeDevice(RemoteDevice remoteDevice);

    boolean removeDevice(UDN udn);

    void removeListener(RegistryListener registryListener);

    boolean removeLocalSubscription(LocalGENASubscription localGENASubscription);

    void removeRemoteSubscription(RemoteGENASubscription remoteGENASubscription);

    boolean removeResource(Resource resource);

    void resume();

    void shutdown();

    void unlockRemoteSubscriptions();

    boolean update(RemoteDeviceIdentity remoteDeviceIdentity);

    boolean updateLocalSubscription(LocalGENASubscription localGENASubscription);

    void updateRemoteSubscription(RemoteGENASubscription remoteGENASubscription);
}

