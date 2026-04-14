package org.teleal.cling.transport.impl.apache;

import org.teleal.cling.model.ServerClientTokens;
import org.teleal.cling.transport.spi.StreamClientConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {
    private int maxTotalConnections = 1024;
    private int connectionTimeoutSeconds = 5;
    private int dataReadTimeoutSeconds = 5;
    private String contentCharset = "UTF-8";

    public int getRequestRetryCount() {
        return -1;
    }

    public int getSocketBufferSize() {
        return -1;
    }

    public boolean getStaleCheckingEnabled() {
        return true;
    }

    public int getMaxTotalConnections() {
        return this.maxTotalConnections;
    }

    public void setMaxTotalConnections(int i) {
        this.maxTotalConnections = i;
    }

    public int getConnectionTimeoutSeconds() {
        return this.connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int i) {
        this.connectionTimeoutSeconds = i;
    }

    public int getDataReadTimeoutSeconds() {
        return this.dataReadTimeoutSeconds;
    }

    public void setDataReadTimeoutSeconds(int i) {
        this.dataReadTimeoutSeconds = i;
    }

    public String getContentCharset() {
        return this.contentCharset;
    }

    public void setContentCharset(String str) {
        this.contentCharset = str;
    }

    @Override // org.teleal.cling.transport.spi.StreamClientConfiguration
    public String getUserAgentValue(int i, int i2) {
        return new ServerClientTokens(i, i2).toString();
    }
}

