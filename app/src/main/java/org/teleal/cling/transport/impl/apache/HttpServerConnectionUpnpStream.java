package org.teleal.cling.transport.impl.apache;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpOperation;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.cling.transport.spi.UpnpStream;
import org.teleal.common.io.IO;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class HttpServerConnectionUpnpStream extends UpnpStream {
	private static final Logger log = Logger.getLogger(UpnpStream.class.getName());
	protected final HttpServerConnection connection;
	protected final BasicHttpProcessor httpProcessor;
	protected final HttpService httpService;
	protected final HttpParams params;

	protected HttpServerConnectionUpnpStream(ProtocolFactory protocolFactory, HttpServerConnection httpServerConnection, HttpParams httpParams) {
		super(protocolFactory);
		BasicHttpProcessor basicHttpProcessor = new BasicHttpProcessor();
		this.httpProcessor = basicHttpProcessor;
		this.connection = httpServerConnection;
		this.params = httpParams;
		basicHttpProcessor.addInterceptor(new ResponseDate());
		this.httpProcessor.addInterceptor(new ResponseContent());
		this.httpProcessor.addInterceptor(new ResponseConnControl());
		UpnpHttpService upnpHttpService = new UpnpHttpService(this.httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
		this.httpService = upnpHttpService;
		upnpHttpService.setParams(httpParams);
	}

	public HttpServerConnection getConnection() {
		return this.connection;
	}

	@Override // java.lang.Runnable
	public void run() {
		while (!Thread.interrupted() && this.connection.isOpen()) {
			try {
				try {
					try {
						log.fine("Handling request on open connection...");
						this.httpService.handleRequest(this.connection, new BasicHttpContext(null));
					} catch (SocketTimeoutException e4) {
						log.fine("Server-side closed socket (this is 'normal' behavior of Apache HTTP Core!): " + e4.getMessage());
						try {
							this.connection.shutdown();
							return;
						} catch (IOException e5) {
							// logger assignment removed
							StringBuilder sb = new StringBuilder();
							sb.append("Error closing connection: ");
							sb.append(e5.getMessage());
							log.fine(sb.toString());
						}
					} catch (ConnectionClosedException e6) {
						log.fine("Client closed connection");
						responseException(e6);
						try {
							this.connection.shutdown();
							return;
						} catch (IOException e7) {
							// logger assignment removed
							StringBuilder sb = new StringBuilder();
							sb.append("Error closing connection: ");
							sb.append(e7.getMessage());
							log.fine(sb.toString());
						}
					} catch (IOException e1) {
						log.warning("I/O exception during HTTP request processing: " + e1.getMessage());
						responseException(e1);
						try {
							this.connection.shutdown();
							return;
						} catch (IOException e2) {
							// logger assignment removed
							StringBuilder sb = new StringBuilder();
							sb.append("Error closing connection: ");
							sb.append(e2.getMessage());
							log.fine(sb.toString());
						}
					} catch (HttpException e3) {
						throw new UnsupportedDataException("Request malformed: " + e3.getMessage(), e3);
					}
				} catch (Exception e) {
					log.warning("Exception during request handling: " + e.getMessage());
				}
			} catch (Throwable th) {
				try {
					this.connection.shutdown();
				} catch (IOException e8) {
					log.fine("Error closing connection: " + e8.getMessage());
				}
				throw th;
			}
		}
		try {
			this.connection.shutdown();
		} catch (IOException e9) {
			// logger assignment removed
			StringBuilder sb = new StringBuilder();
			sb.append("Error closing connection: ");
			sb.append(e9.getMessage());
			log.fine(sb.toString());
		}
	}

	protected class UpnpHttpService extends HttpService {
		public UpnpHttpService(HttpProcessor httpProcessor, ConnectionReuseStrategy connectionReuseStrategy, HttpResponseFactory httpResponseFactory) {
			super(httpProcessor, connectionReuseStrategy, httpResponseFactory);
		}

		/* JADX WARN: Type inference fix 'apply assigned field type' failed
		java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
			at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
			at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
			at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
			at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
			at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
			at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
			at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
		 */
		@Override // org.apache.http.protocol.HttpService
		protected void doService(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
			HttpServerConnectionUpnpStream.log.fine("Processing HTTP request: " + httpRequest.getRequestLine().toString());
			String method = httpRequest.getRequestLine().getMethod();
			String uri = httpRequest.getRequestLine().getUri();
			try {
				StreamRequestMessage streamRequestMessage = new StreamRequestMessage(UpnpRequest.Method.getByHttpName(method), URI.create(uri));
				if (!((UpnpRequest) streamRequestMessage.getOperation()).getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
					HttpServerConnectionUpnpStream.log.fine("Created new request message: " + streamRequestMessage);
					((UpnpRequest) streamRequestMessage.getOperation()).setHttpMinorVersion(httpRequest.getProtocolVersion().getMinor());
					streamRequestMessage.setHeaders(new UpnpHeaders(HeaderUtil.get(httpRequest)));
					if (httpRequest instanceof HttpEntityEnclosingRequest) {
						HttpServerConnectionUpnpStream.log.fine("Request contains entity body, setting on UPnP message");
						InputStream content = null;
						try {
							content = ((HttpEntityEnclosingRequest) httpRequest).getEntity().getContent();
							byte[] bytes = IO.readBytes(content);
							if (bytes.length > 0 && streamRequestMessage.isContentTypeMissingOrText()) {
								HttpServerConnectionUpnpStream.log.fine("Request contains textual entity body, converting then setting string on message");
								streamRequestMessage.setBodyCharacters(bytes);
							} else if (bytes.length > 0) {
								HttpServerConnectionUpnpStream.log.fine("Request contains binary entity body, setting bytes on message");
								streamRequestMessage.setBody(UpnpMessage.BodyType.BYTES, bytes);
							} else {
								HttpServerConnectionUpnpStream.log.fine("Request did not contain entity body");
							}
						} finally {
							if (content != null) {
								content.close();
							}
						}
					} else {
						HttpServerConnectionUpnpStream.log.fine("Request did not contain entity body");
					}
					try {
						StreamResponseMessage streamResponseMessageProcess = HttpServerConnectionUpnpStream.this.process(streamRequestMessage);
						if (streamResponseMessageProcess != null) {
							HttpServerConnectionUpnpStream.log.fine("Sending HTTP response message: " + streamResponseMessageProcess);
							httpResponse.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, streamResponseMessageProcess.getOperation().getHttpMinorVersion()), streamResponseMessageProcess.getOperation().getStatusCode(), streamResponseMessageProcess.getOperation().getStatusMessage()));
							HttpServerConnectionUpnpStream.log.fine("Response status line: " + httpResponse.getStatusLine());
							httpResponse.setParams(getResponseParams(streamRequestMessage.getOperation()));
							HeaderUtil.add(httpResponse, streamResponseMessageProcess.getHeaders());
							if (streamResponseMessageProcess.hasBody() && streamResponseMessageProcess.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
								httpResponse.setEntity(new ByteArrayEntity(streamResponseMessageProcess.getBodyBytes()));
							} else if (streamResponseMessageProcess.hasBody() && streamResponseMessageProcess.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
								httpResponse.setEntity(new StringEntity(streamResponseMessageProcess.getBodyString(), "UTF-8"));
							}
						} else {
							HttpServerConnectionUpnpStream.log.fine("Sending HTTP response: 404");
							httpResponse.setStatusCode(404);
						}
						HttpServerConnectionUpnpStream.this.responseSent(streamResponseMessageProcess);
						return;
					} catch (RuntimeException e) {
						HttpServerConnectionUpnpStream.log.fine("Exception occured during UPnP stream processing: " + e);
						if (HttpServerConnectionUpnpStream.log.isLoggable(Level.FINE)) {
							HttpServerConnectionUpnpStream.log.log(Level.FINE, "Cause: " + Exceptions.unwrap(e), Exceptions.unwrap(e));
						}
						HttpServerConnectionUpnpStream.log.fine("Sending HTTP response: 500");
						httpResponse.setStatusCode(500);
						HttpServerConnectionUpnpStream.this.responseException(e);
						return;
					}
				}
				HttpServerConnectionUpnpStream.log.fine("Method not supported by UPnP stack: " + method);
				throw new MethodNotSupportedException("Method not supported: " + method);
			} catch (IllegalArgumentException e2) {
				String str = "Invalid request URI: " + uri + ": " + e2.getMessage();
				HttpServerConnectionUpnpStream.log.warning(str);
				throw new HttpException(str, e2);
			}
		}

		protected HttpParams getResponseParams(UpnpOperation upnpOperation) {
			return new DefaultedHttpParams(new BasicHttpParams(), HttpServerConnectionUpnpStream.this.params);
		}
	}
}
