package org.teleal.cling.transport.spi;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;

/* JADX INFO: loaded from: classes.dex */
public interface DatagramProcessor {
    IncomingDatagramMessage read(InetAddress inetAddress, DatagramPacket datagramPacket) throws UnsupportedDataException;

    DatagramPacket write(OutgoingDatagramMessage outgoingDatagramMessage) throws UnsupportedDataException;
}

