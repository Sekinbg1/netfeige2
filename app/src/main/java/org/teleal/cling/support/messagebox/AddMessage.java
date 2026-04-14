package org.teleal.cling.support.messagebox;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.messagebox.model.Message;
import org.teleal.common.util.MimeType;

/* JADX INFO: loaded from: classes.dex */
public abstract class AddMessage extends ActionCallback {
    protected final MimeType mimeType;

    public AddMessage(Service service, Message message) {
        super(new ActionInvocation(service.getAction("AddMessage")));
        this.mimeType = MimeType.valueOf("text/xml;charset=\"utf-8\"");
        getActionInvocation().setInput("MessageID", Integer.toString(message.getId()));
        getActionInvocation().setInput("MessageType", this.mimeType.toString());
        getActionInvocation().setInput("Message", message.toString());
    }
}

