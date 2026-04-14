package org.teleal.cling.transport.impl;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.teleal.cling.model.Constants;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.common.util.OS;

/* JADX INFO: loaded from: classes.dex */
public class NetworkAddressFactoryImpl implements NetworkAddressFactory {
    public static final int DEFAULT_TCP_HTTP_LISTEN_PORT = 0;
    private static Logger log = Logger.getLogger(NetworkAddressFactoryImpl.class.getName());
    protected List<InetAddress> bindAddresses;
    protected List<NetworkInterface> networkInterfaces;
    protected int streamListenPort;
    protected Set<String> useAddresses;
    protected Set<String> useInterfaces;

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public int getMulticastPort() {
        return Constants.UPNP_MULTICAST_PORT;
    }

    public NetworkAddressFactoryImpl() throws InitializationException {
        this(0);
    }

    public NetworkAddressFactoryImpl(int i) throws InitializationException {
        this.useInterfaces = new HashSet();
        this.useAddresses = new HashSet();
        this.networkInterfaces = new ArrayList();
        this.bindAddresses = new ArrayList();
        String property = System.getProperty(NetworkAddressFactory.SYSTEM_PROPERTY_NET_IFACES);
        if (property != null) {
            this.useInterfaces.addAll(Arrays.asList(property.split(",")));
        }
        String property2 = System.getProperty(NetworkAddressFactory.SYSTEM_PROPERTY_NET_ADDRESSES);
        if (property2 != null) {
            this.useAddresses.addAll(Arrays.asList(property2.split(",")));
        }
        if (OS.checkForLinux()) {
            Properties properties = System.getProperties();
            properties.setProperty("java.net.preferIPv6Stack", "true");
            System.setProperties(properties);
        }
        discoverNetworkInterfaces();
        discoverBindAddresses();
        if (this.networkInterfaces.size() == 0 || this.bindAddresses.size() == 0) {
            throw new InitializationException("Could not discover any bindable network interfaces and/or addresses");
        }
        this.streamListenPort = i;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getMulticastGroup() {
        try {
            return InetAddress.getByName(Constants.IPV4_UPNP_MULTICAST_GROUP);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public int getStreamListenPort() {
        return this.streamListenPort;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public NetworkInterface[] getNetworkInterfaces() {
        List<NetworkInterface> list = this.networkInterfaces;
        return (NetworkInterface[]) list.toArray(new NetworkInterface[list.size()]);
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress[] getBindAddresses() {
        List<InetAddress> list = this.bindAddresses;
        return (InetAddress[]) list.toArray(new InetAddress[list.size()]);
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public byte[] getHardwareAddress(InetAddress inetAddress) {
        try {
            NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(inetAddress);
            if (byInetAddress != null) {
                return byInetAddress.getHardwareAddress();
            }
            return null;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getBroadcastAddress(InetAddress inetAddress) {
        Iterator<NetworkInterface> it = this.networkInterfaces.iterator();
        while (it.hasNext()) {
            for (InterfaceAddress interfaceAddress : getInterfaceAddresses(it.next())) {
                if (interfaceAddress != null && interfaceAddress.getAddress().equals(inetAddress)) {
                    return interfaceAddress.getBroadcast();
                }
            }
        }
        return null;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean z, InetAddress inetAddress) {
        InetAddress bindAddressInSubnetOf = getBindAddressInSubnetOf(inetAddress);
        if (bindAddressInSubnetOf != null) {
            return bindAddressInSubnetOf;
        }
        log.finer("Could not find local bind address in same subnet as: " + inetAddress.getHostAddress());
        for (InetAddress inetAddress2 : getInetAddresses(networkInterface)) {
            if (z && (inetAddress2 instanceof Inet6Address)) {
                return inetAddress2;
            }
            if (!z && (inetAddress2 instanceof Inet4Address)) {
                return inetAddress2;
            }
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }

    protected List<InterfaceAddress> getInterfaceAddresses(NetworkInterface networkInterface) {
        return networkInterface.getInterfaceAddresses();
    }

    protected List<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses());
    }

    protected InetAddress getBindAddressInSubnetOf(InetAddress inetAddress) {
        Iterator<NetworkInterface> it = this.networkInterfaces.iterator();
        while (it.hasNext()) {
            for (InterfaceAddress interfaceAddress : getInterfaceAddresses(it.next())) {
                if (this.bindAddresses.contains(interfaceAddress.getAddress()) && isInSubnet(inetAddress.getAddress(), interfaceAddress.getAddress().getAddress(), interfaceAddress.getNetworkPrefixLength())) {
                    return interfaceAddress.getAddress();
                }
            }
        }
        return null;
    }

    protected boolean isInSubnet(byte[] bArr, byte[] bArr2, short s) {
        if (bArr.length != bArr2.length || s / 8 > bArr.length) {
            return false;
        }
        int i = 0;
        while (s >= 8 && i < bArr.length) {
            if (bArr[i] != bArr2[i]) {
                return false;
            }
            i++;
            s = (short) (s - 8);
        }
        byte b = (byte) (((1 << (8 - s)) - 1) ^ (-1));
        return (bArr[i] & b) == (bArr2[i] & b);
    }

    protected void discoverNetworkInterfaces() throws InitializationException {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                log.finer("Analyzing network interface: " + networkInterface.getDisplayName());
                if (isUsableNetworkInterface(networkInterface)) {
                    log.fine("Discovered usable network interface: " + networkInterface.getDisplayName());
                    this.networkInterfaces.add(networkInterface);
                } else {
                    log.finer("Ignoring non-usable network interface: " + networkInterface.getDisplayName());
                }
            }
        } catch (Exception e) {
            throw new InitializationException("Could not not analyze local network interfaces: " + e, e);
        }
    }

    protected boolean isUsableNetworkInterface(NetworkInterface networkInterface) throws Exception {
        if (!networkInterface.isUp()) {
            log.finer("Skipping network interface (down): " + networkInterface.getDisplayName());
            return false;
        }
        if (getInetAddresses(networkInterface).size() == 0) {
            log.finer("Skipping network interface without bound IP addresses: " + networkInterface.getDisplayName());
            return false;
        }
        if (networkInterface.getName().toLowerCase().startsWith("vmnet") || networkInterface.getDisplayName().toLowerCase().contains("vmnet")) {
            log.finer("Skipping network interface (VMWare): " + networkInterface.getDisplayName());
            return false;
        }
        if (networkInterface.getName().toLowerCase().startsWith("vnic")) {
            log.finer("Skipping network interface (Parallels): " + networkInterface.getDisplayName());
            return false;
        }
        if (networkInterface.getName().toLowerCase().startsWith("ppp")) {
            log.finer("Skipping network interface (PPP): " + networkInterface.getDisplayName());
            return false;
        }
        if (!networkInterface.supportsMulticast()) {
            log.finer("Skipping network interface (no multicast support): " + networkInterface.getDisplayName());
            return false;
        }
        if (networkInterface.isLoopback()) {
            log.finer("Skipping network interface (ignoring loopback): " + networkInterface.getDisplayName());
            return false;
        }
        if (this.useInterfaces.size() <= 0 || this.useInterfaces.contains(networkInterface.getName())) {
            return true;
        }
        log.finer("Skipping unwanted network interface (-Dorg.teleal.cling.network.useInterfaces): " + networkInterface.getName());
        return false;
    }

    protected void discoverBindAddresses() throws InitializationException {
        try {
            Iterator<NetworkInterface> it = this.networkInterfaces.iterator();
            while (it.hasNext()) {
                NetworkInterface next = it.next();
                log.finer("Discovering addresses of interface: " + next.getDisplayName());
                int i = 0;
                for (InetAddress inetAddress : getInetAddresses(next)) {
                    if (inetAddress == null) {
                        log.warning("Network has a null address: " + next.getDisplayName());
                    } else if (isUsableAddress(next, inetAddress)) {
                        log.fine("Discovered usable network interface address: " + inetAddress.getHostAddress());
                        i++;
                        this.bindAddresses.add(inetAddress);
                    } else {
                        log.finer("Ignoring non-usable network interface address: " + inetAddress.getHostAddress());
                    }
                }
                if (i == 0) {
                    log.finer("Network interface has no usable addresses, removing: " + next.getDisplayName());
                    it.remove();
                }
            }
        } catch (Exception e) {
            throw new InitializationException("Could not not analyze local network interfaces: " + e, e);
        }
    }

    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress inetAddress) {
        if (!(inetAddress instanceof Inet4Address)) {
            log.finer("Skipping unsupported non-IPv4 address: " + inetAddress);
            return false;
        }
        if (inetAddress.isLoopbackAddress()) {
            log.finer("Skipping loopback address: " + inetAddress);
            return false;
        }
        if (this.useAddresses.size() <= 0 || this.useAddresses.contains(inetAddress.getHostAddress())) {
            return true;
        }
        log.finer("Skipping unwanted address: " + inetAddress);
        return false;
    }

    static void displayInterfaceInformation(NetworkInterface networkInterface) throws SocketException {
        System.out.printf("Parent Info:%s\n", networkInterface.getParent());
        System.out.printf("Display name: %s\n", networkInterface.getDisplayName());
        System.out.printf("Name: %s\n", networkInterface.getName());
        Iterator it = Collections.list(networkInterface.getInetAddresses()).iterator();
        while (it.hasNext()) {
            System.out.printf("InetAddress: %s\n", (InetAddress) it.next());
        }
        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            System.out.println(" Interface Address");
            System.out.println("  Address: " + interfaceAddress.getAddress());
            System.out.println("  Broadcast: " + interfaceAddress.getBroadcast());
            System.out.println("  Prefix length: " + ((int) interfaceAddress.getNetworkPrefixLength()));
        }
        for (NetworkInterface networkInterface2 : Collections.list(networkInterface.getSubInterfaces())) {
            System.out.printf("\tSub Interface Display name: %s\n", networkInterface2.getDisplayName());
            System.out.printf("\tSub Interface Name: %s\n", networkInterface2.getName());
        }
        System.out.printf("Up? %s\n", Boolean.valueOf(networkInterface.isUp()));
        System.out.printf("Loopback? %s\n", Boolean.valueOf(networkInterface.isLoopback()));
        System.out.printf("PointToPoint? %s\n", Boolean.valueOf(networkInterface.isPointToPoint()));
        System.out.printf("Supports multicast? %s\n", Boolean.valueOf(networkInterface.supportsMulticast()));
        System.out.printf("Virtual? %s\n", Boolean.valueOf(networkInterface.isVirtual()));
        System.out.printf("Hardware address: %s\n", Arrays.toString(networkInterface.getHardwareAddress()));
        System.out.printf("MTU: %s\n", Integer.valueOf(networkInterface.getMTU()));
        System.out.printf("\n", new Object[0]);
    }
}

