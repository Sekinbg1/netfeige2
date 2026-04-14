package org.teleal.cling.support.igd.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.model.PortMapping;

/* JADX INFO: loaded from: classes.dex */
public abstract class PortMappingDelete extends ActionCallback {
    protected final PortMapping portMapping;

    public PortMappingDelete(Service service, PortMapping portMapping) {
        this(service, null, portMapping);
    }

    protected PortMappingDelete(Service service, ControlPoint controlPoint, PortMapping portMapping) {
        super(new ActionInvocation(service.getAction("DeletePortMapping")), controlPoint);
        this.portMapping = portMapping;
        getActionInvocation().setInput("NewExternalPort", portMapping.getExternalPort());
        getActionInvocation().setInput("NewProtocol", portMapping.getProtocol());
        if (portMapping.hasRemoteHost()) {
            getActionInvocation().setInput("NewRemoteHost", portMapping.getRemoteHost());
        }
    }
}

