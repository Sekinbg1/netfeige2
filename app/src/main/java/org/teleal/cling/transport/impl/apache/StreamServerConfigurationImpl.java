package org.teleal.cling.transport.impl.apache;

import org.teleal.cling.transport.spi.StreamServerConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class StreamServerConfigurationImpl implements StreamServerConfiguration {
    private int bufferSizeKilobytes;
    private int dataWaitTimeoutSeconds;
    private int listenPort;
    private boolean staleConnectionCheck;
    private int tcpConnectionBacklog;
    private boolean tcpNoDelay;

    public StreamServerConfigurationImpl() {
        this.listenPort = 0;
        this.dataWaitTimeoutSeconds = 5;
        this.bufferSizeKilobytes = 8;
        this.staleConnectionCheck = true;
        this.tcpNoDelay = true;
        this.tcpConnectionBacklog = 0;
    }

    public StreamServerConfigurationImpl(int i) {
        this.listenPort = 0;
        this.dataWaitTimeoutSeconds = 5;
        this.bufferSizeKilobytes = 8;
        this.staleConnectionCheck = true;
        this.tcpNoDelay = true;
        this.tcpConnectionBacklog = 0;
        this.listenPort = i;
    }

    @Override // org.teleal.cling.transport.spi.StreamServerConfiguration
    public int getListenPort() {
        return this.listenPort;
    }

    public void setListenPort(int i) {
        this.listenPort = i;
    }

    public int getDataWaitTimeoutSeconds() {
        return this.dataWaitTimeoutSeconds;
    }

    public void setDataWaitTimeoutSeconds(int i) {
        this.dataWaitTimeoutSeconds = i;
    }

    public int getBufferSizeKilobytes() {
        return this.bufferSizeKilobytes;
    }

    public void setBufferSizeKilobytes(int i) {
        this.bufferSizeKilobytes = i;
    }

    public boolean isStaleConnectionCheck() {
        return this.staleConnectionCheck;
    }

    public void setStaleConnectionCheck(boolean z) {
        this.staleConnectionCheck = z;
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public void setTcpNoDelay(boolean z) {
        this.tcpNoDelay = z;
    }

    public int getTcpConnectionBacklog() {
        return this.tcpConnectionBacklog;
    }

    public void setTcpConnectionBacklog(int i) {
        this.tcpConnectionBacklog = i;
    }
}

