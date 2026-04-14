package org.teleal.cling.transport.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpOperation;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.common.http.Headers;

/* JADX INFO: loaded from: classes.dex */
public class DatagramProcessorImpl implements DatagramProcessor {
	private static Logger log = Logger.getLogger(DatagramProcessor.class.getName());

	@Override // org.teleal.cling.transport.spi.DatagramProcessor
	public IncomingDatagramMessage read(InetAddress inetAddress, DatagramPacket datagramPacket) throws UnsupportedDataException {
		try {
			if (log.isLoggable(Level.FINER)) {
				log.finer("===================================== DATAGRAM BEGIN ============================================");
				log.finer(new String(datagramPacket.getData()));
				log.finer("-===================================== DATAGRAM END =============================================");
			}
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(datagramPacket.getData());
			String[] strArrSplit = Headers.readLine(byteArrayInputStream).split(" ");
			if (strArrSplit[0].startsWith("HTTP/1.")) {
				return readResponseMessage(inetAddress, datagramPacket, byteArrayInputStream, Integer.valueOf(strArrSplit[1]).intValue(), strArrSplit[2], strArrSplit[0]);
			}
			return readRequestMessage(inetAddress, datagramPacket, byteArrayInputStream, strArrSplit[0], strArrSplit[2]);
		} catch (Exception e) {
			throw new UnsupportedDataException("Could not parse headers: " + e, e);
		}
	}

	/* JADX WARN: Type inference failed for: r1v0, types: [org.teleal.cling.model.message.UpnpOperation] */
	@Override // org.teleal.cling.transport.spi.DatagramProcessor
	public DatagramPacket write(OutgoingDatagramMessage outgoingDatagramMessage) throws UnsupportedDataException {
		StringBuilder sb = new StringBuilder();
		UpnpOperation operation = outgoingDatagramMessage.getOperation();
		if (operation instanceof UpnpRequest) {
			sb.append(((UpnpRequest) operation).getHttpMethodName());
			sb.append(" * ");
			sb.append("HTTP/1.");
			sb.append(operation.getHttpMinorVersion());
			sb.append("\r\n");
		} else if (operation instanceof UpnpResponse) {
			UpnpResponse upnpResponse = (UpnpResponse) operation;
			sb.append("HTTP/1.");
			sb.append(operation.getHttpMinorVersion());
			sb.append(" ");
			sb.append(upnpResponse.getStatusCode());
			sb.append(" ");
			sb.append(upnpResponse.getStatusMessage());
			sb.append("\r\n");
		} else {
			throw new UnsupportedDataException("Message operation is not request or response, don't know how to process: " + outgoingDatagramMessage);
		}
		StringBuilder sb2 = new StringBuilder();
		sb2.append((CharSequence) sb);
		sb2.append(outgoingDatagramMessage.getHeaders().toString());
		sb2.append("\r\n");
		if (log.isLoggable(Level.FINER)) {
			log.finer("Writing message data for: " + outgoingDatagramMessage);
			log.finer("---------------------------------------------------------------------------------");
			log.finer(sb2.toString().substring(0, sb2.length() + (-2)));
			log.finer("---------------------------------------------------------------------------------");
		}
		try {
			byte[] bytes = sb2.toString().getBytes("US-ASCII");
			log.fine("Writing new datagram packet with " + bytes.length + " bytes for: " + outgoingDatagramMessage);
			return new DatagramPacket(bytes, bytes.length, outgoingDatagramMessage.getDestinationAddress(), outgoingDatagramMessage.getDestinationPort());
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedDataException("Can't convert message content to US-ASCII: " + e.getMessage(), e);
		}
	}

	protected IncomingDatagramMessage readRequestMessage(InetAddress inetAddress, DatagramPacket datagramPacket, ByteArrayInputStream byteArrayInputStream, String str, String str2) throws Exception {
		UpnpHeaders upnpHeaders = new UpnpHeaders(byteArrayInputStream);
		UpnpRequest upnpRequest = new UpnpRequest(UpnpRequest.Method.getByHttpName(str));
		upnpRequest.setHttpMinorVersion(str2.toUpperCase().equals("HTTP/1.1") ? 1 : 0);
		IncomingDatagramMessage incomingDatagramMessage = new IncomingDatagramMessage(upnpRequest, datagramPacket.getAddress(), datagramPacket.getPort(), inetAddress);
		incomingDatagramMessage.setHeaders(upnpHeaders);
		return incomingDatagramMessage;
	}

	protected IncomingDatagramMessage readResponseMessage(InetAddress inetAddress, DatagramPacket datagramPacket, ByteArrayInputStream byteArrayInputStream, int i, String str, String str2) throws Exception {
		UpnpHeaders upnpHeaders = new UpnpHeaders(byteArrayInputStream);
		UpnpResponse upnpResponse = new UpnpResponse(i, str);
		upnpResponse.setHttpMinorVersion(str2.toUpperCase().equals("HTTP/1.1") ? 1 : 0);
		IncomingDatagramMessage incomingDatagramMessage = new IncomingDatagramMessage(upnpResponse, datagramPacket.getAddress(), datagramPacket.getPort(), inetAddress);
		incomingDatagramMessage.setHeaders(upnpHeaders);
		return incomingDatagramMessage;
	}
}

