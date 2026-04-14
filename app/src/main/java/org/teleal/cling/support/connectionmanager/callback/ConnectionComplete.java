package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;

/* JADX INFO: loaded from: classes.dex */
public abstract class ConnectionComplete extends ActionCallback {
    public ConnectionComplete(Service service, int i) {
        this(service, null, i);
    }

    protected ConnectionComplete(Service service, ControlPoint controlPoint, int i) {
        super(new ActionInvocation(service.getAction("ConnectionComplete")), controlPoint);
        getActionInvocation().setInput("ConnectionID", Integer.valueOf(i));
    }
}

