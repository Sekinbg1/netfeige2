package org.teleal.cling.support.igd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.igd.callback.PortMappingAdd;
import org.teleal.cling.support.igd.callback.PortMappingDelete;
import org.teleal.cling.support.model.PortMapping;

/* JADX INFO: loaded from: classes.dex */
public class PortMappingListener extends DefaultRegistryListener {
    protected Map<Service, List<PortMapping>> activePortMappings;
    protected PortMapping[] portMappings;
    private static final Logger log = Logger.getLogger(PortMappingListener.class.getName());
    public static final DeviceType IGD_DEVICE_TYPE = new UDADeviceType("InternetGatewayDevice", 1);
    public static final DeviceType CONNECTION_DEVICE_TYPE = new UDADeviceType("WANConnectionDevice", 1);
    public static final ServiceType IP_SERVICE_TYPE = new UDAServiceType("WANIPConnection", 1);
    public static final ServiceType PPP_SERVICE_TYPE = new UDAServiceType("WANPPPConnection", 1);

    public PortMappingListener(PortMapping portMapping) {
        this(new PortMapping[]{portMapping});
    }

    public PortMappingListener(PortMapping[] portMappingArr) {
        this.activePortMappings = new HashMap();
        this.portMappings = portMappingArr;
    }

    @Override // org.teleal.cling.registry.DefaultRegistryListener
    public synchronized void deviceAdded(Registry registry, Device device) {
        Service serviceDiscoverConnectionService = discoverConnectionService(device);
        if (serviceDiscoverConnectionService == null) {
            return;
        }
        log.fine("Activating port mappings on: " + serviceDiscoverConnectionService);
        final ArrayList arrayList = new ArrayList();
        for (final PortMapping portMapping : this.portMappings) {
            new PortMappingAdd(serviceDiscoverConnectionService, registry.getUpnpService().getControlPoint(), portMapping) { // from class: org.teleal.cling.support.igd.PortMappingListener.1
                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    PortMappingListener.log.fine("Port mapping added: " + portMapping);
                    arrayList.add(portMapping);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    PortMappingListener.this.handleFailureMessage("Failed to add port mapping: " + portMapping);
                    PortMappingListener.this.handleFailureMessage("Reason: " + str);
                }
            }.run();
        }
        this.activePortMappings.put(serviceDiscoverConnectionService, arrayList);
    }

    @Override // org.teleal.cling.registry.DefaultRegistryListener
    public synchronized void deviceRemoved(Registry registry, Device device) {
        for (Service service : device.findServices()) {
            Iterator<Map.Entry<Service, List<PortMapping>>> it = this.activePortMappings.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Service, List<PortMapping>> next = it.next();
                if (next.getKey().equals(service)) {
                    if (next.getValue().size() > 0) {
                        handleFailureMessage("Device disappeared, couldn't delete port mappings: " + next.getValue().size());
                    }
                    it.remove();
                }
            }
        }
    }

    @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
    public synchronized void beforeShutdown(Registry registry) {
        for (Map.Entry<Service, List<PortMapping>> entry : this.activePortMappings.entrySet()) {
            final Iterator<PortMapping> it = entry.getValue().iterator();
            while (it.hasNext()) {
                final PortMapping next = it.next();
                log.fine("Trying to delete port mapping on IGD: " + next);
                new PortMappingDelete(entry.getKey(), registry.getUpnpService().getControlPoint(), next) { // from class: org.teleal.cling.support.igd.PortMappingListener.2
                    @Override // org.teleal.cling.controlpoint.ActionCallback
                    public void success(ActionInvocation actionInvocation) {
                        PortMappingListener.log.fine("Port mapping deleted: " + next);
                        it.remove();
                    }

                    @Override // org.teleal.cling.controlpoint.ActionCallback
                    public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                        PortMappingListener.this.handleFailureMessage("Failed to delete port mapping: " + next);
                        PortMappingListener.this.handleFailureMessage("Reason: " + str);
                    }
                }.run();
            }
        }
    }

    protected Service discoverConnectionService(Device device) {
        if (!device.getType().equals(IGD_DEVICE_TYPE)) {
            return null;
        }
        Device[] deviceArrFindDevices = device.findDevices(CONNECTION_DEVICE_TYPE);
        if (deviceArrFindDevices.length == 0) {
            log.fine("IGD doesn't support '" + CONNECTION_DEVICE_TYPE + "': " + device);
            return null;
        }
        Device device2 = deviceArrFindDevices[0];
        log.fine("Using first discovered WAN connection device: " + device2);
        Service serviceFindService = device2.findService(IP_SERVICE_TYPE);
        Service serviceFindService2 = device2.findService(PPP_SERVICE_TYPE);
        if (serviceFindService == null && serviceFindService2 == null) {
            log.fine("IGD doesn't support IP or PPP WAN connection service: " + device);
        }
        return serviceFindService != null ? serviceFindService : serviceFindService2;
    }

    protected void handleFailureMessage(String str) {
        log.warning(str);
    }
}

