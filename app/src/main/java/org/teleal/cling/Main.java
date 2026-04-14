package org.teleal.cling;

import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

/* JADX INFO: loaded from: classes.dex */
public class Main {
    public static void main(String[] strArr) throws Exception {
        System.out.println("Starting Cling...");
        UpnpServiceImpl upnpServiceImpl = new UpnpServiceImpl(new RegistryListener() { // from class: org.teleal.cling.Main.1
            @Override // org.teleal.cling.registry.RegistryListener
            public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice remoteDevice) {
                System.out.println("Discovery started: " + remoteDevice.getDisplayString());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice remoteDevice, Exception exc) {
                System.out.println("Discovery failed: " + remoteDevice.getDisplayString() + " => " + exc);
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void remoteDeviceAdded(Registry registry, RemoteDevice remoteDevice) {
                System.out.println("Remote device added: " + remoteDevice.getDisplayString());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void remoteDeviceUpdated(Registry registry, RemoteDevice remoteDevice) {
                System.out.println("Remote device updated: " + remoteDevice.getDisplayString());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void remoteDeviceRemoved(Registry registry, RemoteDevice remoteDevice) {
                System.out.println("Remote device removed: " + remoteDevice.getDisplayString());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void localDeviceAdded(Registry registry, LocalDevice localDevice) {
                System.out.println("Local device added: " + localDevice.getDisplayString());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void localDeviceRemoved(Registry registry, LocalDevice localDevice) {
                System.out.println("Local device removed: " + localDevice.getDisplayString());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void beforeShutdown(Registry registry) {
                System.out.println("Before shutdown, the registry has devices: " + registry.getDevices().size());
            }

            @Override // org.teleal.cling.registry.RegistryListener
            public void afterShutdown() {
                System.out.println("Shutdown of registry complete!");
            }
        });
        upnpServiceImpl.getControlPoint().search(new STAllHeader());
        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(10000L);
        System.out.println("Stopping Cling...");
        upnpServiceImpl.shutdown();
    }
}

