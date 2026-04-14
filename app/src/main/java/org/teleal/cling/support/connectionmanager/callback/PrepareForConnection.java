package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;

/* JADX INFO: loaded from: classes.dex */
public abstract class PrepareForConnection extends ActionCallback {
    public abstract void received(ActionInvocation actionInvocation, int i, int i2, int i3);

    public PrepareForConnection(Service service, ProtocolInfo protocolInfo, ServiceReference serviceReference, int i, ConnectionInfo.Direction direction) {
        this(service, null, protocolInfo, serviceReference, i, direction);
    }

    public PrepareForConnection(Service service, ControlPoint controlPoint, ProtocolInfo protocolInfo, ServiceReference serviceReference, int i, ConnectionInfo.Direction direction) {
        super(new ActionInvocation(service.getAction("PrepareForConnection")), controlPoint);
        getActionInvocation().setInput("RemoteProtocolInfo", protocolInfo.toString());
        getActionInvocation().setInput("PeerConnectionManager", serviceReference.toString());
        getActionInvocation().setInput("PeerConnectionID", Integer.valueOf(i));
        getActionInvocation().setInput("Direction", direction.toString());
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        received(actionInvocation, ((Integer) actionInvocation.getOutput("ConnectionID").getValue()).intValue(), ((Integer) actionInvocation.getOutput("RcsID").getValue()).intValue(), ((Integer) actionInvocation.getOutput("AVTransportID").getValue()).intValue());
    }
}

