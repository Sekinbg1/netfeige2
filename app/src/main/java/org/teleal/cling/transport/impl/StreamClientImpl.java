package org.teleal.cling.transport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamClient;
// import org.teleal.common.http.Headers; // May not be available on Android
// import org.teleal.common.io.IO; // May not be available on Android
// import org.teleal.common.mock.http.MockHttpServletRequest; // May not be available on Android
// import sun.net.www.protocol.http.Handler; // Not available on Android

/* JADX INFO: loaded from: classes.dex */
public class StreamClientImpl implements StreamClient {
	static final String HACK_STREAM_HANDLER_SYSTEM_PROPERTY = "hackStreamHandlerProperty";
	private static final Logger log = Logger.getLogger(StreamClient.class.getName());
	protected final StreamClientConfigurationImpl configuration;

	@Override // org.teleal.cling.transport.spi.StreamClient
	public void stop() {
	}

	public StreamClientImpl(StreamClientConfigurationImpl streamClientConfigurationImpl) throws InitializationException {
		this.configuration = streamClientConfigurationImpl;
		log.warning("StreamClientImpl using sun.net APIs - limited support on Android");
		// Commented out for Android compatibility - sun.net not available
		/*
		System.setProperty("http.keepAlive", Boolean.toString(streamClientConfigurationImpl.isUsePersistentConnections()));
		if (System.getProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY) == null) {
			log.fine("Setting custom static URLStreamHandlerFactory to work around Sun JDK bugs");
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				@Override
				public URLStreamHandler createURLStreamHandler(String str) {
					return null; // Stub for Android
				}
			});
			System.setProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY, "alreadyWorkedAroundTheEvilJDK");
		}
		*/
	}

	@Override // org.teleal.cling.transport.spi.StreamClient
	public StreamClientConfigurationImpl getConfiguration() {
		return this.configuration;
	}

	@Override // org.teleal.cling.transport.spi.StreamClient
	public StreamResponseMessage sendRequest(StreamRequestMessage request) {
		// Stub implementation - original code uses sun.net APIs
		log.warning("sendRequest not fully supported on Android (sun.net unavailable)");
		return null;
	}

	// Commented out methods that use unavailable libraries
	/*
	protected void applyRequestProperties(HttpURLConnection httpURLConnection, StreamRequestMessage streamRequestMessage) {
		// ...
	}

	protected void applyHeaders(HttpURLConnection httpURLConnection, Headers headers) {
		// ...
	}

	protected void applyRequestBody(HttpURLConnection httpURLConnection, StreamRequestMessage streamRequestMessage) throws IOException {
		// ...
	}

	protected StreamResponseMessage createResponse(HttpURLConnection httpURLConnection, InputStream inputStream) throws Exception {
		// ...
	}
	*/

	// Commented out - extends sun.net.www.protocol.http.HttpURLConnection which is not available on Android
	/*
	static class UpnpURLConnection extends sun.net.www.protocol.http.HttpURLConnection {
		// ...
	}
	*/
}
