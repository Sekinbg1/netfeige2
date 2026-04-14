package org.teleal.cling.model;

/* JADX INFO: loaded from: classes.dex */
public interface Command<T> {
    void execute(ServiceManager<T> serviceManager) throws Exception;
}

