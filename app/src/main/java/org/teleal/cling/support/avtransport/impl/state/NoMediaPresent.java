package org.teleal.cling.support.avtransport.impl.state;

import java.net.URI;
import java.util.logging.Logger;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.TransportAction;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportState;

/* JADX INFO: loaded from: classes.dex */
public abstract class NoMediaPresent<T extends AVTransport> extends AbstractState<T> {
    private static final Logger log = Logger.getLogger(Stopped.class.getName());

    public abstract Class<? extends AbstractState> setTransportURI(URI uri, String str);

    public NoMediaPresent(T t) {
        super(t);
    }

    public void onEntry() {
        log.fine("Setting transport state to NO_MEDIA_PRESENT");
        getTransport().setTransportInfo(new TransportInfo(TransportState.NO_MEDIA_PRESENT, getTransport().getTransportInfo().getCurrentTransportStatus(), getTransport().getTransportInfo().getCurrentSpeed()));
        getTransport().getLastChange().setEventedValue(getTransport().getInstanceId(), new AVTransportVariable.TransportState(TransportState.NO_MEDIA_PRESENT), new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions()));
    }

    @Override // org.teleal.cling.support.avtransport.impl.state.AbstractState
    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[]{TransportAction.Stop};
    }
}

