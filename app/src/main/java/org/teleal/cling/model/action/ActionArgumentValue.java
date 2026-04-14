package org.teleal.cling.model.action;

import org.teleal.cling.model.VariableValue;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class ActionArgumentValue<S extends Service> extends VariableValue {
	private final ActionArgument<S> argument;

	/* JADX WARN: Illegal instructions before constructor call */
	public ActionArgumentValue(ActionArgument<S> actionArgument, Object obj) throws InvalidValueException {
		super(actionArgument.getDatatype(), (obj != null && obj.getClass().isEnum()) ? obj.toString() : obj);
		this.argument = actionArgument;
	}

	public ActionArgument<S> getArgument() {
		return this.argument;
	}
}

