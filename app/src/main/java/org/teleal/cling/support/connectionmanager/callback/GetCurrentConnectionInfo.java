package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;

/* JADX INFO: loaded from: classes.dex */
public abstract class GetCurrentConnectionInfo extends ActionCallback {
    public abstract void received(ActionInvocation actionInvocation, ConnectionInfo connectionInfo);

    public GetCurrentConnectionInfo(Service service, int i) {
        this(service, null, i);
    }

    protected GetCurrentConnectionInfo(Service service, ControlPoint controlPoint, int i) {
        super(new ActionInvocation(service.getAction("GetCurrentConnectionInfo")), controlPoint);
        getActionInvocation().setInput("ConnectionID", Integer.valueOf(i));
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        try {
            received(actionInvocation, new ConnectionInfo(((Integer) actionInvocation.getInput("ConnectionID").getValue()).intValue(), ((Integer) actionInvocation.getOutput("RcsID").getValue()).intValue(), ((Integer) actionInvocation.getOutput("AVTransportID").getValue()).intValue(), new ProtocolInfo(actionInvocation.getOutput("ProtocolInfo").toString()), new ServiceReference(actionInvocation.getOutput("PeerConnectionManager").toString()), ((Integer) actionInvocation.getOutput("PeerConnectionID").getValue()).intValue(), ConnectionInfo.Direction.valueOf(actionInvocation.getOutput("Direction").toString()), ConnectionInfo.Status.valueOf(actionInvocation.getOutput("Status").toString())));
        } catch (Exception e) {
            actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ConnectionInfo response: " + e, e));
            failure(actionInvocation, null);
        }
    }
}

