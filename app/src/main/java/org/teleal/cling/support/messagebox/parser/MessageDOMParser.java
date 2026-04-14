package org.teleal.cling.support.messagebox.parser;

import javax.xml.xpath.XPath;
import org.teleal.common.xml.DOMParser;
import org.teleal.common.xml.NamespaceContextMap;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public class MessageDOMParser extends DOMParser<MessageDOM> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.teleal.common.xml.DOMParser
    public MessageDOM createDOM(Document document) {
        return new MessageDOM(document);
    }

    public NamespaceContextMap createDefaultNamespaceContext(String... strArr) {
        NamespaceContextMap namespaceContextMap = new NamespaceContextMap() { // from class: org.teleal.cling.support.messagebox.parser.MessageDOMParser.1
            @Override // org.teleal.common.xml.NamespaceContextMap
            protected String getDefaultNamespaceURI() {
                return MessageDOM.NAMESPACE_URI;
            }
        };
        for (String str : strArr) {
            namespaceContextMap.put(str, MessageDOM.NAMESPACE_URI);
        }
        return namespaceContextMap;
    }

    public XPath createXPath() {
        return super.createXPath(createDefaultNamespaceContext(MessageElement.XPATH_PREFIX));
    }
}

