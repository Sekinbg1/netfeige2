package org.teleal.cling.transport.impl;

import com.netfeige.common.Public_MsgID;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Logger;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.DatagramIO;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.UnsupportedDataException;

/* JADX INFO: loaded from: classes.dex */
public class DatagramIOImpl implements DatagramIO<DatagramIOConfigurationImpl> {
    private static Logger log = Logger.getLogger(DatagramIO.class.getName());
    protected final DatagramIOConfigurationImpl configuration;
    protected DatagramProcessor datagramProcessor;
    protected InetSocketAddress localAddress;
    protected Router router;
    protected MulticastSocket socket;

    public DatagramIOImpl(DatagramIOConfigurationImpl datagramIOConfigurationImpl) {
        this.configuration = datagramIOConfigurationImpl;
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public DatagramIOConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void init(InetAddress inetAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException {
        this.router = router;
        this.datagramProcessor = datagramProcessor;
        try {
            log.info("Creating bound socket (for datagram input/output) on: " + inetAddress);
            this.localAddress = new InetSocketAddress(inetAddress, 0);
            MulticastSocket multicastSocket = new MulticastSocket(this.localAddress);
            this.socket = multicastSocket;
            multicastSocket.setTimeToLive(this.configuration.getTimeToLive());
            this.socket.setReceiveBufferSize(32768);
        } catch (Exception e) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + e);
        }
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void stop() {
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        log.fine("Entering blocking receiving loop, listening for UDP datagrams on: " + this.socket.getLocalAddress());
        while (true) {
            try {
                int maxDatagramBytes = getConfiguration().getMaxDatagramBytes();
                DatagramPacket datagramPacket = new DatagramPacket(new byte[maxDatagramBytes], maxDatagramBytes);
                this.socket.receive(datagramPacket);
                log.fine("UDP datagram received from: " + datagramPacket.getAddress().getHostAddress() + Public_MsgID.PRO_SPACE + datagramPacket.getPort() + " on: " + this.localAddress);
                this.router.received(this.datagramProcessor.read(this.localAddress.getAddress(), datagramPacket));
            } catch (SocketException unused) {
                log.fine("Socket closed");
                try {
                    if (this.socket.isClosed()) {
                        return;
                    }
                    log.fine("Closing unicast socket");
                    this.socket.close();
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (UnsupportedDataException e2) {
                log.info("Could not read datagram: " + e2.getMessage());
            } catch (Exception e3) {
                throw new RuntimeException(e3);
            }
        }
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void send(OutgoingDatagramMessage outgoingDatagramMessage) {
        log.fine("Sending message from address: " + this.localAddress);
        DatagramPacket datagramPacketWrite = this.datagramProcessor.write(outgoingDatagramMessage);
        log.fine("Sending UDP datagram packet to: " + outgoingDatagramMessage.getDestinationAddress() + Public_MsgID.PRO_SPACE + outgoingDatagramMessage.getDestinationPort());
        send(datagramPacketWrite);
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void send(DatagramPacket datagramPacket) {
        log.fine("Sending message from address: " + this.localAddress);
        try {
            this.socket.send(datagramPacket);
        } catch (RuntimeException e) {
            throw e;
        } catch (SocketException unused) {
            log.fine("Socket closed, aborting datagram send to: " + datagramPacket.getAddress());
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
}

