package org.teleal.cling.support.avtransport.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* JADX INFO: loaded from: classes.dex */
public abstract class SetAVTransportURI extends ActionCallback {
    private static Logger log = Logger.getLogger(SetAVTransportURI.class.getName());

    public SetAVTransportURI(Service service, String str) {
        this(new UnsignedIntegerFourBytes(0L), service, str, null);
    }

    public SetAVTransportURI(Service service, String str, String str2) {
        this(new UnsignedIntegerFourBytes(0L), service, str, str2);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service, String str) {
        this(unsignedIntegerFourBytes, service, str, null);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service, String str, String str2) {
        super(new ActionInvocation(service.getAction("SetAVTransportURI")));
        log.fine("Creating SetAVTransportURI action for URI: " + str);
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
        getActionInvocation().setInput("CurrentURI", str);
        getActionInvocation().setInput("CurrentURIMetaData", str2);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        log.fine("Execution successful");
    }
}

