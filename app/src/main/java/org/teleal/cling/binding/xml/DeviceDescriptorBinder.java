package org.teleal.cling.binding.xml;

import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public interface DeviceDescriptorBinder {
    Document buildDOM(Device device, ControlPointInfo controlPointInfo, Namespace namespace) throws DescriptorBindingException;

    <T extends Device> T describe(T t, String str) throws ValidationException, DescriptorBindingException;

    <T extends Device> T describe(T t, Document document) throws ValidationException, DescriptorBindingException;

    String generate(Device device, ControlPointInfo controlPointInfo, Namespace namespace) throws DescriptorBindingException;
}

