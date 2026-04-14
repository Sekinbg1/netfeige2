package org.teleal.cling.model.gena;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.ServiceManager;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public abstract class LocalGENASubscription extends GENASubscription<LocalService> implements PropertyChangeListener {
	private static Logger log = Logger.getLogger(LocalGENASubscription.class.getName());
	final List<URL> callbackURLs;
	final Map<String, Long> lastSentNumericValue;
	final Map<String, Long> lastSentTimestamp;

	public abstract void ended(CancelReason cancelReason);

	protected LocalGENASubscription(LocalService localService, List<URL> list) throws Exception {
		super(localService);
		this.lastSentTimestamp = new HashMap();
		this.lastSentNumericValue = new HashMap();
		this.callbackURLs = list;
	}

	/* JADX WARN: Type inference incomplete: some casts might be missing */
	public LocalGENASubscription(LocalService localService, Integer num, List<URL> list) throws Exception {
		super(localService);
		this.lastSentTimestamp = new HashMap();
		this.lastSentNumericValue = new HashMap();
		setSubscriptionDuration(num);
		log.fine("Reading initial state of local service at subscription time");
		long time = new Date().getTime();
		this.currentValues.clear();
		Collection<StateVariableValue> eventedStateVariableValues = getService().getManager().readEventedStateVariableValues();
		log.finer("Got evented state variable values: " + eventedStateVariableValues.size());
		for (StateVariableValue stateVariableValue : eventedStateVariableValues) {
			this.currentValues.put(stateVariableValue.getStateVariable().getName(), stateVariableValue);
			if (log.isLoggable(Level.FINEST)) {
				log.finer("Read state variable value '" + stateVariableValue.getStateVariable().getName() + "': " + stateVariableValue.toString());
			}
			this.lastSentTimestamp.put(stateVariableValue.getStateVariable().getName(), Long.valueOf(time));
			if (stateVariableValue.getStateVariable().isModeratedNumericType()) {
				this.lastSentNumericValue.put(stateVariableValue.getStateVariable().getName(), Long.valueOf(stateVariableValue.toString()));
			}
		}
		this.subscriptionId = "uuid:" + UUID.randomUUID();
		this.currentSequence = new UnsignedIntegerFourBytes(0L);
		this.callbackURLs = list;
	}

	public synchronized List<URL> getCallbackURLs() {
		return this.callbackURLs;
	}

	public synchronized void registerOnService() {
		getService().getManager().getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	public synchronized void establish() {
		established();
	}

	public synchronized void end(CancelReason cancelReason) {
		try {
			getService().getManager().getPropertyChangeSupport().removePropertyChangeListener(this);
		} catch (Exception e) {
			log.warning("Removal of local service property change listener failed: " + Exceptions.unwrap(e));
		}
		ended(cancelReason);
	}

	/* JADX WARN: Type inference incomplete: some casts might be missing */
	@Override // java.beans.PropertyChangeListener
	public synchronized void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if (propertyChangeEvent.getPropertyName().equals(ServiceManager.EVENTED_STATE_VARIABLES)) {
			log.fine("Eventing triggered, getting state for subscription: " + getSubscriptionId());
			long time = new Date().getTime();
			Collection<StateVariableValue> collection = (Collection) propertyChangeEvent.getNewValue();
			Set<String> setModerateStateVariables = moderateStateVariables(time, collection);
			this.currentValues.clear();
			for (StateVariableValue stateVariableValue : collection) {
				String name = stateVariableValue.getStateVariable().getName();
				if (!setModerateStateVariables.contains(name)) {
					log.fine("Adding state variable value to current values of event: " + stateVariableValue.getStateVariable() + " = " + stateVariableValue);
					this.currentValues.put(stateVariableValue.getStateVariable().getName(), stateVariableValue);
					this.lastSentTimestamp.put(name, Long.valueOf(time));
					if (stateVariableValue.getStateVariable().isModeratedNumericType()) {
						this.lastSentNumericValue.put(name, Long.valueOf(stateVariableValue.toString()));
					}
				}
			}
			if (this.currentValues.size() > 0) {
				log.fine("Propagating new state variable values to subscription: " + this);
				eventReceived();
			} else {
				log.fine("No state variable values for event (all moderated out?), not triggering event");
			}
		}
	}

	protected synchronized Set<String> moderateStateVariables(long j, Collection<StateVariableValue> collection) {
		HashSet hashSet;
		hashSet = new HashSet();
		for (StateVariableValue stateVariableValue : collection) {
			StateVariable stateVariable = stateVariableValue.getStateVariable();
			String name = stateVariableValue.getStateVariable().getName();
			if (stateVariable.getEventDetails().getEventMaximumRateMilliseconds() == 0 && stateVariable.getEventDetails().getEventMinimumDelta() == 0) {
				log.finer("Variable is not moderated: " + stateVariable);
			} else if (!this.lastSentTimestamp.containsKey(name)) {
				log.finer("Variable is moderated but was never sent before: " + stateVariable);
			} else if (stateVariable.getEventDetails().getEventMaximumRateMilliseconds() > 0 && j <= this.lastSentTimestamp.get(name).longValue() + ((long) stateVariable.getEventDetails().getEventMaximumRateMilliseconds())) {
				log.finer("Excluding state variable with maximum rate: " + stateVariable);
				hashSet.add(name);
			} else if (stateVariable.isModeratedNumericType() && this.lastSentNumericValue.get(name) != null) {
				long jLongValue = Long.valueOf(this.lastSentNumericValue.get(name).longValue()).longValue();
				long jLongValue2 = Long.valueOf(stateVariableValue.toString()).longValue();
				long eventMinimumDelta = stateVariable.getEventDetails().getEventMinimumDelta();
				if (jLongValue2 > jLongValue && jLongValue2 - jLongValue < eventMinimumDelta) {
					log.finer("Excluding state variable with minimum delta: " + stateVariable);
					hashSet.add(name);
				} else if (jLongValue2 < jLongValue && jLongValue - jLongValue2 < eventMinimumDelta) {
					log.finer("Excluding state variable with minimum delta: " + stateVariable);
					hashSet.add(name);
				}
			}
		}
		return hashSet;
	}

	public synchronized void incrementSequence() {
		this.currentSequence.increment(true);
	}

	public synchronized void setSubscriptionDuration(Integer num) {
		this.requestedDurationSeconds = num == null ? 1800 : num.intValue();
		setActualSubscriptionDurationSeconds(this.requestedDurationSeconds);
	}
}

