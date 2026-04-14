package org.teleal.cling.protocol.async;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.Location;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequest;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestDeviceType;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestRootDevice;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestServiceType;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestUDN;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.protocol.SendingAsync;

/* JADX INFO: loaded from: classes.dex */
public abstract class SendingNotification extends SendingAsync {
    private static final Logger log = Logger.getLogger(SendingNotification.class.getName());
    private LocalDevice device;

    protected int getBulkIntervalMilliseconds() {
        return 150;
    }

    protected int getBulkRepeat() {
        return 3;
    }

    protected abstract NotificationSubtype getNotificationSubtype();

    public SendingNotification(UpnpService upnpService, LocalDevice localDevice) {
        super(upnpService);
        this.device = localDevice;
    }

    public LocalDevice getDevice() {
        return this.device;
    }

    @Override // org.teleal.cling.protocol.SendingAsync
    protected void execute() {
        List<NetworkAddress> activeStreamServers = getUpnpService().getRouter().getActiveStreamServers(null);
        if (activeStreamServers.size() == 0) {
            log.fine("Aborting notifications, no active stream servers found (network disabled?)");
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<NetworkAddress> it = activeStreamServers.iterator();
        while (it.hasNext()) {
            arrayList.add(new Location(it.next(), getUpnpService().getConfiguration().getNamespace().getDescriptorPath(getDevice())));
        }
        for (int i = 0; i < getBulkRepeat(); i++) {
            try {
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    sendMessages((Location) it2.next());
                }
                log.finer("Sleeping " + getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep((long) getBulkIntervalMilliseconds());
            } catch (InterruptedException e) {
                log.warning("Advertisement thread was interrupted: " + e);
            }
        }
    }

    public void sendMessages(Location location) {
        log.finer("Sending root device messages: " + getDevice());
        Iterator<OutgoingNotificationRequest> it = createDeviceMessages(getDevice(), location).iterator();
        while (it.hasNext()) {
            getUpnpService().getRouter().send(it.next());
        }
        if (getDevice().hasEmbeddedDevices()) {
            for (LocalDevice localDevice : getDevice().findEmbeddedDevices()) {
                log.finer("Sending embedded device messages: " + localDevice);
                Iterator<OutgoingNotificationRequest> it2 = createDeviceMessages(localDevice, location).iterator();
                while (it2.hasNext()) {
                    getUpnpService().getRouter().send(it2.next());
                }
            }
        }
        List<OutgoingNotificationRequest> listCreateServiceTypeMessages = createServiceTypeMessages(getDevice(), location);
        if (listCreateServiceTypeMessages.size() > 0) {
            log.finer("Sending service type messages");
            Iterator<OutgoingNotificationRequest> it3 = listCreateServiceTypeMessages.iterator();
            while (it3.hasNext()) {
                getUpnpService().getRouter().send(it3.next());
            }
        }
    }

    protected List<OutgoingNotificationRequest> createDeviceMessages(LocalDevice localDevice, Location location) {
        ArrayList arrayList = new ArrayList();
        if (localDevice.isRoot()) {
            arrayList.add(new OutgoingNotificationRequestRootDevice(location, localDevice, getNotificationSubtype()));
        }
        arrayList.add(new OutgoingNotificationRequestUDN(location, localDevice, getNotificationSubtype()));
        arrayList.add(new OutgoingNotificationRequestDeviceType(location, localDevice, getNotificationSubtype()));
        return arrayList;
    }

    protected List<OutgoingNotificationRequest> createServiceTypeMessages(LocalDevice localDevice, Location location) {
        ArrayList arrayList = new ArrayList();
        for (ServiceType serviceType : localDevice.findServiceTypes()) {
            arrayList.add(new OutgoingNotificationRequestServiceType(location, localDevice, getNotificationSubtype(), serviceType));
        }
        return arrayList;
    }
}

