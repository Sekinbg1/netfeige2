package org.teleal.cling.protocol;

import java.net.URL;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.protocol.async.SendingNotificationAlive;
import org.teleal.cling.protocol.async.SendingNotificationByebye;
import org.teleal.cling.protocol.async.SendingSearch;
import org.teleal.cling.protocol.sync.SendingAction;
import org.teleal.cling.protocol.sync.SendingEvent;
import org.teleal.cling.protocol.sync.SendingRenewal;
import org.teleal.cling.protocol.sync.SendingSubscribe;
import org.teleal.cling.protocol.sync.SendingUnsubscribe;

/* JADX INFO: loaded from: classes.dex */
public interface ProtocolFactory {
    ReceivingAsync createReceivingAsync(IncomingDatagramMessage incomingDatagramMessage) throws ProtocolCreationException;

    ReceivingSync createReceivingSync(StreamRequestMessage streamRequestMessage) throws ProtocolCreationException;

    SendingAction createSendingAction(ActionInvocation actionInvocation, URL url);

    SendingEvent createSendingEvent(LocalGENASubscription localGENASubscription);

    SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice);

    SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice);

    SendingRenewal createSendingRenewal(RemoteGENASubscription remoteGENASubscription);

    SendingSearch createSendingSearch(UpnpHeader upnpHeader, int i);

    SendingSubscribe createSendingSubscribe(RemoteGENASubscription remoteGENASubscription);

    SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription remoteGENASubscription);

    UpnpService getUpnpService();
}

