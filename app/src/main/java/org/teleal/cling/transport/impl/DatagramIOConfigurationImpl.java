package org.teleal.cling.transport.impl;

import org.teleal.cling.transport.spi.DatagramIOConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class DatagramIOConfigurationImpl implements DatagramIOConfiguration {
    private int maxDatagramBytes;
    private int timeToLive;

    public DatagramIOConfigurationImpl() {
        this.timeToLive = 4;
        this.maxDatagramBytes = 640;
    }

    public DatagramIOConfigurationImpl(int i, int i2) {
        this.timeToLive = 4;
        this.maxDatagramBytes = 640;
        this.timeToLive = i;
        this.maxDatagramBytes = i2;
    }

    @Override // org.teleal.cling.transport.spi.DatagramIOConfiguration
    public int getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(int i) {
        this.timeToLive = i;
    }

    @Override // org.teleal.cling.transport.spi.DatagramIOConfiguration
    public int getMaxDatagramBytes() {
        return this.maxDatagramBytes;
    }

    public void setMaxDatagramBytes(int i) {
        this.maxDatagramBytes = i;
    }
}

