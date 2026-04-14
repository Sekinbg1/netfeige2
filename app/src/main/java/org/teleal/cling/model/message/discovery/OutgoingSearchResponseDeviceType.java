package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.message.header.DeviceUSNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;

/* JADX INFO: loaded from: classes.dex */
public class OutgoingSearchResponseDeviceType extends OutgoingSearchResponse {
    public OutgoingSearchResponseDeviceType(IncomingDatagramMessage incomingDatagramMessage, Location location, LocalDevice localDevice) {
        super(incomingDatagramMessage, location, localDevice);
        getHeaders().add(UpnpHeader.Type.ST, new DeviceTypeHeader(localDevice.getType()));
        getHeaders().add(UpnpHeader.Type.USN, new DeviceUSNHeader(localDevice.getIdentity().getUdn(), localDevice.getType()));
    }
}

