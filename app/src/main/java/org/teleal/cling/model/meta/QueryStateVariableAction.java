package org.teleal.cling.model.meta;

import java.util.Collections;
import java.util.List;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.Service;

/* JADX INFO: loaded from: classes.dex */
public class QueryStateVariableAction<S extends Service> extends Action<S> {
    public static final String ACTION_NAME = "QueryStateVariable";
    public static final String VIRTUAL_STATEVARIABLE_INPUT = "VirtualQueryActionInput";
    public static final String VIRTUAL_STATEVARIABLE_OUTPUT = "VirtualQueryActionOutput";

    @Override // org.teleal.cling.model.meta.Action
    public String getName() {
        return ACTION_NAME;
    }

    public QueryStateVariableAction() {
        this(null);
    }

    public QueryStateVariableAction(S s) {
        super(ACTION_NAME, new ActionArgument[]{new ActionArgument("varName", VIRTUAL_STATEVARIABLE_INPUT, ActionArgument.Direction.IN), new ActionArgument("return", VIRTUAL_STATEVARIABLE_OUTPUT, ActionArgument.Direction.OUT)});
        setService(s);
    }

    @Override // org.teleal.cling.model.meta.Action, org.teleal.cling.model.Validatable
    public List<ValidationError> validate() {
        return Collections.EMPTY_LIST;
    }
}

