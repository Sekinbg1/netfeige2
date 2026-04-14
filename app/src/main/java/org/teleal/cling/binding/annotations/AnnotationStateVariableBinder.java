package org.teleal.cling.binding.annotations;

import java.util.Set;
import java.util.logging.Logger;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.meta.StateVariableAllowedValueRange;
import org.teleal.cling.model.meta.StateVariableEventDetails;
import org.teleal.cling.model.meta.StateVariableTypeDetails;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.Datatype;

/* JADX INFO: loaded from: classes.dex */
public class AnnotationStateVariableBinder {
    private static Logger log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());
    protected StateVariableAccessor accessor;
    protected UpnpStateVariable annotation;
    protected String name;
    protected Set<Class> stringConvertibleTypes;

    public AnnotationStateVariableBinder(UpnpStateVariable upnpStateVariable, String str, StateVariableAccessor stateVariableAccessor, Set<Class> set) {
        this.annotation = upnpStateVariable;
        this.name = str;
        this.accessor = stateVariableAccessor;
        this.stringConvertibleTypes = set;
    }

    public UpnpStateVariable getAnnotation() {
        return this.annotation;
    }

    public String getName() {
        return this.name;
    }

    public StateVariableAccessor getAccessor() {
        return this.accessor;
    }

    public Set<Class> getStringConvertibleTypes() {
        return this.stringConvertibleTypes;
    }

    protected StateVariable createStateVariable() throws LocalServiceBindingException {
        String[] allowedValues;
        int iEventMinimumDelta;
        int iEventMaximumRateMilliseconds;
        boolean z;
        log.fine("Creating state variable '" + getName() + "' with accessor: " + getAccessor());
        Datatype datatypeCreateDatatype = createDatatype();
        String strCreateDefaultValue = createDefaultValue(datatypeCreateDatatype);
        StateVariableAllowedValueRange allowedValueRange = null;
        int i = 0;
        if (Datatype.Builtin.STRING.equals(datatypeCreateDatatype.getBuiltin())) {
            if (getAnnotation().allowedValues().length > 0) {
                allowedValues = getAnnotation().allowedValues();
            } else if (getAnnotation().allowedValuesEnum() != Void.TYPE) {
                allowedValues = getAllowedValues(getAnnotation().allowedValuesEnum());
            } else if (getAccessor() != null && getAccessor().getReturnType().isEnum()) {
                allowedValues = getAllowedValues(getAccessor().getReturnType());
            } else {
                log.finer("Not restricting allowed values (of string typed state var): " + getName());
                allowedValues = null;
            }
            if (allowedValues != null && strCreateDefaultValue != null) {
                int length = allowedValues.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        z = false;
                        break;
                    }
                    if (allowedValues[i2].equals(strCreateDefaultValue)) {
                        z = true;
                        break;
                    }
                    i2++;
                }
                if (!z) {
                    throw new LocalServiceBindingException("Default value '" + strCreateDefaultValue + "' is not in allowed values of: " + getName());
                }
            }
        } else {
            allowedValues = null;
        }
        if ((Datatype.Builtin.isNumeric(datatypeCreateDatatype.getBuiltin()) && getAnnotation().allowedValueMinimum() > 0) || getAnnotation().allowedValueMaximum() > 0) {
            allowedValueRange = getAllowedValueRange();
            if (strCreateDefaultValue != null && allowedValueRange != null) {
                try {
                    if (!allowedValueRange.isInRange(Long.valueOf(strCreateDefaultValue).longValue())) {
                        throw new LocalServiceBindingException("Default value '" + strCreateDefaultValue + "' is not in allowed range of: " + getName());
                    }
                } catch (Exception unused) {
                    throw new LocalServiceBindingException("Default value '" + strCreateDefaultValue + "' is not numeric (for range checking) of: " + getName());
                }
            }
        }
        boolean zSendEvents = getAnnotation().sendEvents();
        if (zSendEvents && getAccessor() == null) {
            throw new LocalServiceBindingException("State variable sends events but has no accessor for field or getter: " + getName());
        }
        if (zSendEvents) {
            if (getAnnotation().eventMaximumRateMilliseconds() > 0) {
                log.finer("Moderating state variable events using maximum rate (milliseconds): " + getAnnotation().eventMaximumRateMilliseconds());
                iEventMaximumRateMilliseconds = getAnnotation().eventMaximumRateMilliseconds();
            } else {
                iEventMaximumRateMilliseconds = 0;
            }
            if (getAnnotation().eventMinimumDelta() <= 0 || !Datatype.Builtin.isNumeric(datatypeCreateDatatype.getBuiltin())) {
                i = iEventMaximumRateMilliseconds;
                iEventMinimumDelta = 0;
            } else {
                log.finer("Moderating state variable events using minimum delta: " + getAnnotation().eventMinimumDelta());
                int i3 = iEventMaximumRateMilliseconds;
                iEventMinimumDelta = getAnnotation().eventMinimumDelta();
                i = i3;
            }
        } else {
            iEventMinimumDelta = 0;
        }
        return new StateVariable(getName(), new StateVariableTypeDetails(datatypeCreateDatatype, strCreateDefaultValue, allowedValues, allowedValueRange), new StateVariableEventDetails(zSendEvents, i, iEventMinimumDelta));
    }

    protected Datatype createDatatype() throws LocalServiceBindingException {
        String strDatatype = getAnnotation().datatype();
        if (strDatatype.length() == 0 && getAccessor() != null) {
            Class<?> returnType = getAccessor().getReturnType();
            log.finer("Using accessor return type as state variable type: " + returnType);
            if (ModelUtil.isStringConvertibleType(getStringConvertibleTypes(), returnType)) {
                log.finer("Return type is string-convertible, using string datatype");
                return Datatype.Default.STRING.getBuiltinType().getDatatype();
            }
            Datatype.Default byJavaType = Datatype.Default.getByJavaType(returnType);
            if (byJavaType != null) {
                log.finer("Return type has default UPnP datatype: " + byJavaType);
                return byJavaType.getBuiltinType().getDatatype();
            }
        }
        if ((strDatatype == null || strDatatype.length() == 0) && (getAnnotation().allowedValues().length > 0 || getAnnotation().allowedValuesEnum() != Void.TYPE)) {
            log.finer("State variable has restricted allowed values, hence using 'string' datatype");
            strDatatype = "string";
        }
        if (strDatatype == null || strDatatype.length() == 0) {
            throw new LocalServiceBindingException("Could not detect datatype of state variable: " + getName());
        }
        log.finer("Trying to find built-in UPnP datatype for detected name: " + strDatatype);
        Datatype.Builtin byDescriptorName = Datatype.Builtin.getByDescriptorName(strDatatype);
        if (byDescriptorName != null) {
            log.finer("Found built-in UPnP datatype: " + byDescriptorName);
            return byDescriptorName.getDatatype();
        }
        throw new LocalServiceBindingException("No built-in UPnP datatype found, using CustomDataType (TODO: NOT IMPLEMENTED)");
    }

    protected String createDefaultValue(Datatype datatype) throws LocalServiceBindingException {
        if (getAnnotation().defaultValue().length() == 0) {
            return null;
        }
        try {
            datatype.valueOf(getAnnotation().defaultValue());
            log.finer("Found state variable default value: " + getAnnotation().defaultValue());
            return getAnnotation().defaultValue();
        } catch (Exception e) {
            throw new LocalServiceBindingException("Default value doesn't match datatype of state variable '" + getName() + "': " + e.getMessage());
        }
    }

    protected String[] getAllowedValues(Class cls) throws LocalServiceBindingException {
        if (!cls.isEnum()) {
            throw new LocalServiceBindingException("Allowed values type is not an Enum: " + cls);
        }
        log.finer("Restricting allowed values of state variable to Enum: " + getName());
        String[] strArr = new String[cls.getEnumConstants().length];
        for (int i = 0; i < cls.getEnumConstants().length; i++) {
            Object obj = cls.getEnumConstants()[i];
            if (obj.toString().length() > 32) {
                throw new LocalServiceBindingException("Allowed value string (that is, Enum constant name) is longer than 32 characters: " + obj.toString());
            }
            log.finer("Adding allowed value (converted to string): " + obj.toString());
            strArr[i] = obj.toString();
        }
        return strArr;
    }

    protected StateVariableAllowedValueRange getAllowedValueRange() throws LocalServiceBindingException {
        if (getAnnotation().allowedValueMaximum() < getAnnotation().allowedValueMinimum()) {
            throw new LocalServiceBindingException("Allowed value range maximum is smaller than minimum: " + getName());
        }
        return new StateVariableAllowedValueRange(getAnnotation().allowedValueMinimum(), getAnnotation().allowedValueMaximum(), getAnnotation().allowedValueStep());
    }
}

