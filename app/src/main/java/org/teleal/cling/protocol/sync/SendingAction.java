package org.teleal.cling.protocol.sync;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.control.IncomingActionResponseMessage;
import org.teleal.cling.model.message.control.OutgoingActionRequestMessage;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.protocol.SendingSync;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class SendingAction extends SendingSync<OutgoingActionRequestMessage, IncomingActionResponseMessage> {
	private static final Logger log = Logger.getLogger(SendingAction.class.getName());
	protected final ActionInvocation actionInvocation;

	public SendingAction(UpnpService upnpService, ActionInvocation actionInvocation, URL url) {
		super(upnpService, new OutgoingActionRequestMessage(actionInvocation, url));
		this.actionInvocation = actionInvocation;
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // org.teleal.cling.protocol.SendingSync
	public IncomingActionResponseMessage executeSync() {
		return invokeRemote(getInputMessage());
	}

	protected IncomingActionResponseMessage invokeRemote(OutgoingActionRequestMessage outgoingActionRequestMessage) {
		Device device = this.actionInvocation.getAction().getService().getDevice();
		log.fine("Sending outgoing action call '" + this.actionInvocation.getAction().getName() + "' to remote service of: " + device);
		IncomingActionResponseMessage incomingActionResponseMessage = null;
		try {
			StreamResponseMessage streamResponseMessageSendRemoteRequest = sendRemoteRequest(outgoingActionRequestMessage);
			if (streamResponseMessageSendRemoteRequest == null) {
				log.fine("No connection or no no response received, returning null");
				this.actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Connection error or no response received"));
				return null;
			}
			IncomingActionResponseMessage incomingActionResponseMessage2 = new IncomingActionResponseMessage(streamResponseMessageSendRemoteRequest);
			try {
				if (incomingActionResponseMessage2.isFailedNonRecoverable()) {
					log.fine("Response was a non-recoverable failure: " + incomingActionResponseMessage2);
					throw new ActionException(ErrorCode.ACTION_FAILED, "Non-recoverable remote execution failure: " + incomingActionResponseMessage2.getOperation().getResponseDetails());
				}
				if (incomingActionResponseMessage2.isFailedRecoverable()) {
					handleResponseFailure(incomingActionResponseMessage2);
				} else {
					handleResponse(incomingActionResponseMessage2);
				}
				return incomingActionResponseMessage2;
			} catch (ActionException e) {
				log.fine("Remote action invocation failed, returning Internal Server Error message: " + e.getMessage());
				this.actionInvocation.setFailure(e);
				return (incomingActionResponseMessage2 == null || !incomingActionResponseMessage2.getOperation().isFailed()) ? new IncomingActionResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR)) : incomingActionResponseMessage2;
			}
		} catch (ActionException e2) {
			log.fine("Remote action invocation failed: " + e2.getMessage());
			this.actionInvocation.setFailure(e2);
			return new IncomingActionResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
		}
	}

	protected StreamResponseMessage sendRemoteRequest(OutgoingActionRequestMessage outgoingActionRequestMessage) throws ActionException {
		try {
			log.fine("Writing SOAP request body of: " + outgoingActionRequestMessage);
			getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(outgoingActionRequestMessage, this.actionInvocation);
			log.fine("Sending SOAP body of message as stream to remote device");
			return getUpnpService().getRouter().send(outgoingActionRequestMessage);
		} catch (UnsupportedDataException e) {
			log.fine("Error writing SOAP body: " + e);
			log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
			throw new ActionException(ErrorCode.ACTION_FAILED, "Error writing request message. " + e.getMessage());
		}
	}

	protected void handleResponse(IncomingActionResponseMessage incomingActionResponseMessage) throws ActionException {
		try {
			log.fine("Received response for outgoing call, reading SOAP response body: " + incomingActionResponseMessage);
			getUpnpService().getConfiguration().getSoapActionProcessor().readBody(incomingActionResponseMessage, this.actionInvocation);
		} catch (UnsupportedDataException e) {
			log.fine("Error reading SOAP body: " + e);
			log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
			throw new ActionException(ErrorCode.ACTION_FAILED, "Error reading response message. " + e.getMessage());
		}
	}

	protected void handleResponseFailure(IncomingActionResponseMessage incomingActionResponseMessage) throws ActionException {
		try {
			log.fine("Received response with Internal Server Error, reading SOAP failure message");
			getUpnpService().getConfiguration().getSoapActionProcessor().readBody(incomingActionResponseMessage, this.actionInvocation);
		} catch (UnsupportedDataException e) {
			log.fine("Error reading SOAP body: " + e);
			log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
			throw new ActionException(ErrorCode.ACTION_FAILED, "Error reading response failure message. " + e.getMessage());
		}
	}
}

