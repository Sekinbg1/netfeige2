package org.teleal.cling.model.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;

/* JADX INFO: loaded from: classes.dex */
public abstract class Service<D extends Device, S extends Service> {
	private final Map<String, Action> actions;
	private D device;
	private final ServiceId serviceId;
	private final ServiceType serviceType;
	private final Map<String, StateVariable> stateVariables;

	public abstract Action getQueryStateVariableAction();

	public Service(ServiceType serviceType, ServiceId serviceId) throws ValidationException {
		this(serviceType, serviceId, null, null);
	}

	public Service(ServiceType serviceType, ServiceId serviceId, Action<S>[] actionArr, StateVariable<S>[] stateVariableArr) throws ValidationException {
		this.actions = new HashMap();
		this.stateVariables = new HashMap();
		this.serviceType = serviceType;
		this.serviceId = serviceId;
		if (actionArr != null) {
			for (Action<S> action : actionArr) {
				this.actions.put(action.getName(), action);
				action.setService((S) this);
			}
		}
		if (stateVariableArr != null) {
			for (StateVariable<S> stateVariable : stateVariableArr) {
				this.stateVariables.put(stateVariable.getName(), stateVariable);
				stateVariable.setService((S) this);
			}
		}
	}

	public ServiceType getServiceType() {
		return this.serviceType;
	}

	public ServiceId getServiceId() {
		return this.serviceId;
	}

	public boolean hasActions() {
		return getActions() != null && getActions().length > 0;
	}

	public Action<S>[] getActions() {
		Map<String, Action> map = this.actions;
		if (map == null) {
			return null;
		}
		return (Action[]) map.values().toArray(new Action[this.actions.values().size()]);
	}

	public boolean hasStateVariables() {
		return getStateVariables() != null && getStateVariables().length > 0;
	}

	public StateVariable<S>[] getStateVariables() {
		Map<String, StateVariable> map = this.stateVariables;
		if (map == null) {
			return null;
		}
		return (StateVariable[]) map.values().toArray(new StateVariable[this.stateVariables.values().size()]);
	}

	public D getDevice() {
		return this.device;
	}

	void setDevice(D d) {
		if (this.device != null) {
			throw new IllegalStateException("Final value has been set already, model is immutable");
		}
		this.device = d;
	}

	public Action<S> getAction(String str) {
		Map<String, Action> map = this.actions;
		if (map == null) {
			return null;
		}
		return map.get(str);
	}

	public StateVariable<S> getStateVariable(String str) {
		if (QueryStateVariableAction.VIRTUAL_STATEVARIABLE_INPUT.equals(str)) {
			return new StateVariable<>(QueryStateVariableAction.VIRTUAL_STATEVARIABLE_INPUT, new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()));
		}
		if (QueryStateVariableAction.VIRTUAL_STATEVARIABLE_OUTPUT.equals(str)) {
			return new StateVariable<>(QueryStateVariableAction.VIRTUAL_STATEVARIABLE_OUTPUT, new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()));
		}
		Map<String, StateVariable> map = this.stateVariables;
		if (map == null) {
			return null;
		}
		return map.get(str);
	}

	public StateVariable<S> getRelatedStateVariable(ActionArgument actionArgument) {
		return getStateVariable(actionArgument.getRelatedStateVariableName());
	}

	public Datatype<S> getDatatype(ActionArgument actionArgument) {
		return getRelatedStateVariable(actionArgument).getTypeDetails().getDatatype();
	}

	public ServiceReference getReference() {
		return new ServiceReference(getDevice().getIdentity().getUdn(), getServiceId());
	}

	public List<ValidationError> validate() {
		ArrayList arrayList = new ArrayList();
		if (getServiceType() == null) {
			arrayList.add(new ValidationError(getClass(), "serviceType", "Service type/info is required"));
		}
		if (getServiceId() == null) {
			arrayList.add(new ValidationError(getClass(), "serviceId", "Service ID is required"));
		}
		if (hasActions()) {
			for (Action<S> action : getActions()) {
				arrayList.addAll(action.validate());
			}
		}
		if (hasStateVariables()) {
			for (StateVariable<S> stateVariable : getStateVariables()) {
				arrayList.addAll(stateVariable.validate());
			}
		}
		return arrayList;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ") ServiceId: " + getServiceId();
	}
}

