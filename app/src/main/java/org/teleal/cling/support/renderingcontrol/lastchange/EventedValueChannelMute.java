package org.teleal.cling.support.renderingcontrol.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.BooleanDatatype;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.support.lastchange.EventedValue;
import org.teleal.cling.support.model.Channel;
import org.teleal.cling.support.shared.AbstractMap;

/* JADX INFO: loaded from: classes.dex */
public class EventedValueChannelMute extends EventedValue<ChannelMute> {
    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return null;
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected /* bridge */ /* synthetic */ ChannelMute valueOf(Map.Entry[] entryArr) throws InvalidValueException {
        return valueOf2((Map.Entry<String, String>[]) entryArr);
    }

    public EventedValueChannelMute(ChannelMute channelMute) {
        super(channelMute);
    }

    public EventedValueChannelMute(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    /* JADX INFO: renamed from: valueOf, reason: avoid collision after fix types in other method */
    protected ChannelMute valueOf2(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
        Channel channelValueOf = null;
        Boolean boolValueOf = null;
        for (Map.Entry<String, String> entry : entryArr) {
            if (entry.getKey().equals(a.c)) {
                channelValueOf = Channel.valueOf(entry.getValue());
            }
            if (entry.getKey().equals("val")) {
                boolValueOf = new BooleanDatatype().valueOf(entry.getValue());
            }
        }
        if (channelValueOf == null || boolValueOf == null) {
            return null;
        }
        return new ChannelMute(channelValueOf, boolValueOf);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{new AbstractMap.SimpleEntry("val", new BooleanDatatype().getString(getValue().getMute())), new AbstractMap.SimpleEntry(a.c, getValue().getChannel().name())};
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public String toString() {
        return getValue().toString();
    }
}

