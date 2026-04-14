package org.teleal.cling.support.avtransport.callback;

import com.netfeige.dlna.ContentTree;
import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* JADX INFO: loaded from: classes.dex */
public abstract class Play extends ActionCallback {
    private static Logger log = Logger.getLogger(Play.class.getName());

    public Play(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service, ContentTree.VIDEO_ID);
    }

    public Play(Service service, String str) {
        this(new UnsignedIntegerFourBytes(0L), service, str);
    }

    public Play(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        this(unsignedIntegerFourBytes, service, ContentTree.VIDEO_ID);
    }

    public Play(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service, String str) {
        super(new ActionInvocation(service.getAction("Play")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
        getActionInvocation().setInput("Speed", str);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        log.fine("Execution successful");
    }
}

