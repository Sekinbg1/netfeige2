package org.teleal.cling.mock;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import org.teleal.cling.DefaultUpnpServiceConfiguration;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.controlpoint.ControlPointImpl;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.protocol.ProtocolFactoryImpl;
import org.teleal.cling.protocol.async.SendingNotificationAlive;
import org.teleal.cling.protocol.async.SendingSearch;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryImpl;
import org.teleal.cling.registry.RegistryMaintainer;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.impl.NetworkAddressFactoryImpl;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.UpnpStream;

/* JADX INFO: loaded from: classes.dex */
public class MockUpnpService implements UpnpService {
    private List<byte[]> broadcastedBytes;
    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    private List<IncomingDatagramMessage> incomingDatagramMessages;
    protected final NetworkAddressFactory networkAddressFactory;
    private List<OutgoingDatagramMessage> outgoingDatagramMessages;
    protected final ProtocolFactory protocolFactory;
    private List<UpnpStream> receivedUpnpStreams;
    protected final Registry registry;
    protected final Router router;
    private List<StreamRequestMessage> sentStreamRequestMessages;

    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage streamRequestMessage) {
        return null;
    }

    public StreamResponseMessage[] getStreamResponseMessages() {
        return null;
    }

    public MockUpnpService() {
        this(false, false, false);
    }

    public MockUpnpService(boolean z, boolean z2) {
        this(z, z2, false);
    }

    public MockUpnpService(boolean z, final boolean z2, final boolean z3) {
        this.incomingDatagramMessages = new ArrayList();
        this.outgoingDatagramMessages = new ArrayList();
        this.receivedUpnpStreams = new ArrayList();
        this.sentStreamRequestMessages = new ArrayList();
        this.broadcastedBytes = new ArrayList();
        this.configuration = new DefaultUpnpServiceConfiguration(false) { // from class: org.teleal.cling.mock.MockUpnpService.1
            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
            protected NetworkAddressFactory createNetworkAddressFactory(int i) {
                return new NetworkAddressFactoryImpl(i) { // from class: org.teleal.cling.mock.MockUpnpService.1.1
                    @Override // org.teleal.cling.transport.impl.NetworkAddressFactoryImpl
                    protected boolean isUsableNetworkInterface(NetworkInterface networkInterface) throws Exception {
                        return networkInterface.isLoopback();
                    }

                    @Override // org.teleal.cling.transport.impl.NetworkAddressFactoryImpl
                    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress inetAddress) {
                        return inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address);
                    }
                };
            }

            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
            public Executor getRegistryMaintainerExecutor() {
                if (z2) {
                    return new Executor() { // from class: org.teleal.cling.mock.MockUpnpService.1.2
                        @Override // java.util.concurrent.Executor
                        public void execute(Runnable runnable) {
                            new Thread(runnable).start();
                        }
                    };
                }
                return createDefaultExecutor();
            }

            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
            protected Executor createDefaultExecutor() {
                return z3 ? super.createDefaultExecutor() : new Executor() { // from class: org.teleal.cling.mock.MockUpnpService.1.3
                    @Override // java.util.concurrent.Executor
                    public void execute(Runnable runnable) {
                        runnable.run();
                    }
                };
            }
        };
        this.protocolFactory = createProtocolFactory(this, z);
        this.registry = new RegistryImpl(this) { // from class: org.teleal.cling.mock.MockUpnpService.2
            @Override // org.teleal.cling.registry.RegistryImpl
            protected RegistryMaintainer createRegistryMaintainer() {
                if (z2) {
                    return super.createRegistryMaintainer();
                }
                return null;
            }
        };
        this.networkAddressFactory = this.configuration.createNetworkAddressFactory();
        this.router = createRouter();
        this.controlPoint = new ControlPointImpl(this.configuration, this.protocolFactory, this.registry);
    }

    protected ProtocolFactory createProtocolFactory(UpnpService upnpService, boolean z) {
        return new MockProtocolFactory(upnpService, z);
    }

    protected Router createRouter() {
        return new MockRouter();
    }

    public static class MockProtocolFactory extends ProtocolFactoryImpl {
        private boolean sendsAlive;

        public MockProtocolFactory(UpnpService upnpService, boolean z) {
            super(upnpService);
            this.sendsAlive = z;
        }

        @Override // org.teleal.cling.protocol.ProtocolFactoryImpl, org.teleal.cling.protocol.ProtocolFactory
        public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
            return new SendingNotificationAlive(getUpnpService(), localDevice) { // from class: org.teleal.cling.mock.MockUpnpService.MockProtocolFactory.1
                @Override // org.teleal.cling.protocol.async.SendingNotificationAlive, org.teleal.cling.protocol.async.SendingNotification, org.teleal.cling.protocol.SendingAsync
                protected void execute() {
                    if (MockProtocolFactory.this.sendsAlive) {
                        super.execute();
                    }
                }
            };
        }

        @Override // org.teleal.cling.protocol.ProtocolFactoryImpl, org.teleal.cling.protocol.ProtocolFactory
        public SendingSearch createSendingSearch(UpnpHeader upnpHeader, int i) {
            return new SendingSearch(getUpnpService(), upnpHeader, i) { // from class: org.teleal.cling.mock.MockUpnpService.MockProtocolFactory.2
                @Override // org.teleal.cling.protocol.async.SendingSearch
                public int getBulkIntervalMilliseconds() {
                    return 0;
                }
            };
        }
    }

    public class MockRouter implements Router {
        int counter = -1;

        public StreamClient getStreamClient() {
            return null;
        }

        @Override // org.teleal.cling.transport.Router
        public void shutdown() {
        }

        public MockRouter() {
        }

        @Override // org.teleal.cling.transport.Router
        public UpnpServiceConfiguration getConfiguration() {
            return MockUpnpService.this.configuration;
        }

        @Override // org.teleal.cling.transport.Router
        public ProtocolFactory getProtocolFactory() {
            return MockUpnpService.this.protocolFactory;
        }

        @Override // org.teleal.cling.transport.Router
        public NetworkAddressFactory getNetworkAddressFactory() {
            return MockUpnpService.this.networkAddressFactory;
        }

        @Override // org.teleal.cling.transport.Router
        public List<NetworkAddress> getActiveStreamServers(InetAddress inetAddress) {
            try {
                return Arrays.asList(new NetworkAddress(InetAddress.getByName("127.0.0.1"), 0));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // org.teleal.cling.transport.Router
        public void received(IncomingDatagramMessage incomingDatagramMessage) {
            MockUpnpService.this.incomingDatagramMessages.add(incomingDatagramMessage);
        }

        @Override // org.teleal.cling.transport.Router
        public void received(UpnpStream upnpStream) {
            MockUpnpService.this.receivedUpnpStreams.add(upnpStream);
        }

        @Override // org.teleal.cling.transport.Router
        public void send(OutgoingDatagramMessage outgoingDatagramMessage) {
            MockUpnpService.this.outgoingDatagramMessages.add(outgoingDatagramMessage);
        }

        @Override // org.teleal.cling.transport.Router
        public StreamResponseMessage send(StreamRequestMessage streamRequestMessage) {
            MockUpnpService.this.sentStreamRequestMessages.add(streamRequestMessage);
            this.counter++;
            return MockUpnpService.this.getStreamResponseMessages() != null ? MockUpnpService.this.getStreamResponseMessages()[this.counter] : MockUpnpService.this.getStreamResponseMessage(streamRequestMessage);
        }

        @Override // org.teleal.cling.transport.Router
        public void broadcast(byte[] bArr) {
            MockUpnpService.this.broadcastedBytes.add(bArr);
        }
    }

    @Override // org.teleal.cling.UpnpService
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.UpnpService
    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    @Override // org.teleal.cling.UpnpService
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.UpnpService
    public Registry getRegistry() {
        return this.registry;
    }

    @Override // org.teleal.cling.UpnpService
    public Router getRouter() {
        return this.router;
    }

    @Override // org.teleal.cling.UpnpService
    public void shutdown() {
        getRouter().shutdown();
        getRegistry().shutdown();
        getConfiguration().shutdown();
    }

    public List<IncomingDatagramMessage> getIncomingDatagramMessages() {
        return this.incomingDatagramMessages;
    }

    public List<OutgoingDatagramMessage> getOutgoingDatagramMessages() {
        return this.outgoingDatagramMessages;
    }

    public List<UpnpStream> getReceivedUpnpStreams() {
        return this.receivedUpnpStreams;
    }

    public List<StreamRequestMessage> getSentStreamRequestMessages() {
        return this.sentStreamRequestMessages;
    }

    public List<byte[]> getBroadcastedBytes() {
        return this.broadcastedBytes;
    }
}

