package org.teleal.cling.protocol.async;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.Location;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.discovery.IncomingSearchRequest;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponse;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseDeviceType;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseRootDevice;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseRootDeviceUDN;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseServiceType;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseUDN;
import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.message.header.MXHeader;
import org.teleal.cling.model.message.header.RootDeviceHeader;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.ServiceTypeHeader;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ReceivingAsync;

/* JADX INFO: loaded from: classes.dex */
public class ReceivingSearch extends ReceivingAsync<IncomingSearchRequest> {
    private static final Logger log = Logger.getLogger(ReceivingSearch.class.getName());
    protected final Random randomGenerator;

    public ReceivingSearch(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> incomingDatagramMessage) {
        super(upnpService, new IncomingSearchRequest(incomingDatagramMessage));
        this.randomGenerator = new Random();
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected void execute() {
        if (getUpnpService().getRouter() == null) {
            log.fine("Router hasn't completed initialization, ignoring received search message");
            return;
        }
        if (!getInputMessage().isMANSSDPDiscover()) {
            log.fine("Invalid search request, no or invalid MAN ssdp:discover header: " + getInputMessage());
            return;
        }
        UpnpHeader searchTarget = getInputMessage().getSearchTarget();
        if (searchTarget == null) {
            log.fine("Invalid search request, did not contain ST header: " + getInputMessage());
            return;
        }
        List<NetworkAddress> activeStreamServers = getUpnpService().getRouter().getActiveStreamServers(getInputMessage().getLocalAddress());
        if (activeStreamServers.size() == 0) {
            log.fine("Aborting search response, no active stream servers found (network disabled?)");
            return;
        }
        Iterator<NetworkAddress> it = activeStreamServers.iterator();
        while (it.hasNext()) {
            sendResponses(searchTarget, it.next());
        }
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected boolean waitBeforeExecution() throws InterruptedException {
        Integer mx = getInputMessage().getMX();
        if (mx == null) {
            log.fine("Invalid search request, did not contain MX header: " + getInputMessage());
            return false;
        }
        if (mx.intValue() > 120 || mx.intValue() <= 0) {
            mx = MXHeader.DEFAULT_VALUE;
        }
        if (getUpnpService().getRegistry().getLocalDevices().size() <= 0) {
            return true;
        }
        int iNextInt = this.randomGenerator.nextInt(mx.intValue() * 1000);
        log.fine("Sleeping " + iNextInt + " milliseconds to avoid flooding with search responses");
        Thread.sleep((long) iNextInt);
        return true;
    }

    protected void sendResponses(UpnpHeader upnpHeader, NetworkAddress networkAddress) {
        if (upnpHeader instanceof STAllHeader) {
            sendSearchResponseAll(networkAddress);
            return;
        }
        if (upnpHeader instanceof RootDeviceHeader) {
            sendSearchResponseRootDevices(networkAddress);
            return;
        }
        if (upnpHeader instanceof UDNHeader) {
            sendSearchResponseUDN((UDN) upnpHeader.getValue(), networkAddress);
            return;
        }
        if (upnpHeader instanceof DeviceTypeHeader) {
            sendSearchResponseDeviceType((DeviceType) upnpHeader.getValue(), networkAddress);
            return;
        }
        if (upnpHeader instanceof ServiceTypeHeader) {
            sendSearchResponseServiceType((ServiceType) upnpHeader.getValue(), networkAddress);
            return;
        }
        log.warning("Non-implemented search request target: " + upnpHeader.getClass());
    }

    protected void sendSearchResponseAll(NetworkAddress networkAddress) {
        log.fine("Responding to 'all' search with advertisement messages for all local devices");
        for (LocalDevice localDevice : getUpnpService().getRegistry().getLocalDevices()) {
            log.finer("Sending root device messages: " + localDevice);
            Iterator<OutgoingSearchResponse> it = createDeviceMessages(localDevice, networkAddress).iterator();
            while (it.hasNext()) {
                getUpnpService().getRouter().send(it.next());
            }
            if (localDevice.hasEmbeddedDevices()) {
                for (LocalDevice localDevice2 : localDevice.findEmbeddedDevices()) {
                    log.finer("Sending embedded device messages: " + localDevice2);
                    Iterator<OutgoingSearchResponse> it2 = createDeviceMessages(localDevice2, networkAddress).iterator();
                    while (it2.hasNext()) {
                        getUpnpService().getRouter().send(it2.next());
                    }
                }
            }
            List<OutgoingSearchResponse> listCreateServiceTypeMessages = createServiceTypeMessages(localDevice, networkAddress);
            if (listCreateServiceTypeMessages.size() > 0) {
                log.finer("Sending service type messages");
                Iterator<OutgoingSearchResponse> it3 = listCreateServiceTypeMessages.iterator();
                while (it3.hasNext()) {
                    getUpnpService().getRouter().send(it3.next());
                }
            }
        }
    }

    protected List<OutgoingSearchResponse> createDeviceMessages(LocalDevice localDevice, NetworkAddress networkAddress) {
        ArrayList arrayList = new ArrayList();
        if (localDevice.isRoot()) {
            arrayList.add(new OutgoingSearchResponseRootDevice(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice));
        }
        arrayList.add(new OutgoingSearchResponseUDN(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice));
        arrayList.add(new OutgoingSearchResponseDeviceType(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice));
        return arrayList;
    }

    protected List<OutgoingSearchResponse> createServiceTypeMessages(LocalDevice localDevice, NetworkAddress networkAddress) {
        ArrayList arrayList = new ArrayList();
        for (ServiceType serviceType : localDevice.findServiceTypes()) {
            arrayList.add(new OutgoingSearchResponseServiceType(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice, serviceType));
        }
        return arrayList;
    }

    protected void sendSearchResponseRootDevices(NetworkAddress networkAddress) {
        log.fine("Responding to root device search with advertisement messages for all local root devices");
        for (LocalDevice localDevice : getUpnpService().getRegistry().getLocalDevices()) {
            getUpnpService().getRouter().send(new OutgoingSearchResponseRootDeviceUDN(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice));
        }
    }

    protected void sendSearchResponseUDN(UDN udn, NetworkAddress networkAddress) {
        Device device = getUpnpService().getRegistry().getDevice(udn, false);
        if (device == null || !(device instanceof LocalDevice)) {
            return;
        }
        log.fine("Responding to UDN device search: " + udn);
        LocalDevice localDevice = (LocalDevice) device;
        getUpnpService().getRouter().send(new OutgoingSearchResponseUDN(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice));
    }

    protected void sendSearchResponseDeviceType(DeviceType deviceType, NetworkAddress networkAddress) {
        log.fine("Responding to device type search: " + deviceType);
        for (Device device : getUpnpService().getRegistry().getDevices(deviceType)) {
            if (device instanceof LocalDevice) {
                log.finer("Sending matching device type search result for: " + device);
                LocalDevice localDevice = (LocalDevice) device;
                getUpnpService().getRouter().send(new OutgoingSearchResponseDeviceType(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice));
            }
        }
    }

    protected void sendSearchResponseServiceType(ServiceType serviceType, NetworkAddress networkAddress) {
        log.fine("Responding to service type search: " + serviceType);
        for (Device device : getUpnpService().getRegistry().getDevices(serviceType)) {
            if (device instanceof LocalDevice) {
                log.finer("Sending matching service type search result: " + device);
                LocalDevice localDevice = (LocalDevice) device;
                getUpnpService().getRouter().send(new OutgoingSearchResponseServiceType(getInputMessage(), getDescriptorLocation(networkAddress, localDevice), localDevice, serviceType));
            }
        }
    }

    protected Location getDescriptorLocation(NetworkAddress networkAddress, LocalDevice localDevice) {
        return new Location(networkAddress, getUpnpService().getConfiguration().getNamespace().getDescriptorPath(localDevice));
    }
}

