package org.teleal.cling.transport.spi;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.DatagramIOConfiguration;

/* JADX INFO: loaded from: classes.dex */
public interface DatagramIO<C extends DatagramIOConfiguration> extends Runnable {
    C getConfiguration();

    void init(InetAddress inetAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException;

    void send(DatagramPacket datagramPacket);

    void send(OutgoingDatagramMessage outgoingDatagramMessage);

    void stop();
}

