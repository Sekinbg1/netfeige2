package org.teleal.cling.support.renderingcontrol.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.support.model.Channel;

/* JADX INFO: loaded from: classes.dex */
public abstract class SetVolume extends ActionCallback {
    private static Logger log = Logger.getLogger(SetVolume.class.getName());

    public SetVolume(Service service, long j) {
        this(new UnsignedIntegerFourBytes(0L), service, j);
    }

    public SetVolume(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service, long j) {
        super(new ActionInvocation(service.getAction("SetVolume")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
        getActionInvocation().setInput("DesiredVolume", new UnsignedIntegerTwoBytes(j));
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        log.fine("Executed successfully");
    }
}

