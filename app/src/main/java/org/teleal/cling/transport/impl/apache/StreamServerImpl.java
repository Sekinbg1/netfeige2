package org.teleal.cling.transport.impl.apache;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;
import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamServer;

/* JADX INFO: loaded from: classes.dex */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {
    private static final Logger log = Logger.getLogger(StreamServer.class.getName());
    protected final StreamServerConfigurationImpl configuration;
    protected Router router;
    protected ServerSocket serverSocket;
    protected HttpParams globalParams = new BasicHttpParams();
    private volatile boolean stopped = false;

    public StreamServerImpl(StreamServerConfigurationImpl streamServerConfigurationImpl) {
        this.configuration = streamServerConfigurationImpl;
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public StreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized void init(InetAddress inetAddress, Router router) throws InitializationException {
        try {
            this.router = router;
            this.serverSocket = new ServerSocket(this.configuration.getListenPort(), this.configuration.getTcpConnectionBacklog(), inetAddress);
            log.info("Created socket (for receiving TCP streams) on: " + this.serverSocket.getLocalSocketAddress());
            this.globalParams.setIntParameter("http.socket.timeout", this.configuration.getDataWaitTimeoutSeconds() * 1000).setIntParameter("http.socket.buffer-size", this.configuration.getBufferSizeKilobytes() * 1024).setBooleanParameter("http.connection.stalecheck", this.configuration.isStaleConnectionCheck()).setBooleanParameter("http.tcp.nodelay", this.configuration.isTcpNoDelay());
        } catch (Exception e) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + e.toString(), e);
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized int getPort() {
        return this.serverSocket.getLocalPort();
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized void stop() {
        this.stopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            log.fine("Exception closing streaming server socket: " + e);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        log.fine("Entering blocking receiving loop, listening for HTTP stream requests on: " + this.serverSocket.getLocalSocketAddress());
        while (!this.stopped) {
            try {
                Socket socketAccept = this.serverSocket.accept();
                DefaultHttpServerConnection defaultHttpServerConnection = new DefaultHttpServerConnection() { // from class: org.teleal.cling.transport.impl.apache.StreamServerImpl.1
                    @Override // org.apache.http.impl.AbstractHttpServerConnection
                    protected HttpRequestFactory createHttpRequestFactory() {
                        return new UpnpHttpRequestFactory();
                    }
                };
                log.fine("Incoming connection from: " + socketAccept.getInetAddress());
                defaultHttpServerConnection.bind(socketAccept, this.globalParams);
                this.router.received(new HttpServerConnectionUpnpStream(this.router.getProtocolFactory(), defaultHttpServerConnection, this.globalParams));
            } catch (InterruptedIOException e) {
                log.fine("I/O has been interrupted, stopping receiving loop, bytes transfered: " + e.bytesTransferred);
            } catch (SocketException e2) {
                if (!this.stopped) {
                    log.fine("Exception using server socket: " + e2.getMessage());
                }
            } catch (IOException e3) {
                log.fine("Exception initializing receiving loop: " + e3.getMessage());
            }
        }
        try {
            log.fine("Receiving loop stopped");
            if (this.serverSocket.isClosed()) {
                return;
            }
            log.fine("Closing streaming server socket");
            this.serverSocket.close();
        } catch (Exception e4) {
            log.info("Exception closing streaming server socket: " + e4.getMessage());
        }
    }
}

