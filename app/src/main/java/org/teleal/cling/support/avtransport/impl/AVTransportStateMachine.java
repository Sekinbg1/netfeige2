package org.teleal.cling.support.avtransport.impl;

import java.net.URI;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.common.statemachine.StateMachine;

/* JADX INFO: loaded from: classes.dex */
public interface AVTransportStateMachine extends StateMachine<AbstractState> {
    void next();

    void pause();

    void play(String str);

    void previous();

    void record();

    void seek(SeekMode seekMode, String str);

    void setNextTransportURI(URI uri, String str);

    void setTransportURI(URI uri, String str);

    void stop();
}

