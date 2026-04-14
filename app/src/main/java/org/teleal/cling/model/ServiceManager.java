package org.teleal.cling.model;

import java.beans.PropertyChangeSupport;
import java.util.Collection;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.state.StateVariableValue;

/* JADX INFO: loaded from: classes.dex */
public interface ServiceManager<T> {
    public static final String EVENTED_STATE_VARIABLES = "_EventedStateVariables";

    void execute(Command<T> command) throws Exception;

    T getImplementation();

    PropertyChangeSupport getPropertyChangeSupport();

    LocalService<T> getService();

    Collection<StateVariableValue> readEventedStateVariableValues() throws Exception;
}

