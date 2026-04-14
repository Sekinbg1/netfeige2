package org.teleal.cling.binding.xml;

import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.teleal.cling.binding.staging.MutableDevice;
import org.teleal.cling.binding.staging.MutableIcon;
import org.teleal.cling.binding.staging.MutableService;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.XMLUtil;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.types.DLNACaps;
import org.teleal.cling.model.types.DLNADoc;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.common.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: classes.dex */
public class UDA10DeviceDescriptorBinderImpl implements DeviceDescriptorBinder {
    private static Logger log = Logger.getLogger(DeviceDescriptorBinder.class.getName());

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public <D extends Device> D describe(D d, String str) throws ValidationException, DescriptorBindingException {
        if (str == null || str.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            log.fine("Populating device from XML descriptor: " + d);
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setNamespaceAware(true);
            return (D) describe(d, documentBuilderFactoryNewInstance.newDocumentBuilder().parse(new InputSource(new StringReader(str.trim()))));
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw new DescriptorBindingException("Could not parse device descriptor: " + e2.toString(), e2);
        }
    }

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public <D extends Device> D describe(D d, Document document) throws ValidationException, DescriptorBindingException {
        try {
            log.fine("Populating device from DOM: " + d);
            MutableDevice mutableDevice = new MutableDevice();
            hydrateRoot(mutableDevice, document.getDocumentElement());
            return (D) buildInstance(d, mutableDevice);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw new DescriptorBindingException("Could not parse device DOM: " + e2.toString(), e2);
        }
    }

    public <D extends Device> D buildInstance(D d, MutableDevice mutableDevice) throws ValidationException {
        return (D) mutableDevice.build(d);
    }

    protected void hydrateRoot(MutableDevice mutableDevice, Element element) throws DescriptorBindingException {
        if (element.getNamespaceURI() == null || !element.getNamespaceURI().equals(Descriptor.Device.NAMESPACE_URI)) {
            log.warning("Wrong XML namespace declared on root element: " + element.getNamespaceURI());
        }
        if (!element.getNodeName().equals(Descriptor.Device.ELEMENT.root.name())) {
            throw new DescriptorBindingException("Root element name is not <root>: " + element.getNodeName());
        }
        NodeList childNodes = element.getChildNodes();
        Node node = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.specVersion.equals(nodeItem)) {
                    hydrateSpecVersion(mutableDevice, nodeItem);
                } else if (Descriptor.Device.ELEMENT.URLBase.equals(nodeItem)) {
                    try {
                        mutableDevice.baseURL = new URL(XMLUtil.getTextContent(nodeItem));
                    } catch (Exception e) {
                        throw new DescriptorBindingException("Invalid URLBase: " + e.getMessage());
                    }
                } else if (!Descriptor.Device.ELEMENT.device.equals(nodeItem)) {
                    log.finer("Ignoring unknown element: " + nodeItem.getNodeName());
                } else {
                    if (node != null) {
                        throw new DescriptorBindingException("Found multiple <device> elements in <root>");
                    }
                    node = nodeItem;
                }
            }
        }
        if (node == null) {
            throw new DescriptorBindingException("No <device> element in <root>");
        }
        hydrateDevice(mutableDevice, node);
    }

    public void hydrateSpecVersion(MutableDevice mutableDevice, Node node) throws DescriptorBindingException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.major.equals(nodeItem)) {
                    mutableDevice.udaVersion.major = Integer.valueOf(XMLUtil.getTextContent(nodeItem)).intValue();
                } else if (Descriptor.Device.ELEMENT.minor.equals(nodeItem)) {
                    mutableDevice.udaVersion.minor = Integer.valueOf(XMLUtil.getTextContent(nodeItem)).intValue();
                }
            }
        }
    }

    public void hydrateDevice(MutableDevice mutableDevice, Node node) throws DescriptorBindingException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.deviceType.equals(nodeItem)) {
                    mutableDevice.deviceType = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.friendlyName.equals(nodeItem)) {
                    mutableDevice.friendlyName = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.manufacturer.equals(nodeItem)) {
                    mutableDevice.manufacturer = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.manufacturerURL.equals(nodeItem)) {
                    mutableDevice.manufacturerURI = parseURI(XMLUtil.getTextContent(nodeItem));
                } else if (Descriptor.Device.ELEMENT.modelDescription.equals(nodeItem)) {
                    mutableDevice.modelDescription = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.modelName.equals(nodeItem)) {
                    mutableDevice.modelName = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.modelNumber.equals(nodeItem)) {
                    mutableDevice.modelNumber = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.modelURL.equals(nodeItem)) {
                    mutableDevice.modelURI = parseURI(XMLUtil.getTextContent(nodeItem));
                } else if (Descriptor.Device.ELEMENT.presentationURL.equals(nodeItem)) {
                    mutableDevice.presentationURI = parseURI(XMLUtil.getTextContent(nodeItem));
                } else if (Descriptor.Device.ELEMENT.UPC.equals(nodeItem)) {
                    mutableDevice.upc = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.serialNumber.equals(nodeItem)) {
                    mutableDevice.serialNumber = XMLUtil.getTextContent(nodeItem);
                } else if (Descriptor.Device.ELEMENT.UDN.equals(nodeItem)) {
                    mutableDevice.udn = UDN.valueOf(XMLUtil.getTextContent(nodeItem));
                } else if (Descriptor.Device.ELEMENT.iconList.equals(nodeItem)) {
                    hydrateIconList(mutableDevice, nodeItem);
                } else if (Descriptor.Device.ELEMENT.serviceList.equals(nodeItem)) {
                    hydrateServiceList(mutableDevice, nodeItem);
                } else if (Descriptor.Device.ELEMENT.deviceList.equals(nodeItem)) {
                    hydrateDeviceList(mutableDevice, nodeItem);
                } else if (Descriptor.Device.ELEMENT.X_DLNADOC.equals(nodeItem) && Descriptor.Device.DLNA_PREFIX.equals(nodeItem.getPrefix())) {
                    String textContent = XMLUtil.getTextContent(nodeItem);
                    try {
                        mutableDevice.dlnaDocs.add(DLNADoc.valueOf(textContent));
                    } catch (InvalidValueException unused) {
                        log.info("Invalid X_DLNADOC value, ignoring value: " + textContent);
                    }
                } else if (Descriptor.Device.ELEMENT.X_DLNACAP.equals(nodeItem) && Descriptor.Device.DLNA_PREFIX.equals(nodeItem.getPrefix())) {
                    mutableDevice.dlnaCaps = DLNACaps.valueOf(XMLUtil.getTextContent(nodeItem));
                }
            }
        }
    }

    public void hydrateIconList(MutableDevice mutableDevice, Node node) throws DescriptorBindingException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1 && Descriptor.Device.ELEMENT.icon.equals(nodeItem)) {
                MutableIcon mutableIcon = new MutableIcon();
                NodeList childNodes2 = nodeItem.getChildNodes();
                for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                    Node nodeItem2 = childNodes2.item(i2);
                    if (nodeItem2.getNodeType() == 1) {
                        if (Descriptor.Device.ELEMENT.width.equals(nodeItem2)) {
                            mutableIcon.width = Integer.valueOf(XMLUtil.getTextContent(nodeItem2)).intValue();
                        } else if (Descriptor.Device.ELEMENT.height.equals(nodeItem2)) {
                            mutableIcon.height = Integer.valueOf(XMLUtil.getTextContent(nodeItem2)).intValue();
                        } else if (Descriptor.Device.ELEMENT.depth.equals(nodeItem2)) {
                            mutableIcon.depth = Integer.valueOf(XMLUtil.getTextContent(nodeItem2)).intValue();
                        } else if (Descriptor.Device.ELEMENT.url.equals(nodeItem2)) {
                            mutableIcon.uri = parseURI(XMLUtil.getTextContent(nodeItem2));
                        } else if (Descriptor.Device.ELEMENT.mimetype.equals(nodeItem2)) {
                            mutableIcon.mimeType = XMLUtil.getTextContent(nodeItem2);
                        }
                    }
                }
                mutableDevice.icons.add(mutableIcon);
            }
        }
    }

    public void hydrateServiceList(MutableDevice mutableDevice, Node node) throws DescriptorBindingException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1 && Descriptor.Device.ELEMENT.service.equals(nodeItem)) {
                MutableService mutableService = new MutableService();
                NodeList childNodes2 = nodeItem.getChildNodes();
                for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                    Node nodeItem2 = childNodes2.item(i2);
                    if (nodeItem2.getNodeType() == 1) {
                        if (Descriptor.Device.ELEMENT.serviceType.equals(nodeItem2)) {
                            mutableService.serviceType = ServiceType.valueOf(XMLUtil.getTextContent(nodeItem2));
                        } else if (Descriptor.Device.ELEMENT.serviceId.equals(nodeItem2)) {
                            mutableService.serviceId = ServiceId.valueOf(XMLUtil.getTextContent(nodeItem2));
                        } else if (Descriptor.Device.ELEMENT.SCPDURL.equals(nodeItem2)) {
                            mutableService.descriptorURI = parseURI(XMLUtil.getTextContent(nodeItem2));
                        } else if (Descriptor.Device.ELEMENT.controlURL.equals(nodeItem2)) {
                            mutableService.controlURI = parseURI(XMLUtil.getTextContent(nodeItem2));
                        } else if (Descriptor.Device.ELEMENT.eventSubURL.equals(nodeItem2)) {
                            mutableService.eventSubscriptionURI = parseURI(XMLUtil.getTextContent(nodeItem2));
                        }
                    }
                }
                mutableDevice.services.add(mutableService);
            }
        }
    }

    public void hydrateDeviceList(MutableDevice mutableDevice, Node node) throws DescriptorBindingException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1 && Descriptor.Device.ELEMENT.device.equals(nodeItem)) {
                MutableDevice mutableDevice2 = new MutableDevice();
                mutableDevice2.parentDevice = mutableDevice;
                mutableDevice.embeddedDevices.add(mutableDevice2);
                hydrateDevice(mutableDevice2, nodeItem);
            }
        }
    }

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public String generate(Device device, ControlPointInfo controlPointInfo, Namespace namespace) throws DescriptorBindingException {
        try {
            log.fine("Generating XML descriptor from device model: " + device);
            return XMLUtil.documentToString(buildDOM(device, controlPointInfo, namespace));
        } catch (Exception e) {
            throw new DescriptorBindingException("Could not build DOM: " + e.getMessage(), e);
        }
    }

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public Document buildDOM(Device device, ControlPointInfo controlPointInfo, Namespace namespace) throws DescriptorBindingException {
        try {
            log.fine("Generating DOM from device model: " + device);
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setNamespaceAware(true);
            Document documentNewDocument = documentBuilderFactoryNewInstance.newDocumentBuilder().newDocument();
            generateRoot(namespace, device, documentNewDocument, controlPointInfo);
            return documentNewDocument;
        } catch (Exception e) {
            throw new DescriptorBindingException("Could not generate device descriptor: " + e.getMessage(), e);
        }
    }

    protected void generateRoot(Namespace namespace, Device device, Document document, ControlPointInfo controlPointInfo) {
        Element elementCreateElementNS = document.createElementNS(Descriptor.Device.NAMESPACE_URI, Descriptor.Device.ELEMENT.root.toString());
        document.appendChild(elementCreateElementNS);
        generateSpecVersion(namespace, device, document, elementCreateElementNS);
        generateDevice(namespace, device, document, elementCreateElementNS, controlPointInfo);
    }

    protected void generateSpecVersion(Namespace namespace, Device device, Document document, Element element) {
        Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, Descriptor.Device.ELEMENT.specVersion);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.major, Integer.valueOf(device.getVersion().getMajor()));
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.minor, Integer.valueOf(device.getVersion().getMinor()));
    }

    protected void generateDevice(Namespace namespace, Device device, Document document, Element element, ControlPointInfo controlPointInfo) {
        Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, Descriptor.Device.ELEMENT.device);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.deviceType, device.getType());
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.UDN, device.getIdentity().getUdn());
        DeviceDetails details = device.getDetails(controlPointInfo);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.friendlyName, details.getFriendlyName());
        if (details.getManufacturerDetails() != null) {
            XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.manufacturer, details.getManufacturerDetails().getManufacturer());
            XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.manufacturerURL, details.getManufacturerDetails().getManufacturerURI());
        }
        if (details.getModelDetails() != null) {
            XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.modelDescription, details.getModelDetails().getModelDescription());
            XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.modelName, details.getModelDetails().getModelName());
            XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.modelNumber, details.getModelDetails().getModelNumber());
            XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.modelURL, details.getModelDetails().getModelURI());
        }
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.serialNumber, details.getSerialNumber());
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.presentationURL, details.getPresentationURI());
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, Descriptor.Device.ELEMENT.UPC, details.getUpc());
        if (details.getDlnaDocs() != null) {
            for (DLNADoc dLNADoc : details.getDlnaDocs()) {
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "dlna:" + Descriptor.Device.ELEMENT.X_DLNADOC, dLNADoc, Descriptor.Device.DLNA_NAMESPACE_URI);
            }
        }
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "dlna:" + Descriptor.Device.ELEMENT.X_DLNACAP, details.getDlnaCaps(), Descriptor.Device.DLNA_NAMESPACE_URI);
        generateIconList(namespace, device, document, elementAppendNewElement);
        generateServiceList(namespace, device, document, elementAppendNewElement);
        generateDeviceList(namespace, device, document, elementAppendNewElement, controlPointInfo);
    }

    protected void generateIconList(Namespace namespace, Device device, Document document, Element element) {
        if (device.hasIcons()) {
            Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, Descriptor.Device.ELEMENT.iconList);
            for (Icon icon : device.getIcons()) {
                Element elementAppendNewElement2 = XMLUtil.appendNewElement(document, elementAppendNewElement, Descriptor.Device.ELEMENT.icon);
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.mimetype, icon.getMimeType());
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.width, Integer.valueOf(icon.getWidth()));
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.height, Integer.valueOf(icon.getHeight()));
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.depth, Integer.valueOf(icon.getDepth()));
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.url, icon.getUri());
            }
        }
    }

    protected void generateServiceList(Namespace namespace, Device device, Document document, Element element) {
        if (device.hasServices()) {
            Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, Descriptor.Device.ELEMENT.serviceList);
            for (Service service : device.getServices()) {
                Element elementAppendNewElement2 = XMLUtil.appendNewElement(document, elementAppendNewElement, Descriptor.Device.ELEMENT.service);
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.serviceType, service.getServiceType());
                XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.serviceId, service.getServiceId());
                if (service instanceof RemoteService) {
                    RemoteService remoteService = (RemoteService) service;
                    XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.controlURL, remoteService.getControlURI());
                    XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.eventSubURL, remoteService.getEventSubscriptionURI());
                    XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.SCPDURL, remoteService.getDescriptorURI());
                } else if (service instanceof LocalService) {
                    LocalService localService = (LocalService) service;
                    XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.controlURL, namespace.getControlPath(localService));
                    XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.eventSubURL, namespace.getEventSubscriptionPath(localService));
                    XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement2, Descriptor.Device.ELEMENT.SCPDURL, namespace.getDescriptorPath(localService));
                }
            }
        }
    }

    protected void generateDeviceList(Namespace namespace, Device device, Document document, Element element, ControlPointInfo controlPointInfo) {
        if (device.hasEmbeddedDevices()) {
            Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, Descriptor.Device.ELEMENT.deviceList);
            for (Device device2 : device.getEmbeddedDevices()) {
                generateDevice(namespace, device2, document, elementAppendNewElement, controlPointInfo);
            }
        }
    }

    protected static URI parseURI(String str) {
        if (str.startsWith("www.")) {
            str = "http://" + str;
        }
        try {
            return URI.create(str);
        } catch (IllegalArgumentException e) {
            log.fine("Illegal URI, trying with ./ prefix: " + Exceptions.unwrap(e));
            try {
                return URI.create("./" + str);
            } catch (IllegalArgumentException e2) {
                log.warning("Illegal URI '" + str + "', ignoring value: " + Exceptions.unwrap(e2));
                return null;
            }
        }
    }
}

