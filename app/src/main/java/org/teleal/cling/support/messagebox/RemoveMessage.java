package org.teleal.cling.support.messagebox;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.messagebox.model.Message;

/* JADX INFO: loaded from: classes.dex */
public abstract class RemoveMessage extends ActionCallback {
    public RemoveMessage(Service service, Message message) {
        this(service, message.getId());
    }

    public RemoveMessage(Service service, int i) {
        super(new ActionInvocation(service.getAction("RemoveMessage")));
        getActionInvocation().setInput("MessageID", Integer.valueOf(i));
    }
}

