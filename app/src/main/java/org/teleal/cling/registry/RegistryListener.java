package org.teleal.cling.registry;

import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;

/* JADX INFO: loaded from: classes.dex */
public interface RegistryListener {
    void afterShutdown();

    void beforeShutdown(Registry registry);

    void localDeviceAdded(Registry registry, LocalDevice localDevice);

    void localDeviceRemoved(Registry registry, LocalDevice localDevice);

    void remoteDeviceAdded(Registry registry, RemoteDevice remoteDevice);

    void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice remoteDevice, Exception exc);

    void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice remoteDevice);

    void remoteDeviceRemoved(Registry registry, RemoteDevice remoteDevice);

    void remoteDeviceUpdated(Registry registry, RemoteDevice remoteDevice);
}

