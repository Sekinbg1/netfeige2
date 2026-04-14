package org.teleal.cling.model.message.control;

import org.teleal.cling.model.message.UpnpMessage;

/* JADX INFO: loaded from: classes.dex */
public interface ActionMessage {
    String getActionNamespace();

    Object getBody();

    byte[] getBodyBytes();

    String getBodyString();

    UpnpMessage.BodyType getBodyType();

    void setBody(UpnpMessage.BodyType bodyType, Object obj);
}

