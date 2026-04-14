package org.teleal.cling.model.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.model.VariableValue;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ErrorCode;

/* JADX INFO: loaded from: classes.dex */
public class MethodActionExecutor extends AbstractActionExecutor {
	private static Logger log = Logger.getLogger(MethodActionExecutor.class.getName());
	protected Method method;

	public MethodActionExecutor(Method method) {
		this.method = method;
	}

	public MethodActionExecutor(Map<ActionArgument<LocalService>, StateVariableAccessor> map, Method method) {
		super(map);
		this.method = method;
	}

	public Method getMethod() {
		return this.method;
	}

	/* JADX WARN: Removed duplicated region for block: B:24:0x00cf  */
	/* JADX WARN: Removed duplicated region for block: B:26:0x00d5  */
	@Override // org.teleal.cling.model.action.AbstractActionExecutor
	/*
		Code decompiled incorrectly, please refer to instructions dump.
		To view partially-correct code enable 'Show inconsistent code' option in preferences
	*/
	protected void execute(org.teleal.cling.model.action.ActionInvocation<org.teleal.cling.model.meta.LocalService> r6, java.lang.Object r7) throws java.lang.Exception {
		/*
			Method dump skipped, instruction units count: 241
			To view this dump change 'Code comments level' option to 'DEBUG'
		*/
		throw new UnsupportedOperationException("Method not decompiled: org.teleal.cling.model.action.MethodActionExecutor.execute(org.teleal.cling.model.action.ActionInvocation, java.lang.Object):void");
	}

	protected boolean isUseOutputArgumentAccessors(ActionInvocation<LocalService> actionInvocation) {
		for (ActionArgument actionArgument : actionInvocation.getAction().getOutputArguments()) {
			if (getOutputArgumentAccessors().get(actionArgument) != null) {
				return true;
			}
		}
		return false;
	}

	protected Object[] createInputArgumentValues(ActionInvocation<LocalService> actionInvocation, Method method) throws ActionException {
		int i = 0;
		LocalService localService = (LocalService) actionInvocation.getAction().getService();
		Object[] objArr = new Object[actionInvocation.getAction().getInputArguments().length];
		int i2 = 0;
		for (ActionArgument actionArgument : actionInvocation.getAction().getInputArguments()) {
			Class<?> cls = method.getParameterTypes()[i2];
			VariableValue input = actionInvocation.getInput(actionArgument);
			if (cls.isPrimitive() && (input == null || input.toString().length() == 0)) {
				throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Primitive action method argument '" + actionArgument.getName() + "' requires input value, can't be null or empty string");
			}
			if (input == null) {
				i = i2 + 1;
				objArr[i2] = null;
			} else {
				String string = input.toString();
				if (string.length() > 0 && localService.isStringConvertibleType((Class) cls) && !cls.isEnum()) {
					try {
						Constructor<?> constructor = cls.getConstructor(String.class);
						log.finer("Creating new input argument value instance with String.class constructor of type: " + cls);
						int i3 = i2 + 1;
						objArr[i2] = constructor.newInstance(string);
						i2 = i3;
					} catch (Exception e) {
						e.printStackTrace(System.err);
						throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Can't convert input argment string to desired type of '" + actionArgument.getName() + "': " + e);
					}
				} else {
					i = i2 + 1;
					objArr[i2] = input.getValue();
				}
			}
			i2 = i;
		}
		return objArr;
	}
}
