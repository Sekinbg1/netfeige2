package org.teleal.cling.support.avtransport.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.TransportAction;

/* JADX INFO: loaded from: classes.dex */
public abstract class GetCurrentTransportActions extends ActionCallback {
    private static Logger log = Logger.getLogger(GetCurrentTransportActions.class.getName());

    public abstract void received(ActionInvocation actionInvocation, TransportAction[] transportActionArr);

    public GetCurrentTransportActions(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetCurrentTransportActions(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        super(new ActionInvocation(service.getAction("GetCurrentTransportActions")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        received(actionInvocation, TransportAction.valueOfCommaSeparatedList((String) actionInvocation.getOutput("Actions").getValue()));
    }
}

