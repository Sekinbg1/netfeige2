package org.teleal.cling;

import java.util.concurrent.Executor;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.transport.spi.DatagramIO;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.GENAEventProcessor;
import org.teleal.cling.transport.spi.MulticastReceiver;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.SOAPActionProcessor;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;

/* JADX INFO: loaded from: classes.dex */
public interface UpnpServiceConfiguration {
    DatagramIO createDatagramIO(NetworkAddressFactory networkAddressFactory);

    MulticastReceiver createMulticastReceiver(NetworkAddressFactory networkAddressFactory);

    NetworkAddressFactory createNetworkAddressFactory();

    StreamClient createStreamClient();

    StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory);

    Executor getAsyncProtocolExecutor();

    Executor getDatagramIOExecutor();

    DatagramProcessor getDatagramProcessor();

    DeviceDescriptorBinder getDeviceDescriptorBinderUDA10();

    ServiceType[] getExclusiveServiceTypes();

    GENAEventProcessor getGenaEventProcessor();

    Executor getMulticastReceiverExecutor();

    Namespace getNamespace();

    Executor getRegistryListenerExecutor();

    Executor getRegistryMaintainerExecutor();

    int getRegistryMaintenanceIntervalMillis();

    ServiceDescriptorBinder getServiceDescriptorBinderUDA10();

    SOAPActionProcessor getSoapActionProcessor();

    Executor getStreamServerExecutor();

    Executor getSyncProtocolExecutor();

    void shutdown();
}

