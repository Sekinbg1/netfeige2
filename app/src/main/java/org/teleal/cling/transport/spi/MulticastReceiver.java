package org.teleal.cling.transport.spi;

import java.net.NetworkInterface;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.MulticastReceiverConfiguration;

/* JADX INFO: loaded from: classes.dex */
public interface MulticastReceiver<C extends MulticastReceiverConfiguration> extends Runnable {
    C getConfiguration();

    void init(NetworkInterface networkInterface, Router router, DatagramProcessor datagramProcessor) throws InitializationException;

    void stop();
}

