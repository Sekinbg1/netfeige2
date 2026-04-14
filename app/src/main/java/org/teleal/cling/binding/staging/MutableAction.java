package org.teleal.cling.binding.staging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;

/* JADX INFO: loaded from: classes.dex */
public class MutableAction {
    public List<MutableActionArgument> arguments = new ArrayList();
    public String name;

    public Action build() {
        return new Action(this.name, createActionArgumennts());
    }

    public ActionArgument[] createActionArgumennts() {
        ActionArgument[] actionArgumentArr = new ActionArgument[this.arguments.size()];
        Iterator<MutableActionArgument> it = this.arguments.iterator();
        int i = 0;
        while (it.hasNext()) {
            actionArgumentArr[i] = it.next().build();
            i++;
        }
        return actionArgumentArr;
    }
}

