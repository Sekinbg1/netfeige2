package org.teleal.cling.support.shared;

import java.util.ArrayList;
import java.util.logging.Level;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.protocol.RetrieveRemoteDescriptors;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.protocol.sync.ReceivingEvent;
import org.teleal.cling.protocol.sync.ReceivingRetrieval;
import org.teleal.cling.protocol.sync.ReceivingSubscribe;
import org.teleal.cling.protocol.sync.ReceivingUnsubscribe;
import org.teleal.cling.protocol.sync.SendingAction;
import org.teleal.cling.protocol.sync.SendingEvent;
import org.teleal.cling.protocol.sync.SendingRenewal;
import org.teleal.cling.protocol.sync.SendingSubscribe;
import org.teleal.cling.protocol.sync.SendingUnsubscribe;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.DatagramIO;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.GENAEventProcessor;
import org.teleal.cling.transport.spi.MulticastReceiver;
import org.teleal.cling.transport.spi.SOAPActionProcessor;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;
import org.teleal.cling.transport.spi.UpnpStream;
import org.teleal.common.androidfwk.logging.LogCategory;

/* JADX INFO: loaded from: classes.dex */
public class LogCategories extends ArrayList<LogCategory> {
    public LogCategories() {
        super(10);
        add(new LogCategory("Network", new LogCategory.Group[]{new LogCategory.Group("UDP communication", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(DatagramIO.class.getName(), Level.FINE), new LogCategory.LoggerLevel(MulticastReceiver.class.getName(), Level.FINE)}), new LogCategory.Group("UDP datagram processing and content", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(DatagramProcessor.class.getName(), Level.FINER)}), new LogCategory.Group("TCP communication", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(UpnpStream.class.getName(), Level.FINER), new LogCategory.LoggerLevel(StreamServer.class.getName(), Level.FINE), new LogCategory.LoggerLevel(StreamClient.class.getName(), Level.FINE)}), new LogCategory.Group("SOAP action message processing and content", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(SOAPActionProcessor.class.getName(), Level.FINER)}), new LogCategory.Group("GENA event message processing and content", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(GENAEventProcessor.class.getName(), Level.FINER)}), new LogCategory.Group("HTTP header processing", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(UpnpHeaders.class.getName(), Level.FINER)})}));
        add(new LogCategory("UPnP Protocol", new LogCategory.Group[]{new LogCategory.Group("Discovery (Notification & Search)", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.FINER), new LogCategory.LoggerLevel("org.teleal.cling.protocol.async", Level.FINER)}), new LogCategory.Group("Description", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.FINER), new LogCategory.LoggerLevel(RetrieveRemoteDescriptors.class.getName(), Level.FINE), new LogCategory.LoggerLevel(ReceivingRetrieval.class.getName(), Level.FINE), new LogCategory.LoggerLevel(DeviceDescriptorBinder.class.getName(), Level.FINE), new LogCategory.LoggerLevel(ServiceDescriptorBinder.class.getName(), Level.FINE)}), new LogCategory.Group("Control", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.FINER), new LogCategory.LoggerLevel(ReceivingAction.class.getName(), Level.FINER), new LogCategory.LoggerLevel(SendingAction.class.getName(), Level.FINER)}), new LogCategory.Group("GENA ", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel("org.teleal.cling.model.gena", Level.FINER), new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.FINER), new LogCategory.LoggerLevel(ReceivingEvent.class.getName(), Level.FINER), new LogCategory.LoggerLevel(ReceivingSubscribe.class.getName(), Level.FINER), new LogCategory.LoggerLevel(ReceivingUnsubscribe.class.getName(), Level.FINER), new LogCategory.LoggerLevel(SendingEvent.class.getName(), Level.FINER), new LogCategory.LoggerLevel(SendingSubscribe.class.getName(), Level.FINER), new LogCategory.LoggerLevel(SendingUnsubscribe.class.getName(), Level.FINER), new LogCategory.LoggerLevel(SendingRenewal.class.getName(), Level.FINER)})}));
        add(new LogCategory("Core", new LogCategory.Group[]{new LogCategory.Group("Router", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(Router.class.getName(), Level.FINER)}), new LogCategory.Group("Registry", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel(Registry.class.getName(), Level.FINER)}), new LogCategory.Group("Local service binding & invocation", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel("org.teleal.cling.binding.annotations", Level.FINER), new LogCategory.LoggerLevel(LocalService.class.getName(), Level.FINER), new LogCategory.LoggerLevel("org.teleal.cling.model.action", Level.FINER), new LogCategory.LoggerLevel("org.teleal.cling.model.state", Level.FINER), new LogCategory.LoggerLevel(DefaultServiceManager.class.getName(), Level.FINER)}), new LogCategory.Group("Control Point interaction", new LogCategory.LoggerLevel[]{new LogCategory.LoggerLevel("org.teleal.cling.controlpoint", Level.FINER)})}));
    }
}

