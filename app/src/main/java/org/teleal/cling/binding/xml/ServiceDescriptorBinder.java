package org.teleal.cling.binding.xml;

import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Service;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public interface ServiceDescriptorBinder {
    Document buildDOM(Service service) throws DescriptorBindingException;

    <T extends Service> T describe(T t, String str) throws ValidationException, DescriptorBindingException;

    <T extends Service> T describe(T t, Document document) throws ValidationException, DescriptorBindingException;

    String generate(Service service) throws DescriptorBindingException;
}

