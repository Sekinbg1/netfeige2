package org.teleal.cling.android;

import android.net.wifi.WifiManager;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.Constants;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;

/* JADX INFO: loaded from: classes.dex */
public class AndroidNetworkAddressFactory implements NetworkAddressFactory {
    private static final Logger log = Logger.getLogger(NetworkAddressFactory.class.getName());
    protected List<InetAddress> bindAddresses = new ArrayList();
    protected NetworkInterface wifiInterface;

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getBroadcastAddress(InetAddress inetAddress) {
        return null;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public byte[] getHardwareAddress(InetAddress inetAddress) {
        return null;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public int getMulticastPort() {
        return Constants.UPNP_MULTICAST_PORT;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public int getStreamListenPort() {
        return 0;
    }

    public AndroidNetworkAddressFactory(WifiManager wifiManager) throws InitializationException {
        NetworkInterface wifiNetworkInterface = getWifiNetworkInterface(wifiManager);
        this.wifiInterface = wifiNetworkInterface;
        if (wifiNetworkInterface == null) {
            throw new InitializationException("Could not discover WiFi network interface");
        }
        log.info("Discovered WiFi network interface: " + this.wifiInterface.getDisplayName());
        discoverBindAddresses();
    }

    protected void discoverBindAddresses() throws InitializationException {
        try {
            log.finer("Discovering addresses of interface: " + this.wifiInterface.getDisplayName());
            for (InetAddress inetAddress : getInetAddresses(this.wifiInterface)) {
                if (inetAddress == null) {
                    log.warning("Network has a null address: " + this.wifiInterface.getDisplayName());
                } else if (isUsableAddress(inetAddress)) {
                    log.fine("Discovered usable network interface address: " + inetAddress.getHostAddress());
                    this.bindAddresses.add(inetAddress);
                } else {
                    log.finer("Ignoring non-usable network interface address: " + inetAddress.getHostAddress());
                }
            }
        } catch (Exception e) {
            throw new InitializationException("Could not not analyze local network interfaces: " + e, e);
        }
    }

    protected boolean isUsableAddress(InetAddress inetAddress) {
        if (inetAddress instanceof Inet4Address) {
            return true;
        }
        log.finer("Skipping unsupported non-IPv4 address: " + inetAddress);
        return false;
    }

    protected List<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses());
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
    public NetworkInterface[] getNetworkInterfaces() {
        return new NetworkInterface[]{this.wifiInterface};
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress[] getBindAddresses() {
        List<InetAddress> list = this.bindAddresses;
        return (InetAddress[]) list.toArray(new InetAddress[list.size()]);
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean z, InetAddress inetAddress) {
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

    public static NetworkInterface getWifiNetworkInterface(WifiManager wifiManager) {
        if (ModelUtil.ANDROID_EMULATOR) {
            return getEmulatorWifiNetworkInterface(wifiManager);
        }
        return getRealWifiNetworkInterface(wifiManager);
    }

    public static NetworkInterface getEmulatorWifiNetworkInterface(WifiManager wifiManager) {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                Iterator it = Collections.list(networkInterface.getInetAddresses()).iterator();
                while (it.hasNext()) {
                    if (!((InetAddress) it.next()).isLoopbackAddress()) {
                        return networkInterface;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new InitializationException("Could not find emulator's network interface: " + e, e);
        }
    }

    public static NetworkInterface getRealWifiNetworkInterface(WifiManager wifiManager) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            int iReverseBytes = Integer.reverseBytes(ipAddress);
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterfaceNextElement = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterfaceNextElement.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    int iByteArrayToInt = byteArrayToInt(inetAddresses.nextElement().getAddress(), 0);
                    if (iByteArrayToInt == ipAddress || iByteArrayToInt == iReverseBytes) {
                        return networkInterfaceNextElement;
                    }
                }
            }
            return null;
        } catch (SocketException unused) {
            log.info("No network interfaces available");
            return null;
        }
    }

    static int byteArrayToInt(byte[] bArr, int i) {
        if (bArr == null || bArr.length - i < 4) {
            return -1;
        }
        int i2 = (bArr[i] & 255) << 24;
        int i3 = (bArr[i + 1] & 255) << 16;
        return i2 + i3 + ((bArr[i + 2] & 255) << 8) + (bArr[i + 3] & 255);
    }
}

