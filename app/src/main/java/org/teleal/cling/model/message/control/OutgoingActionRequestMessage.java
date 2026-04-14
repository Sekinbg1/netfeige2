package org.teleal.cling.model.message.control;

import java.net.URL;
import java.util.logging.Logger;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.SoapActionHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.QueryStateVariableAction;
import org.teleal.cling.model.types.SoapActionType;

/* JADX INFO: loaded from: classes.dex */
public class OutgoingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage {
	private static Logger log = Logger.getLogger(OutgoingActionRequestMessage.class.getName());
	private final String actionNamespace;

	public OutgoingActionRequestMessage(ActionInvocation actionInvocation, URL url) {
		this(actionInvocation.getAction(), new UpnpRequest(UpnpRequest.Method.POST, url));
	}

	public OutgoingActionRequestMessage(Action action, UpnpRequest upnpRequest) {
		super(upnpRequest);
		SoapActionHeader soapActionHeader;
		getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8));
		if (action instanceof QueryStateVariableAction) {
			log.fine("Adding magic control SOAP action header for state variable query action");
			soapActionHeader = new SoapActionHeader(new SoapActionType("schemas-upnp-org", SoapActionType.MAGIC_CONTROL_TYPE, null, action.getName()));
		} else {
			soapActionHeader = new SoapActionHeader(new SoapActionType(action.getService().getServiceType(), action.getName()));
		}
		this.actionNamespace = soapActionHeader.getValue().getTypeString();
		if (getOperation().getMethod().equals(UpnpRequest.Method.POST)) {
			getHeaders().add(UpnpHeader.Type.SOAPACTION, soapActionHeader);
			log.fine("Added SOAP action header: " + getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION).getString());
			return;
		}
		throw new IllegalArgumentException("Can't send action with request method: " + getOperation().getMethod());
	}

	@Override // org.teleal.cling.model.message.control.ActionMessage
	public String getActionNamespace() {
		return this.actionNamespace;
	}
}

