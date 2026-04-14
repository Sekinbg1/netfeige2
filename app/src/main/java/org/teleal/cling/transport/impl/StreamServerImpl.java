// WARNING: This class uses com.sun.net.httpserver which is NOT available on Android
// This file should be excluded from Android builds
package org.teleal.cling.transport.impl;

// import com.sun.net.httpserver.HttpExchange; // Not available on Android
// import com.sun.net.httpserver.HttpHandler; // Not available on Android
// import com.sun.net.httpserver.HttpServer; // Not available on Android
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamServer;

/* JADX INFO: loaded from: classes.dex */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {
	private static Logger log = Logger.getLogger(StreamServer.class.getName());
	protected final StreamServerConfigurationImpl configuration;
	// protected HttpServer server; // Not available on Android
	protected Object server; // Stub for Android

	public StreamServerImpl(StreamServerConfigurationImpl streamServerConfigurationImpl) {
		this.configuration = streamServerConfigurationImpl;
	}

	@Override // org.teleal.cling.transport.spi.StreamServer
	public synchronized void init(InetAddress inetAddress, Router router) throws InitializationException {
		// Stub implementation - HttpServer not available on Android
		log.warning("StreamServerImpl is not fully supported on Android (com.sun.net.httpserver unavailable)");
	}

	@Override // org.teleal.cling.transport.spi.StreamServer
	public synchronized int getPort() {
		return 0; // Stub
	}

	@Override // org.teleal.cling.transport.spi.StreamServer
	public StreamServerConfigurationImpl getConfiguration() {
		return this.configuration;
	}

	@Override // java.lang.Runnable
	public synchronized void run() {
		log.fine("Starting StreamServer... (stub for Android)");
		// this.server.start(); // Not available
	}

	@Override // org.teleal.cling.transport.spi.StreamServer
	public synchronized void stop() {
		log.fine("Stopping StreamServer...");
		//if (this.server != null) {
		//	this.server.stop(1); // Not available
		//}
	}

	// static class RequestHttpHandler implements HttpHandler { // Not available on Android
	// 	// Commented out for Android compatibility
	// }
}

