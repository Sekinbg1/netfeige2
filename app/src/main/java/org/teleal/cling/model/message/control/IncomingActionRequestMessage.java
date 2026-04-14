package org.teleal.cling.model.message.control;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.header.SoapActionHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.QueryStateVariableAction;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.SoapActionType;

/* JADX INFO: loaded from: classes.dex */
public class IncomingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage {
    private final Action action;
    private final String actionNamespace;

    public IncomingActionRequestMessage(StreamRequestMessage streamRequestMessage, LocalService localService) throws ActionException {
        super(streamRequestMessage);
        SoapActionHeader soapActionHeader = (SoapActionHeader) getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION, SoapActionHeader.class);
        if (soapActionHeader == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Missing SOAP action header");
        }
        SoapActionType value = soapActionHeader.getValue();
        Action<LocalService> action = localService.getAction(value.getActionName());
        this.action = action;
        if (action == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't implement action: " + value.getActionName());
        }
        if (!QueryStateVariableAction.ACTION_NAME.equals(value.getActionName()) && !localService.getServiceType().implementsVersion(value.getServiceType())) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't support the requested service version");
        }
        this.actionNamespace = value.getTypeString();
    }

    public Action getAction() {
        return this.action;
    }

    @Override // org.teleal.cling.model.message.control.ActionMessage
    public String getActionNamespace() {
        return this.actionNamespace;
    }
}

