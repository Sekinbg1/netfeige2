package org.teleal.cling.model.action;

import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.QueryStateVariableAction;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ErrorCode;

/* JADX INFO: loaded from: classes.dex */
public class QueryStateVariableExecutor extends AbstractActionExecutor {
    @Override // org.teleal.cling.model.action.AbstractActionExecutor
    protected void execute(ActionInvocation<LocalService> actionInvocation, Object obj) throws Exception {
        if (actionInvocation.getAction() instanceof QueryStateVariableAction) {
            if (!((LocalService) actionInvocation.getAction().getService()).isSupportsQueryStateVariables()) {
                actionInvocation.setFailure(new ActionException(ErrorCode.INVALID_ACTION, "This service does not support querying state variables"));
                return;
            } else {
                executeQueryStateVariable(actionInvocation, obj);
                return;
            }
        }
        throw new IllegalStateException("This class can only execute QueryStateVariableAction's, not: " + actionInvocation.getAction());
    }

    protected void executeQueryStateVariable(ActionInvocation<LocalService> actionInvocation, Object obj) throws Exception {
        LocalService localService = (LocalService) actionInvocation.getAction().getService();
        String string = actionInvocation.getInput("varName").toString();
        StateVariable<LocalService> stateVariable = localService.getStateVariable(string);
        if (stateVariable == null) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "No state variable found: " + string);
        }
        StateVariableAccessor accessor = localService.getAccessor(stateVariable.getName());
        if (accessor == null) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "No accessor for state variable, can't read state: " + string);
        }
        try {
            setOutputArgumentValue(actionInvocation, actionInvocation.getAction().getOutputArgument("return"), accessor.read(stateVariable, obj).toString());
        } catch (Exception e) {
            throw new ActionException(ErrorCode.ACTION_FAILED, e.getMessage());
        }
    }
}

