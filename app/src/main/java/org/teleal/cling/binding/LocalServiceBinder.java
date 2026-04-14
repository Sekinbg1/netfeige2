package org.teleal.cling.binding;

import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;

/* JADX INFO: loaded from: classes.dex */
public interface LocalServiceBinder {
    LocalService read(Class<?> cls) throws LocalServiceBindingException;

    LocalService read(Class<?> cls, ServiceId serviceId, ServiceType serviceType, boolean z, Class[] clsArr) throws LocalServiceBindingException;
}

