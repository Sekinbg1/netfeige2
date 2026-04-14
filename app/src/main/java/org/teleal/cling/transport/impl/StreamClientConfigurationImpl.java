package org.teleal.cling.transport.impl;

import org.teleal.cling.model.ServerClientTokens;
import org.teleal.cling.transport.spi.StreamClientConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {
    private boolean usePersistentConnections = false;
    private int connectionTimeoutSeconds = 5;
    private int dataReadTimeoutSeconds = 5;

    public boolean isUsePersistentConnections() {
        return this.usePersistentConnections;
    }

    public void setUsePersistentConnections(boolean z) {
        this.usePersistentConnections = z;
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

    @Override // org.teleal.cling.transport.spi.StreamClientConfiguration
    public String getUserAgentValue(int i, int i2) {
        return new ServerClientTokens(i, i2).toString();
    }
}

