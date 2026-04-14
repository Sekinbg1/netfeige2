package org.teleal.cling.transport;

import java.net.InetAddress;
import java.util.List;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.UpnpStream;

/* JADX INFO: loaded from: classes.dex */
public interface Router {
    void broadcast(byte[] bArr);

    List<NetworkAddress> getActiveStreamServers(InetAddress inetAddress);

    UpnpServiceConfiguration getConfiguration();

    NetworkAddressFactory getNetworkAddressFactory();

    ProtocolFactory getProtocolFactory();

    void received(IncomingDatagramMessage incomingDatagramMessage);

    void received(UpnpStream upnpStream);

    StreamResponseMessage send(StreamRequestMessage streamRequestMessage);

    void send(OutgoingDatagramMessage outgoingDatagramMessage);

    void shutdown();
}

