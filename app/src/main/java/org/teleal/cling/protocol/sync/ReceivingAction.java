package org.teleal.cling.protocol.sync;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.control.IncomingActionRequestMessage;
import org.teleal.cling.model.message.control.OutgoingActionResponseMessage;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.resource.ServiceControlResource;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.protocol.ReceivingSync;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class ReceivingAction extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingAction.class.getName());
    protected static final ThreadLocal<IncomingActionRequestMessage> requestThreadLocal = new ThreadLocal<>();
    protected static final ThreadLocal<UpnpHeaders> extraResponseHeadersThreadLocal = new ThreadLocal<>();

    public ReceivingAction(UpnpService upnpService, StreamRequestMessage streamRequestMessage) {
        super(upnpService, streamRequestMessage);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ReceivingSync
    protected StreamResponseMessage executeSync() {
        ActionInvocation<LocalService> actionInvocation;
        OutgoingActionResponseMessage outgoingActionResponseMessage;
        Logger logger;
        StringBuilder sb;
        ContentTypeHeader contentTypeHeader = (ContentTypeHeader) ((StreamRequestMessage) getInputMessage()).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
        if (contentTypeHeader != null && !contentTypeHeader.isUDACompliantXML()) {
            log.warning("Received invalid Content-Type '" + contentTypeHeader + "': " + getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE));
        }
        if (contentTypeHeader == null) {
            log.warning("Received without Content-Type: " + getInputMessage());
        }
        ServiceControlResource serviceControlResource = (ServiceControlResource) getUpnpService().getRegistry().getResource(ServiceControlResource.class, ((StreamRequestMessage) getInputMessage()).getUri());
        if (serviceControlResource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return null;
        }
        log.fine("Found local action resource matching relative request URI: " + ((StreamRequestMessage) getInputMessage()).getUri());
        try {
            try {
                IncomingActionRequestMessage incomingActionRequestMessage = new IncomingActionRequestMessage((StreamRequestMessage) getInputMessage(), serviceControlResource.getModel());
                requestThreadLocal.set(incomingActionRequestMessage);
                extraResponseHeadersThreadLocal.set(new UpnpHeaders());
                log.finer("Created incoming action request message: " + incomingActionRequestMessage);
                actionInvocation = new ActionInvocation<>(incomingActionRequestMessage.getAction());
                log.fine("Reading body of request message");
                getUpnpService().getConfiguration().getSoapActionProcessor().readBody(incomingActionRequestMessage, actionInvocation);
                log.fine("Executing on local service: " + actionInvocation);
                serviceControlResource.getModel().getExecutor(actionInvocation.getAction()).execute(actionInvocation);
                if (actionInvocation.getFailure() == null) {
                    outgoingActionResponseMessage = new OutgoingActionResponseMessage(actionInvocation.getAction());
                } else {
                    outgoingActionResponseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR, actionInvocation.getAction());
                }
            } catch (ActionException e) {
                log.finer("Error executing local action: " + e);
                actionInvocation = new ActionInvocation<>(e);
                outgoingActionResponseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
                if (extraResponseHeadersThreadLocal.get() != null) {
                    logger = log;
                    sb = new StringBuilder();
                }
            } catch (UnsupportedDataException e2) {
                if (log.isLoggable(Level.FINER)) {
                    log.log(Level.FINER, "Error reading action request XML body: " + e2.toString(), Exceptions.unwrap(e2));
                }
                actionInvocation = new ActionInvocation<>(Exceptions.unwrap(e2) instanceof ActionException ? (ActionException) Exceptions.unwrap(e2) : new ActionException(ErrorCode.ACTION_FAILED, e2.getMessage()));
                outgoingActionResponseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
                if (extraResponseHeadersThreadLocal.get() != null) {
                    logger = log;
                    sb = new StringBuilder();
                }
            }
            if (extraResponseHeadersThreadLocal.get() != null) {
                logger = log;
                sb = new StringBuilder();
                sb.append("Merging extra headers into action response message: ");
                sb.append(extraResponseHeadersThreadLocal.get().size());
                logger.fine(sb.toString());
                outgoingActionResponseMessage.getHeaders().putAll(extraResponseHeadersThreadLocal.get());
            }
            try {
                log.fine("Writing body of response message");
                getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(outgoingActionResponseMessage, actionInvocation);
                log.fine("Returning finished response message: " + outgoingActionResponseMessage);
                return outgoingActionResponseMessage;
            } catch (UnsupportedDataException e3) {
                log.warning("Failure writing body of response message, sending '500 Internal Server Error' without body");
                log.log(Level.WARNING, "Exception root cause: ", Exceptions.unwrap(e3));
                return new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
            }
        } finally {
            requestThreadLocal.set(null);
            extraResponseHeadersThreadLocal.set(null);
        }
    }

    public static IncomingActionRequestMessage getRequestMessage() {
        return requestThreadLocal.get();
    }

    public static UpnpHeaders getExtraResponseHeaders() {
        return extraResponseHeadersThreadLocal.get();
    }
}

