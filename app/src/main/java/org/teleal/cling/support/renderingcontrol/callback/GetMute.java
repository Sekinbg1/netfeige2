package org.teleal.cling.support.renderingcontrol.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.Channel;

/* JADX INFO: loaded from: classes.dex */
public abstract class GetMute extends ActionCallback {
    private static Logger log = Logger.getLogger(GetMute.class.getName());

    public abstract void received(ActionInvocation actionInvocation, boolean z);

    public GetMute(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetMute(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        super(new ActionInvocation(service.getAction("GetMute")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        received(actionInvocation, ((Boolean) actionInvocation.getOutput("CurrentMute").getValue()).booleanValue());
    }
}

