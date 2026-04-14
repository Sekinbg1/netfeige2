package org.teleal.cling.transport.spi;

import java.net.InetAddress;
import java.net.NetworkInterface;

/* JADX INFO: loaded from: classes.dex */
public interface NetworkAddressFactory {
    public static final String SYSTEM_PROPERTY_NET_ADDRESSES = "org.teleal.cling.network.useAddresses";
    public static final String SYSTEM_PROPERTY_NET_IFACES = "org.teleal.cling.network.useInterfaces";

    InetAddress[] getBindAddresses();

    InetAddress getBroadcastAddress(InetAddress inetAddress);

    byte[] getHardwareAddress(InetAddress inetAddress);

    InetAddress getLocalAddress(NetworkInterface networkInterface, boolean z, InetAddress inetAddress) throws IllegalStateException;

    InetAddress getMulticastGroup();

    int getMulticastPort();

    NetworkInterface[] getNetworkInterfaces();

    int getStreamListenPort();
}

