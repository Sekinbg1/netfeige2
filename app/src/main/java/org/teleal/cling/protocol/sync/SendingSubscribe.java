package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.gena.IncomingSubscribeResponseMessage;
import org.teleal.cling.model.message.gena.OutgoingSubscribeRequestMessage;
import org.teleal.cling.protocol.SendingSync;

/* JADX INFO: loaded from: classes.dex */
public class SendingSubscribe extends SendingSync<OutgoingSubscribeRequestMessage, IncomingSubscribeResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingSubscribe.class.getName());
    protected final RemoteGENASubscription subscription;

    public SendingSubscribe(UpnpService upnpService, RemoteGENASubscription remoteGENASubscription) {
        super(upnpService, new OutgoingSubscribeRequestMessage(remoteGENASubscription, remoteGENASubscription.getEventCallbackURLs(upnpService.getRouter().getActiveStreamServers(remoteGENASubscription.getService().getDevice().getIdentity().getDiscoveredOnLocalAddress()), upnpService.getConfiguration().getNamespace())));
        this.subscription = remoteGENASubscription;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.teleal.cling.protocol.SendingSync
    public IncomingSubscribeResponseMessage executeSync() {
        if (!getInputMessage().hasCallbackURLs()) {
            log.fine("Subscription failed, no active local callback URLs available (network disabled?)");
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.1
                @Override // java.lang.Runnable
                public void run() {
                    SendingSubscribe.this.subscription.fail(null);
                }
            });
            return null;
        }
        log.fine("Sending subscription request: " + getInputMessage());
        try {
            getUpnpService().getRegistry().lockRemoteSubscriptions();
            StreamResponseMessage streamResponseMessageSend = getUpnpService().getRouter().send(getInputMessage());
            if (streamResponseMessageSend == null) {
                log.fine("Subscription failed, no response received");
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.2
                    @Override // java.lang.Runnable
                    public void run() {
                        SendingSubscribe.this.subscription.fail(null);
                    }
                });
                return null;
            }
            final IncomingSubscribeResponseMessage incomingSubscribeResponseMessage = new IncomingSubscribeResponseMessage(streamResponseMessageSend);
            if (streamResponseMessageSend.getOperation().isFailed()) {
                log.fine("Subscription failed, response was: " + incomingSubscribeResponseMessage);
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.3
                    @Override // java.lang.Runnable
                    public void run() {
                        SendingSubscribe.this.subscription.fail(incomingSubscribeResponseMessage.getOperation());
                    }
                });
            } else if (!incomingSubscribeResponseMessage.isVaildHeaders()) {
                log.severe("Subscription failed, invalid or missing (SID, Timeout) response headers");
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.4
                    @Override // java.lang.Runnable
                    public void run() {
                        SendingSubscribe.this.subscription.fail(incomingSubscribeResponseMessage.getOperation());
                    }
                });
            } else {
                log.fine("Subscription established, adding to registry, response was: " + streamResponseMessageSend);
                this.subscription.setSubscriptionId(incomingSubscribeResponseMessage.getSubscriptionId());
                this.subscription.setActualSubscriptionDurationSeconds(incomingSubscribeResponseMessage.getSubscriptionDurationSeconds());
                getUpnpService().getRegistry().addRemoteSubscription(this.subscription);
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.5
                    @Override // java.lang.Runnable
                    public void run() {
                        SendingSubscribe.this.subscription.establish();
                    }
                });
            }
            return incomingSubscribeResponseMessage;
        } finally {
            getUpnpService().getRegistry().unlockRemoteSubscriptions();
        }
    }
}

