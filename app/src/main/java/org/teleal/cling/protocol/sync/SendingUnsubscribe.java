package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.gena.OutgoingUnsubscribeRequestMessage;
import org.teleal.cling.protocol.SendingSync;

/* JADX INFO: loaded from: classes.dex */
public class SendingUnsubscribe extends SendingSync<OutgoingUnsubscribeRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingUnsubscribe.class.getName());
    protected final RemoteGENASubscription subscription;

    public SendingUnsubscribe(UpnpService upnpService, RemoteGENASubscription remoteGENASubscription) {
        super(upnpService, new OutgoingUnsubscribeRequestMessage(remoteGENASubscription));
        this.subscription = remoteGENASubscription;
    }

    @Override // org.teleal.cling.protocol.SendingSync
    protected StreamResponseMessage executeSync() {
        log.fine("Sending unsubscribe request: " + getInputMessage());
        final StreamResponseMessage streamResponseMessageSend = getUpnpService().getRouter().send(getInputMessage());
        getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingUnsubscribe.1
            @Override // java.lang.Runnable
            public void run() {
                StreamResponseMessage streamResponseMessage = streamResponseMessageSend;
                if (streamResponseMessage == null) {
                    SendingUnsubscribe.log.fine("Unsubscribe failed, no response received");
                    SendingUnsubscribe.this.subscription.end(CancelReason.UNSUBSCRIBE_FAILED, null);
                    return;
                }
                if (streamResponseMessage.getOperation().isFailed()) {
                    SendingUnsubscribe.log.fine("Unsubscribe failed, response was: " + streamResponseMessageSend);
                    SendingUnsubscribe.this.subscription.end(CancelReason.UNSUBSCRIBE_FAILED, streamResponseMessageSend.getOperation());
                    return;
                }
                SendingUnsubscribe.log.fine("Unsubscribe successful, response was: " + streamResponseMessageSend);
                SendingUnsubscribe.this.subscription.end(null, streamResponseMessageSend.getOperation());
            }
        });
        return streamResponseMessageSend;
    }
}

