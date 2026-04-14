package org.teleal.cling.support.renderingcontrol.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytesDatatype;
import org.teleal.cling.support.lastchange.EventedValue;
import org.teleal.cling.support.model.Channel;
import org.teleal.cling.support.shared.AbstractMap;

/* JADX INFO: loaded from: classes.dex */
public class EventedValueChannelVolume extends EventedValue<ChannelVolume> {
	// Constants for XML attributes
	static class a {
		static final String c = "channel";
	}

	@Override // org.teleal.cling.support.lastchange.EventedValue
	protected Datatype getDatatype() {
		return null;
	}

	public EventedValueChannelVolume(ChannelVolume channelVolume) {
		super(channelVolume);
	}

	public EventedValueChannelVolume(Map.Entry<String, String>[] entryArr) {
		super(entryArr);
	}

	@Override // org.teleal.cling.support.lastchange.EventedValue
	protected ChannelVolume valueOf(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
		Channel channelValueOf = null;
		Integer numValueOf = null;
		for (Map.Entry<String, String> entry : entryArr) {
			if (entry.getKey().equals(a.c)) {
				channelValueOf = Channel.valueOf(entry.getValue());
			}
			if (entry.getKey().equals("val")) {
				numValueOf = Integer.valueOf(new UnsignedIntegerTwoBytesDatatype().valueOf(entry.getValue()).getValue().intValue());
			}
		}
		if (channelValueOf == null || numValueOf == null) {
			return null;
		}
		return new ChannelVolume(channelValueOf, numValueOf);
	}

	@Override // org.teleal.cling.support.lastchange.EventedValue
	public Map.Entry<String, String>[] getAttributes() {
		return new Map.Entry[]{new AbstractMap.SimpleEntry("val", new UnsignedIntegerTwoBytesDatatype().getString(new UnsignedIntegerTwoBytes(getValue().getVolume().intValue()))), new AbstractMap.SimpleEntry(a.c, getValue().getChannel().name())};
	}

	@Override // org.teleal.cling.support.lastchange.EventedValue
	public String toString() {
		return getValue().toString();
	}
}
