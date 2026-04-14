package org.teleal.cling.support.avtransport.impl.state;

import java.net.URI;
import java.util.logging.Logger;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.TransportAction;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportState;

/* JADX INFO: loaded from: classes.dex */
public abstract class Playing<T extends AVTransport> extends AbstractState {
    private static final Logger log = Logger.getLogger(Playing.class.getName());

    public abstract Class<? extends AbstractState> next();

    public abstract Class<? extends AbstractState> pause();

    public abstract Class<? extends AbstractState> play(String str);

    public abstract Class<? extends AbstractState> previous();

    public abstract Class<? extends AbstractState> seek(SeekMode seekMode, String str);

    public abstract Class<? extends AbstractState> setTransportURI(URI uri, String str);

    public abstract Class<? extends AbstractState> stop();

    public Playing(T t) {
        super(t);
    }

    public void onEntry() {
        log.fine("Setting transport state to PLAYING");
        getTransport().setTransportInfo(new TransportInfo(TransportState.PLAYING, getTransport().getTransportInfo().getCurrentTransportStatus(), getTransport().getTransportInfo().getCurrentSpeed()));
        getTransport().getLastChange().setEventedValue(getTransport().getInstanceId(), new AVTransportVariable.TransportState(TransportState.PLAYING), new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions()));
    }

    @Override // org.teleal.cling.support.avtransport.impl.state.AbstractState
    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[]{TransportAction.Stop, TransportAction.Play, TransportAction.Pause, TransportAction.Next, TransportAction.Previous, TransportAction.Seek};
    }
}

