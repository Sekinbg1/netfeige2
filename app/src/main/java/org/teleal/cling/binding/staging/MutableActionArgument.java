package org.teleal.cling.binding.staging;

import org.teleal.cling.model.meta.ActionArgument;

/* JADX INFO: loaded from: classes.dex */
public class MutableActionArgument {
    public ActionArgument.Direction direction;
    public String name;
    public String relatedStateVariable;
    public boolean retval;

    public ActionArgument build() {
        return new ActionArgument(this.name, this.relatedStateVariable, this.direction, this.retval);
    }
}

