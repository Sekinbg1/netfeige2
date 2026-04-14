package org.teleal.cling.model.gena;

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.teleal.cling.model.Location;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* JADX INFO: loaded from: classes.dex */
public abstract class RemoteGENASubscription extends GENASubscription<RemoteService> {
	protected PropertyChangeSupport propertyChangeSupport;

	public abstract void ended(CancelReason cancelReason, UpnpResponse upnpResponse);

	public abstract void eventsMissed(int i);

	public abstract void failed(UpnpResponse upnpResponse);

	protected RemoteGENASubscription(RemoteService remoteService) {
		super(remoteService);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	protected RemoteGENASubscription(RemoteService remoteService, int i) {
		super(remoteService, i);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public synchronized URL getEventSubscriptionURL() {
		return getService().getDevice().normalizeURI(getService().getEventSubscriptionURI());
	}

	public synchronized List<URL> getEventCallbackURLs(List<NetworkAddress> list, Namespace namespace) {
		ArrayList arrayList;
		arrayList = new ArrayList();
		Iterator<NetworkAddress> it = list.iterator();
		while (it.hasNext()) {
			arrayList.add(new Location(it.next(), namespace.getEventCallbackPath(getService())).getURL());
		}
		return arrayList;
	}

	public synchronized void establish() {
		established();
	}

	public synchronized void fail(UpnpResponse upnpResponse) {
		failed(upnpResponse);
	}

	public synchronized void end(CancelReason cancelReason, UpnpResponse upnpResponse) {
		ended(cancelReason, upnpResponse);
	}

	/* JADX WARN: Type inference incomplete: some casts might be missing */
	public synchronized void receive(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Collection<StateVariableValue> collection) {
		if (this.currentSequence != null) {
			if (this.currentSequence.getValue().equals(Long.valueOf(this.currentSequence.getBits().getMaxValue())) && unsignedIntegerFourBytes.getValue().longValue() == 1) {
				System.err.println("TODO: HANDLE ROLLOVER");
				return;
			} else {
				if (this.currentSequence.getValue().longValue() >= unsignedIntegerFourBytes.getValue().longValue()) {
					return;
				}
				int iLongValue = (int) (unsignedIntegerFourBytes.getValue().longValue() - (this.currentSequence.getValue().longValue() + 1));
				if (iLongValue != 0) {
					eventsMissed(iLongValue);
				}
			}
		}
		this.currentSequence = unsignedIntegerFourBytes;
		for (StateVariableValue stateVariableValue : collection) {
			this.currentValues.put(stateVariableValue.getStateVariable().getName(), stateVariableValue);
		}
		eventReceived();
	}

	@Override // org.teleal.cling.model.gena.GENASubscription
	public String toString() {
		return "(SID: " + getSubscriptionId() + ") " + getService();
	}
}

