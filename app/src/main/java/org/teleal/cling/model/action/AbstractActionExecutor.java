package org.teleal.cling.model.action;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.Command;
import org.teleal.cling.model.ServiceManager;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractActionExecutor implements ActionExecutor {
    private static Logger log = Logger.getLogger(AbstractActionExecutor.class.getName());
    protected Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors;

    protected abstract void execute(ActionInvocation<LocalService> actionInvocation, Object obj) throws Exception;

    protected AbstractActionExecutor() {
        this.outputArgumentAccessors = new HashMap();
    }

    protected AbstractActionExecutor(Map<ActionArgument<LocalService>, StateVariableAccessor> map) {
        this.outputArgumentAccessors = new HashMap();
        this.outputArgumentAccessors = map;
    }

    public Map<ActionArgument<LocalService>, StateVariableAccessor> getOutputArgumentAccessors() {
        return this.outputArgumentAccessors;
    }

    @Override // org.teleal.cling.model.action.ActionExecutor
    public void execute(final ActionInvocation<LocalService> actionInvocation) {
        log.fine("Invoking on local service: " + actionInvocation);
        LocalService localService = (LocalService) actionInvocation.getAction().getService();
        try {
            if (localService.getManager() == null) {
                throw new IllegalStateException("Service has no implementation factory, can't get service instance");
            }
            localService.getManager().execute(new Command() { // from class: org.teleal.cling.model.action.AbstractActionExecutor.1
                @Override // org.teleal.cling.model.Command
                public void execute(ServiceManager serviceManager) throws Exception {
                    AbstractActionExecutor.this.execute(actionInvocation, serviceManager.getImplementation());
                }

                public String toString() {
                    return "Action invocation: " + actionInvocation.getAction();
                }
            });
        } catch (ActionException e) {
            log.fine("ActionException thrown by service method, wrapping in invocation and returning: " + e);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
            actionInvocation.setFailure(e);
        } catch (Exception e2) {
            log.fine("Exception thrown by execution, wrapping in ActionException and returning: " + e2);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e2));
            ErrorCode errorCode = ErrorCode.ACTION_FAILED;
            StringBuilder sb = new StringBuilder();
            sb.append("Action method invocation failed: ");
            sb.append(e2.getMessage() != null ? e2.getMessage() : e2.toString());
            actionInvocation.setFailure(new ActionException(errorCode, sb.toString(), e2));
        }
    }

    protected Object readOutputArgumentValues(Action<LocalService> action, Object obj) throws Exception {
        int length = action.getOutputArguments().length;
        Object[] objArr = new Object[length];
        log.fine("Attempting to retrieve output argument values using accessor: " + length);
        Object[] outputArguments = action.getOutputArguments();
        int length2 = outputArguments.length;
        int i = 0;
        int i2 = 0;
        while (i < length2) {
            Object obj2 = outputArguments[i];
            log.finer("Calling acccessor method for: " + obj2);
            StateVariableAccessor stateVariableAccessor = getOutputArgumentAccessors().get(obj2);
            if (stateVariableAccessor != null) {
                log.fine("Calling accessor to read output argument value: " + stateVariableAccessor);
                objArr[i2] = stateVariableAccessor.read(obj);
                i++;
                i2++;
            } else {
                throw new IllegalStateException("No accessor bound for: " + obj2);
            }
        }
        if (length == 1) {
            return objArr[0];
        }
        if (length > 0) {
            return objArr;
        }
        return null;
    }

    protected void setOutputArgumentValue(ActionInvocation<LocalService> actionInvocation, ActionArgument<LocalService> actionArgument, Object obj) throws ActionException {
        LocalService localService = (LocalService) actionInvocation.getAction().getService();
        if (obj != null) {
            try {
                if (localService.isStringConvertibleType(obj)) {
                    log.fine("Result of invocation matches convertible type, setting toString() single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<>(actionArgument, obj.toString()));
                } else {
                    log.fine("Result of invocation is Object, setting single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<>(actionArgument, obj));
                }
                return;
            } catch (InvalidValueException e) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Wrong type or invalid value for '" + actionArgument.getName() + "': " + e.getMessage(), e);
            }
        }
        log.fine("Result of invocation is null, not setting any output argument value(s)");
    }
}

