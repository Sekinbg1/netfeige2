package org.teleal.cling.support.model;

import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class PersonWithRole extends Person {
    private String role;

    public PersonWithRole(String str) {
        super(str);
    }

    public PersonWithRole(String str, String str2) {
        super(str);
        this.role = str2;
    }

    public String getRole() {
        return this.role;
    }

    public void setOnElement(Element element) {
        element.setTextContent(toString());
        element.setAttribute("role", getRole() != null ? getRole() : "");
    }
}

