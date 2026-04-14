package org.teleal.cling.transport.impl;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.teleal.cling.model.Constants;
import org.teleal.cling.model.XMLUtil;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.gena.IncomingEventRequestMessage;
import org.teleal.cling.model.message.gena.OutgoingEventRequestMessage;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.transport.spi.GENAEventProcessor;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: classes.dex */
public class GENAEventProcessorImpl implements GENAEventProcessor {
    private static Logger log = Logger.getLogger(GENAEventProcessor.class.getName());

    @Override // org.teleal.cling.transport.spi.GENAEventProcessor
    public void writeBody(OutgoingEventRequestMessage outgoingEventRequestMessage) throws UnsupportedDataException {
        log.fine("Writing body of: " + outgoingEventRequestMessage);
        try {
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setNamespaceAware(true);
            Document documentNewDocument = documentBuilderFactoryNewInstance.newDocumentBuilder().newDocument();
            writeProperties(documentNewDocument, writePropertysetElement(documentNewDocument), outgoingEventRequestMessage);
            outgoingEventRequestMessage.setBody(UpnpMessage.BodyType.STRING, toString(documentNewDocument));
            if (log.isLoggable(Level.FINER)) {
                log.finer("===================================== GENA BODY BEGIN ============================================");
                log.finer(outgoingEventRequestMessage.getBody().toString());
                log.finer("-===================================== GENA BODY END ============================================");
            }
        } catch (Exception e) {
            throw new UnsupportedDataException("Can't transform message payload: " + e.getMessage(), e);
        }
    }

    @Override // org.teleal.cling.transport.spi.GENAEventProcessor
    public void readBody(IncomingEventRequestMessage incomingEventRequestMessage) throws UnsupportedDataException {
        log.fine("Reading body of: " + incomingEventRequestMessage);
        if (log.isLoggable(Level.FINER)) {
            log.finer("===================================== GENA BODY BEGIN ============================================");
            log.finer(incomingEventRequestMessage.getBody().toString());
            log.finer("-===================================== GENA BODY END ============================================");
        }
        if (incomingEventRequestMessage.getBody() == null || !incomingEventRequestMessage.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
            throw new UnsupportedDataException("Can't transform null or non-string body of: " + incomingEventRequestMessage);
        }
        try {
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setNamespaceAware(true);
            readProperties(readPropertysetElement(documentBuilderFactoryNewInstance.newDocumentBuilder().parse(new InputSource(new StringReader(incomingEventRequestMessage.getBodyString().trim())))), incomingEventRequestMessage);
        } catch (Exception e) {
            throw new UnsupportedDataException("Can't transform message payload: " + e.getMessage(), e);
        }
    }

    protected Element writePropertysetElement(Document document) {
        Element elementCreateElementNS = document.createElementNS(Constants.NS_UPNP_EVENT_10, "e:propertyset");
        document.appendChild(elementCreateElementNS);
        return elementCreateElementNS;
    }

    protected Element readPropertysetElement(Document document) {
        Element documentElement = document.getDocumentElement();
        if (documentElement == null || !getUnprefixedNodeName(documentElement).equals("propertyset")) {
            throw new RuntimeException("Root element was not 'propertyset'");
        }
        return documentElement;
    }

    protected void writeProperties(Document document, Element element, OutgoingEventRequestMessage outgoingEventRequestMessage) {
        for (StateVariableValue stateVariableValue : outgoingEventRequestMessage.getStateVariableValues()) {
            Element elementCreateElementNS = document.createElementNS(Constants.NS_UPNP_EVENT_10, "e:property");
            element.appendChild(elementCreateElementNS);
            XMLUtil.appendNewElement(document, elementCreateElementNS, stateVariableValue.getStateVariable().getName(), stateVariableValue.toString());
        }
    }

    protected void readProperties(Element element, IncomingEventRequestMessage incomingEventRequestMessage) {
        NodeList childNodes = element.getChildNodes();
        StateVariable<RemoteService>[] stateVariables = incomingEventRequestMessage.getService().getStateVariables();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1 && getUnprefixedNodeName(nodeItem).equals("property")) {
                NodeList childNodes2 = nodeItem.getChildNodes();
                for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                    Node nodeItem2 = childNodes2.item(i2);
                    if (nodeItem2.getNodeType() == 1) {
                        String unprefixedNodeName = getUnprefixedNodeName(nodeItem2);
                        int length = stateVariables.length;
                        int i3 = 0;
                        while (true) {
                            if (i3 < length) {
                                StateVariable<RemoteService> stateVariable = stateVariables[i3];
                                if (stateVariable.getName().equals(unprefixedNodeName)) {
                                    log.fine("Reading state variable value: " + unprefixedNodeName);
                                    incomingEventRequestMessage.getStateVariableValues().add(new StateVariableValue(stateVariable, XMLUtil.getTextContent(nodeItem2)));
                                    break;
                                }
                                i3++;
                            }
                        }
                    }
                }
            }
        }
    }

    protected String toString(Document document) throws Exception {
        String strDocumentToString = XMLUtil.documentToString(document);
        while (true) {
            if (!strDocumentToString.endsWith("\n") && !strDocumentToString.endsWith("\r")) {
                return strDocumentToString;
            }
            strDocumentToString = strDocumentToString.substring(0, strDocumentToString.length() - 1);
        }
    }

    protected String getUnprefixedNodeName(Node node) {
        return node.getPrefix() != null ? node.getNodeName().substring(node.getPrefix().length() + 1) : node.getNodeName();
    }
}

