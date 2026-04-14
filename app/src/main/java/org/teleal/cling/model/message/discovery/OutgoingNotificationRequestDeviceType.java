package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.message.header.DeviceUSNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.NotificationSubtype;

/* JADX INFO: loaded from: classes.dex */
public class OutgoingNotificationRequestDeviceType extends OutgoingNotificationRequest {
    public OutgoingNotificationRequestDeviceType(Location location, LocalDevice localDevice, NotificationSubtype notificationSubtype) {
        super(location, localDevice, notificationSubtype);
        getHeaders().add(UpnpHeader.Type.NT, new DeviceTypeHeader(localDevice.getType()));
        getHeaders().add(UpnpHeader.Type.USN, new DeviceUSNHeader(localDevice.getIdentity().getUdn(), localDevice.getType()));
    }
}

