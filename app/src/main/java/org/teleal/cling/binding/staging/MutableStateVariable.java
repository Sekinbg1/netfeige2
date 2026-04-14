package org.teleal.cling.binding.staging;

import java.util.List;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.meta.StateVariableAllowedValueRange;
import org.teleal.cling.model.meta.StateVariableEventDetails;
import org.teleal.cling.model.meta.StateVariableTypeDetails;
import org.teleal.cling.model.types.Datatype;

/* JADX INFO: loaded from: classes.dex */
public class MutableStateVariable {
    public MutableAllowedValueRange allowedValueRange;
    public List<String> allowedValues;
    public Datatype dataType;
    public String defaultValue;
    public StateVariableEventDetails eventDetails;
    public String name;

    public StateVariable build() {
        String[] strArr;
        String str = this.name;
        Datatype datatype = this.dataType;
        String str2 = this.defaultValue;
        List<String> list = this.allowedValues;
        if (list == null || list.size() == 0) {
            strArr = null;
        } else {
            List<String> list2 = this.allowedValues;
            strArr = (String[]) list2.toArray(new String[list2.size()]);
        }
        return new StateVariable(str, new StateVariableTypeDetails(datatype, str2, strArr, this.allowedValueRange != null ? new StateVariableAllowedValueRange(this.allowedValueRange.minimum.longValue(), this.allowedValueRange.maximum.longValue(), this.allowedValueRange.step.longValue()) : null), this.eventDetails);
    }
}

