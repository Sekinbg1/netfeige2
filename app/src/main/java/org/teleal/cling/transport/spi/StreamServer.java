package org.teleal.cling.transport.spi;

import java.net.InetAddress;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.StreamServerConfiguration;

/* JADX INFO: loaded from: classes.dex */
public interface StreamServer<C extends StreamServerConfiguration> extends Runnable {
    C getConfiguration();

    int getPort();

    void init(InetAddress inetAddress, Router router) throws InitializationException;

    void stop();
}

