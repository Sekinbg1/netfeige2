package org.teleal.cling.support.avtransport.callback;

import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.DeviceCapabilities;

/* JADX INFO: loaded from: classes.dex */
public abstract class GetDeviceCapabilities extends ActionCallback {
    private static Logger log = Logger.getLogger(GetDeviceCapabilities.class.getName());

    public abstract void received(ActionInvocation actionInvocation, DeviceCapabilities deviceCapabilities);

    public GetDeviceCapabilities(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetDeviceCapabilities(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        super(new ActionInvocation(service.getAction("GetDeviceCapabilities")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        received(actionInvocation, new DeviceCapabilities((Map<String, ActionArgumentValue>) actionInvocation.getOutputMap()));
    }
}

