package org.teleal.cling.transport;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.UpnpStream;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class SwitchableRouterImpl implements SwitchableRouter {
    private static final Logger log = Logger.getLogger(Router.class.getName());
    private final UpnpServiceConfiguration configuration;
    private final ProtocolFactory protocolFactory;
    protected Lock readLock;
    private Router router;
    protected ReentrantReadWriteLock routerLock;
    protected Lock writeLock;

    protected int getLockTimeoutMillis() {
        return 6000;
    }

    public SwitchableRouterImpl(UpnpServiceConfiguration upnpServiceConfiguration, ProtocolFactory protocolFactory) {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);
        this.routerLock = reentrantReadWriteLock;
        this.readLock = reentrantReadWriteLock.readLock();
        this.writeLock = this.routerLock.writeLock();
        this.configuration = upnpServiceConfiguration;
        this.protocolFactory = protocolFactory;
    }

    @Override // org.teleal.cling.transport.Router
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.Router
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public boolean isEnabled() throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null;
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public boolean enable() throws RouterLockAcquisitionException {
        boolean z;
        lock(this.writeLock);
        try {
            if (this.router == null) {
                try {
                    log.fine("Enabling network transport router");
                    this.router = new RouterImpl(getConfiguration(), getProtocolFactory());
                    z = true;
                } catch (InitializationException e) {
                    handleStartFailure(e);
                    z = false;
                    return z;
                }
            } else {
                z = false;
            }
            return z;
        } finally {
            unlock(this.writeLock);
        }
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public void handleStartFailure(InitializationException initializationException) {
        log.severe("Unable to initialize network router: " + initializationException);
        log.severe("Cause: " + Exceptions.unwrap(initializationException));
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public boolean disable() throws RouterLockAcquisitionException {
        boolean z;
        lock(this.writeLock);
        try {
            if (this.router != null) {
                log.fine("Disabling network transport router");
                this.router.shutdown();
                this.router = null;
                z = true;
            } else {
                z = false;
            }
            return z;
        } finally {
            unlock(this.writeLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public NetworkAddressFactory getNetworkAddressFactory() throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null ? this.router.getNetworkAddressFactory() : new DisabledNetworkAddressFactory();
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public List<NetworkAddress> getActiveStreamServers(InetAddress inetAddress) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null ? this.router.getActiveStreamServers(inetAddress) : Collections.EMPTY_LIST;
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void shutdown() throws RouterLockAcquisitionException {
        disable();
    }

    @Override // org.teleal.cling.transport.Router
    public void received(IncomingDatagramMessage incomingDatagramMessage) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.received(incomingDatagramMessage);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void received(UpnpStream upnpStream) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.received(upnpStream);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void send(OutgoingDatagramMessage outgoingDatagramMessage) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.send(outgoingDatagramMessage);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public StreamResponseMessage send(StreamRequestMessage streamRequestMessage) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null ? this.router.send(streamRequestMessage) : null;
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void broadcast(byte[] bArr) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.broadcast(bArr);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    protected void lock(Lock lock, int i) throws RouterLockAcquisitionException {
        try {
            log.finest("Trying to obtain lock with timeout milliseconds '" + i + "': " + lock.getClass().getSimpleName());
            if (lock.tryLock(i, TimeUnit.MILLISECONDS)) {
                log.finest("Acquired router lock: " + lock.getClass().getSimpleName());
                return;
            }
            throw new RouterLockAcquisitionException("Failed to acquire router lock: " + lock.getClass().getSimpleName());
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire router lock: " + lock.getClass().getSimpleName(), e);
        }
    }

    protected void lock(Lock lock) throws RouterLockAcquisitionException {
        lock(lock, getLockTimeoutMillis());
    }

    protected void unlock(Lock lock) {
        log.finest("Releasing router lock: " + lock.getClass().getSimpleName());
        lock.unlock();
    }

    class DisabledNetworkAddressFactory implements NetworkAddressFactory {
        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress[] getBindAddresses() {
            return new InetAddress[0];
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress getBroadcastAddress(InetAddress inetAddress) {
            return null;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public byte[] getHardwareAddress(InetAddress inetAddress) {
            return new byte[0];
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean z, InetAddress inetAddress) throws IllegalStateException {
            return null;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress getMulticastGroup() {
            return null;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public int getMulticastPort() {
            return 0;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public NetworkInterface[] getNetworkInterfaces() {
            return new NetworkInterface[0];
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public int getStreamListenPort() {
            return 0;
        }

        DisabledNetworkAddressFactory() {
        }
    }

    public static class RouterLockAcquisitionException extends RuntimeException {
        public RouterLockAcquisitionException() {
        }

        public RouterLockAcquisitionException(String str) {
            super(str);
        }

        public RouterLockAcquisitionException(String str, Throwable th) {
            super(str, th);
        }

        public RouterLockAcquisitionException(Throwable th) {
            super(th);
        }
    }
}

