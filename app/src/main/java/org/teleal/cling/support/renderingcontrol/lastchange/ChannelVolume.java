package org.teleal.cling.support.renderingcontrol.lastchange;

import org.teleal.cling.support.model.Channel;

/* JADX INFO: loaded from: classes.dex */
public class ChannelVolume {
    protected Channel channel;
    protected Integer volume;

    public ChannelVolume(Channel channel, Integer num) {
        this.channel = channel;
        this.volume = num;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public Integer getVolume() {
        return this.volume;
    }

    public String toString() {
        return "Volume: " + getVolume() + " (" + getChannel() + ")";
    }
}

