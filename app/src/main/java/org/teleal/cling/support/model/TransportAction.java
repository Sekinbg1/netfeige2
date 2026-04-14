package org.teleal.cling.support.model;

import java.util.ArrayList;
import org.teleal.cling.model.ModelUtil;

/* JADX INFO: loaded from: classes.dex */
public enum TransportAction {
    Play,
    Stop,
    Pause,
    Seek,
    Next,
    Previous,
    Record;

    public static TransportAction[] valueOfCommaSeparatedList(String str) {
        String[] strArrFromCommaSeparatedList = ModelUtil.fromCommaSeparatedList(str);
        if (strArrFromCommaSeparatedList == null) {
            return new TransportAction[0];
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : strArrFromCommaSeparatedList) {
            for (TransportAction transportAction : values()) {
                if (transportAction.name().equals(str2)) {
                    arrayList.add(transportAction);
                }
            }
        }
        return (TransportAction[]) arrayList.toArray(new TransportAction[arrayList.size()]);
    }
}

