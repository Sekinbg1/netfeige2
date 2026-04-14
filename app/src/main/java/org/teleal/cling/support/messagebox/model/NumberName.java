package org.teleal.cling.support.messagebox.model;

import org.teleal.cling.support.messagebox.parser.MessageElement;

/* JADX INFO: loaded from: classes.dex */
public class NumberName implements ElementAppender {
    private String name;
    private String number;

    public NumberName(String str, String str2) {
        this.number = str;
        this.name = str2;
    }

    public String getNumber() {
        return this.number;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.teleal.cling.support.messagebox.model.ElementAppender
    public void appendMessageElements(MessageElement messageElement) {
        messageElement.createChild("Number").setContent(getNumber());
        messageElement.createChild("Name").setContent(getName());
    }
}

