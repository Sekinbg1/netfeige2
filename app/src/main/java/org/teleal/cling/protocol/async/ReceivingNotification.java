package org.teleal.cling.protocol.async;

import java.util.Iterator;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.discovery.IncomingNotificationRequest;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ReceivingAsync;
import org.teleal.cling.protocol.RetrieveRemoteDescriptors;

/* JADX INFO: loaded from: classes.dex */
public class ReceivingNotification extends ReceivingAsync<IncomingNotificationRequest> {
    private static final Logger log = Logger.getLogger(ReceivingNotification.class.getName());

    public ReceivingNotification(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> incomingDatagramMessage) {
        super(upnpService, new IncomingNotificationRequest(incomingDatagramMessage));
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected void execute() {
        UDN udn = getInputMessage().getUDN();
        if (udn == null) {
            log.fine("Ignoring notification message without UDN: " + getInputMessage());
            return;
        }
        RemoteDeviceIdentity remoteDeviceIdentity = new RemoteDeviceIdentity(getInputMessage());
        log.fine("Received device notification: " + remoteDeviceIdentity);
        try {
            RemoteDevice remoteDevice = new RemoteDevice(remoteDeviceIdentity);
            if (getInputMessage().isAliveMessage()) {
                log.fine("Received device ALIVE advertisement, descriptor location is: " + remoteDeviceIdentity.getDescriptorURL());
                if (remoteDeviceIdentity.getDescriptorURL() == null) {
                    log.finer("Ignoring message without location URL header: " + getInputMessage());
                    return;
                }
                if (remoteDeviceIdentity.getMaxAgeSeconds() == null) {
                    log.finer("Ignoring message without max-age header: " + getInputMessage());
                    return;
                }
                if (getUpnpService().getRegistry().update(remoteDeviceIdentity)) {
                    log.finer("Remote device was already known: " + udn);
                    return;
                }
                getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(new RetrieveRemoteDescriptors(getUpnpService(), remoteDevice));
                return;
            }
            if (getInputMessage().isByeByeMessage()) {
                log.fine("Received device BYEBYE advertisement");
                if (getUpnpService().getRegistry().removeDevice(remoteDevice)) {
                    log.fine("Removed remote device from registry: " + remoteDevice);
                    return;
                }
                return;
            }
            log.finer("Ignoring unknown notification message: " + getInputMessage());
        } catch (ValidationException e) {
            log.warning("Validation errors of device during discovery: " + remoteDeviceIdentity);
            Iterator<ValidationError> it = e.getErrors().iterator();
            while (it.hasNext()) {
                log.warning(it.next().toString());
            }
        }
    }
}

