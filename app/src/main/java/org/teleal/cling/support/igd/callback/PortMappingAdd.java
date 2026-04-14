package org.teleal.cling.support.igd.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.model.PortMapping;

/* JADX INFO: loaded from: classes.dex */
public abstract class PortMappingAdd extends ActionCallback {
    protected final PortMapping portMapping;

    public PortMappingAdd(Service service, PortMapping portMapping) {
        this(service, null, portMapping);
    }

    protected PortMappingAdd(Service service, ControlPoint controlPoint, PortMapping portMapping) {
        super(new ActionInvocation(service.getAction("AddPortMapping")), controlPoint);
        this.portMapping = portMapping;
        getActionInvocation().setInput("NewExternalPort", portMapping.getExternalPort());
        getActionInvocation().setInput("NewProtocol", portMapping.getProtocol());
        getActionInvocation().setInput("NewInternalClient", portMapping.getInternalClient());
        getActionInvocation().setInput("NewInternalPort", portMapping.getInternalPort());
        getActionInvocation().setInput("NewLeaseDuration", portMapping.getLeaseDurationSeconds());
        getActionInvocation().setInput("NewEnabled", Boolean.valueOf(portMapping.isEnabled()));
        if (portMapping.hasRemoteHost()) {
            getActionInvocation().setInput("NewRemoteHost", portMapping.getRemoteHost());
        }
        if (portMapping.hasDescription()) {
            getActionInvocation().setInput("NewPortMappingDescription", portMapping.getDescription());
        }
    }
}

