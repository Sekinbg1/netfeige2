package org.teleal.cling.support.model;

import java.util.ArrayList;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.types.InvalidValueException;

/* JADX INFO: loaded from: classes.dex */
public class ProtocolInfos extends ArrayList<ProtocolInfo> {
    public ProtocolInfos(ProtocolInfo... protocolInfoArr) {
        for (ProtocolInfo protocolInfo : protocolInfoArr) {
            add(protocolInfo);
        }
    }

    public ProtocolInfos(String str) throws InvalidValueException {
        String[] strArrFromCommaSeparatedList = ModelUtil.fromCommaSeparatedList(str);
        if (strArrFromCommaSeparatedList != null) {
            for (String str2 : strArrFromCommaSeparatedList) {
                add(new ProtocolInfo(str2));
            }
        }
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        return ModelUtil.toCommaSeparatedList(toArray(new ProtocolInfo[size()]));
    }
}

