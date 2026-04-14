package org.teleal.cling.binding.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.model.Constants;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.action.ActionExecutor;
import org.teleal.cling.model.action.MethodActionExecutor;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.GetterStateVariableAccessor;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.Datatype;
import org.teleal.common.util.Reflections;

/* JADX INFO: loaded from: classes.dex */
public class AnnotationActionBinder {
    private static Logger log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());
    protected UpnpAction annotation;
    protected Method method;
    protected Map<StateVariable, StateVariableAccessor> stateVariables;
    protected Set<Class> stringConvertibleTypes;

    public AnnotationActionBinder(Method method, Map<StateVariable, StateVariableAccessor> map, Set<Class> set) {
        this.annotation = (UpnpAction) method.getAnnotation(UpnpAction.class);
        this.stateVariables = map;
        this.method = method;
        this.stringConvertibleTypes = set;
    }

    public UpnpAction getAnnotation() {
        return this.annotation;
    }

    public Map<StateVariable, StateVariableAccessor> getStateVariables() {
        return this.stateVariables;
    }

    public Method getMethod() {
        return this.method;
    }

    public Set<Class> getStringConvertibleTypes() {
        return this.stringConvertibleTypes;
    }

    public void appendAction(Map<Action, ActionExecutor> map) throws LocalServiceBindingException {
        String upnpActionName;
        if (getAnnotation().name().length() != 0) {
            upnpActionName = getAnnotation().name();
        } else {
            upnpActionName = AnnotationLocalServiceBinder.toUpnpActionName(getMethod().getName());
        }
        log.fine("Creating action and executor: " + upnpActionName);
        List<ActionArgument> listCreateInputArguments = createInputArguments();
        Map<ActionArgument<LocalService>, StateVariableAccessor> mapCreateOutputArguments = createOutputArguments();
        listCreateInputArguments.addAll(mapCreateOutputArguments.keySet());
        map.put(new Action(upnpActionName, (ActionArgument[]) listCreateInputArguments.toArray(new ActionArgument[listCreateInputArguments.size()])), createExecutor(mapCreateOutputArguments));
    }

    protected ActionExecutor createExecutor(Map<ActionArgument<LocalService>, StateVariableAccessor> map) {
        return new MethodActionExecutor(map, getMethod());
    }

    protected List<ActionArgument> createInputArguments() throws LocalServiceBindingException {
        ArrayList arrayList = new ArrayList();
        Annotation[][] parameterAnnotations = getMethod().getParameterAnnotations();
        int i = 0;
        for (int i2 = 0; i2 < parameterAnnotations.length; i2++) {
            for (Annotation annotation : parameterAnnotations[i2]) {
                if (annotation instanceof UpnpInputArgument) {
                    UpnpInputArgument upnpInputArgument = (UpnpInputArgument) annotation;
                    i++;
                    String strName = upnpInputArgument.name();
                    StateVariable stateVariableFindRelatedStateVariable = findRelatedStateVariable(upnpInputArgument.stateVariable(), strName, getMethod().getName());
                    if (stateVariableFindRelatedStateVariable == null) {
                        throw new LocalServiceBindingException("Could not detected related state variable of argument: " + strName);
                    }
                    validateType(stateVariableFindRelatedStateVariable, getMethod().getParameterTypes()[i2]);
                    arrayList.add(new ActionArgument(strName, upnpInputArgument.aliases(), stateVariableFindRelatedStateVariable.getName(), ActionArgument.Direction.IN));
                }
            }
        }
        if (i >= getMethod().getParameterTypes().length) {
            return arrayList;
        }
        throw new LocalServiceBindingException("Method has parameters that are not input arguments: " + getMethod().getName());
    }

    protected Map<ActionArgument<LocalService>, StateVariableAccessor> createOutputArguments() throws LocalServiceBindingException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        UpnpAction upnpAction = (UpnpAction) getMethod().getAnnotation(UpnpAction.class);
        if (upnpAction.out().length == 0) {
            return linkedHashMap;
        }
        boolean z = upnpAction.out().length > 1;
        for (UpnpOutputArgument upnpOutputArgument : upnpAction.out()) {
            String strName = upnpOutputArgument.name();
            StateVariable stateVariableFindRelatedStateVariable = findRelatedStateVariable(upnpOutputArgument.stateVariable(), strName, getMethod().getName());
            if (stateVariableFindRelatedStateVariable == null && upnpOutputArgument.getterName().length() > 0) {
                stateVariableFindRelatedStateVariable = findRelatedStateVariable(null, null, upnpOutputArgument.getterName());
            }
            if (stateVariableFindRelatedStateVariable == null) {
                throw new LocalServiceBindingException("Related state variable not found for output argument: " + strName);
            }
            StateVariableAccessor stateVariableAccessorFindOutputArgumentAccessor = findOutputArgumentAccessor(stateVariableFindRelatedStateVariable, upnpOutputArgument.getterName(), z);
            log.finer("Found related state variable for output argument '" + strName + "': " + stateVariableFindRelatedStateVariable);
            linkedHashMap.put(new ActionArgument(strName, stateVariableFindRelatedStateVariable.getName(), ActionArgument.Direction.OUT, z ^ true), stateVariableAccessorFindOutputArgumentAccessor);
        }
        return linkedHashMap;
    }

    protected StateVariableAccessor findOutputArgumentAccessor(StateVariable stateVariable, String str, boolean z) throws LocalServiceBindingException {
        if (getMethod().getReturnType().equals(Void.TYPE)) {
            if (str != null && str.length() > 0) {
                log.finer("Action method is void, will use getter method named: " + str);
                Method method = Reflections.getMethod(getMethod().getDeclaringClass(), str);
                if (method == null) {
                    throw new LocalServiceBindingException("Declared getter method '" + str + "' not found on: " + getMethod().getDeclaringClass());
                }
                validateType(stateVariable, method.getReturnType());
                return new GetterStateVariableAccessor(method);
            }
            log.finer("Action method is void, trying to find existing accessor of related: " + stateVariable);
            return getStateVariables().get(stateVariable);
        }
        if (str == null || str.length() <= 0) {
            if (z) {
                return null;
            }
            log.finer("Action method is not void, will use the returned instance: " + getMethod().getReturnType());
            validateType(stateVariable, getMethod().getReturnType());
            return null;
        }
        log.finer("Action method is not void, will use getter method on returned instance: " + str);
        Method method2 = Reflections.getMethod(getMethod().getReturnType(), str);
        if (method2 == null) {
            throw new LocalServiceBindingException("Declared getter method '" + str + "' not found on return type: " + getMethod().getReturnType());
        }
        validateType(stateVariable, method2.getReturnType());
        return new GetterStateVariableAccessor(method2);
    }

    protected StateVariable findRelatedStateVariable(String str, String str2, String str3) throws LocalServiceBindingException {
        StateVariable stateVariable;
        String methodPropertyName;
        if (str == null || str.length() <= 0) {
            stateVariable = null;
        } else {
            String upnpStateVariableName = AnnotationLocalServiceBinder.toUpnpStateVariableName(str);
            log.finer("Finding related state variable with declared name: " + upnpStateVariableName);
            stateVariable = getStateVariable(upnpStateVariableName);
        }
        if (stateVariable == null && str2 != null && str2.length() > 0) {
            log.finer("Finding related state variable with argument name: " + str2);
            stateVariable = getStateVariable(str2);
        }
        if (stateVariable == null && str2 != null && str2.length() > 0) {
            String str4 = Constants.ARG_TYPE_PREFIX + str2;
            log.finer("Finding related state variable with prefixed argument name: " + str4);
            stateVariable = getStateVariable(str4);
        }
        if (stateVariable != null || str3 == null || str3.length() <= 0 || (methodPropertyName = Reflections.getMethodPropertyName(str3)) == null) {
            return stateVariable;
        }
        log.finer("Finding related state varible with method property name: " + methodPropertyName);
        return getStateVariable(AnnotationLocalServiceBinder.toUpnpStateVariableName(methodPropertyName));
    }

    protected void validateType(StateVariable stateVariable, Class cls) throws LocalServiceBindingException {
        Datatype.Default byJavaType = ModelUtil.isStringConvertibleType(getStringConvertibleTypes(), cls) ? Datatype.Default.STRING : Datatype.Default.getByJavaType(cls);
        log.finer("Expecting '" + stateVariable + "' to match default mapping: " + byJavaType);
        if (byJavaType != null && !stateVariable.getTypeDetails().getDatatype().isHandlingJavaType(byJavaType.getJavaType())) {
            throw new LocalServiceBindingException("State variable '" + stateVariable + "' datatype can't handle action argument's Java type (change one): " + byJavaType.getJavaType());
        }
        if (byJavaType == null && stateVariable.getTypeDetails().getDatatype().getBuiltin() != null) {
            throw new LocalServiceBindingException("State variable '" + stateVariable + "' should be custom datatype (action argument type is unknown Java type): " + cls.getSimpleName());
        }
        log.finer("State variable matches required argument datatype (or can't be validated because it is custom)");
    }

    protected StateVariable getStateVariable(String str) {
        for (StateVariable stateVariable : getStateVariables().keySet()) {
            if (stateVariable.getName().equals(str)) {
                return stateVariable;
            }
        }
        return null;
    }
}

