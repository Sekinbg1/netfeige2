// WARNING: This class uses com.sun.net.httpserver which is NOT available on Android
// This file should be excluded from Android builds or replaced with Android-compatible implementation
package org.teleal.cling.transport.impl;

// import com.sun.net.httpserver.HttpExchange; // Not available on Android
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.spi.UpnpStream;
// import org.teleal.common.io.IO; // May not be available
// import org.teleal.common.util.Exceptions; // May not be available

/* JADX INFO: loaded from: classes.dex */
public class HttpExchangeUpnpStream extends UpnpStream {
	private static Logger log = Logger.getLogger(UpnpStream.class.getName());
	// private HttpExchange httpExchange; // Not available on Android

	public HttpExchangeUpnpStream(ProtocolFactory protocolFactory, Object httpExchange) {
		super(protocolFactory);
		// this.httpExchange = httpExchange; // Commented out for Android
	}

	public Object getHttpExchange() {
		return null; // Stub for Android
	}

	@Override // java.lang.Runnable
	public void run() {
		// Stub implementation for Android - original code uses com.sun.net.httpserver
		log.warning("HttpExchangeUpnpStream is not supported on Android");
	}
}

