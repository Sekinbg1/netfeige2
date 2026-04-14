package org.teleal.cling.support.model;

import java.util.Map;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;

/* JADX INFO: loaded from: classes.dex */
public class PortMapping {
	private String description;
	private boolean enabled;
	private UnsignedIntegerTwoBytes externalPort;
	private String internalClient;
	private UnsignedIntegerTwoBytes internalPort;
	private UnsignedIntegerFourBytes leaseDurationSeconds;
	private Protocol protocol;
	private String remoteHost;

	public enum Protocol {
		UDP,
		TCP
	}

	public PortMapping() {
	}

	public PortMapping(Map<String, ActionArgumentValue<Service>> map) {
		this(((Boolean) map.get("NewEnabled").getValue()).booleanValue(), (UnsignedIntegerFourBytes) map.get("NewLeaseDuration").getValue(), (String) map.get("NewRemoteHost").getValue(), (UnsignedIntegerTwoBytes) map.get("NewExternalPort").getValue(), (UnsignedIntegerTwoBytes) map.get("NewInternalPort").getValue(), (String) map.get("NewInternalClient").getValue(), Protocol.valueOf(map.get("NewProtocol").toString()), (String) map.get("NewPortMappingDescription").getValue());
	}

	/* JADX WARN: Illegal instructions before constructor call */
	public PortMapping(int i, String str, Protocol protocol) {
		this(true, new UnsignedIntegerFourBytes(0L), null, new UnsignedIntegerTwoBytes(i), new UnsignedIntegerTwoBytes(i), str, protocol, null);
	}

	/* JADX WARN: Illegal instructions before constructor call */
	public PortMapping(int i, String str, Protocol protocol, String str2) {
		this(true, new UnsignedIntegerFourBytes(0L), null, new UnsignedIntegerTwoBytes(i), new UnsignedIntegerTwoBytes(i), str, protocol, str2);
	}

	public PortMapping(String str, UnsignedIntegerTwoBytes unsignedIntegerTwoBytes, Protocol protocol) {
		this(true, new UnsignedIntegerFourBytes(0L), str, unsignedIntegerTwoBytes, null, null, protocol, null);
	}

	public PortMapping(boolean z, UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str, UnsignedIntegerTwoBytes unsignedIntegerTwoBytes, UnsignedIntegerTwoBytes unsignedIntegerTwoBytes2, String str2, Protocol protocol, String str3) {
		this.enabled = z;
		this.leaseDurationSeconds = unsignedIntegerFourBytes;
		this.remoteHost = str;
		this.externalPort = unsignedIntegerTwoBytes;
		this.internalPort = unsignedIntegerTwoBytes2;
		this.internalClient = str2;
		this.protocol = protocol;
		this.description = str3;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean z) {
		this.enabled = z;
	}

	public UnsignedIntegerFourBytes getLeaseDurationSeconds() {
		return this.leaseDurationSeconds;
	}

	public void setLeaseDurationSeconds(UnsignedIntegerFourBytes unsignedIntegerFourBytes) {
		this.leaseDurationSeconds = unsignedIntegerFourBytes;
	}

	public boolean hasRemoteHost() {
		String str = this.remoteHost;
		return str != null && str.length() > 0;
	}

	public String getRemoteHost() {
		String str = this.remoteHost;
		return str == null ? "-" : str;
	}

	public void setRemoteHost(String str) {
		if (str == null || str.equals("-") || str.length() == 0) {
			str = null;
		}
		this.remoteHost = str;
	}

	public UnsignedIntegerTwoBytes getExternalPort() {
		return this.externalPort;
	}

	public void setExternalPort(UnsignedIntegerTwoBytes unsignedIntegerTwoBytes) {
		this.externalPort = unsignedIntegerTwoBytes;
	}

	public UnsignedIntegerTwoBytes getInternalPort() {
		return this.internalPort;
	}

	public void setInternalPort(UnsignedIntegerTwoBytes unsignedIntegerTwoBytes) {
		this.internalPort = unsignedIntegerTwoBytes;
	}

	public String getInternalClient() {
		return this.internalClient;
	}

	public void setInternalClient(String str) {
		this.internalClient = str;
	}

	public Protocol getProtocol() {
		return this.protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public boolean hasDescription() {
		return this.description != null;
	}

	public String getDescription() {
		String str = this.description;
		return str == null ? "-" : str;
	}

	public void setDescription(String str) {
		if (str == null || str.equals("-") || str.length() == 0) {
			str = null;
		}
		this.description = str;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ") Protocol: " + getProtocol() + ", " + getExternalPort() + " => " + getInternalClient();
	}
}
