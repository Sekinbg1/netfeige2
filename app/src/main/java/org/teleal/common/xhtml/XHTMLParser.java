package org.teleal.common.xhtml;

import java.util.HashSet;
import javax.xml.xpath.XPath;
import org.teleal.common.xhtml.XHTML;
import org.teleal.common.xml.DOMParser;
import org.teleal.common.xml.NamespaceContextMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: classes.dex */
public class XHTMLParser extends DOMParser<XHTML> {
    public XHTMLParser() {
        super(XHTML.createSchemaSources());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.teleal.common.xml.DOMParser
    public XHTML createDOM(Document document) {
        if (document != null) {
            return new XHTML(document);
        }
        return null;
    }

    public void checkDuplicateIdentifiers(XHTML xhtml) throws IllegalStateException {
        final HashSet hashSet = new HashSet();
        accept(xhtml.getW3CDocument().getDocumentElement(), new DOMParser.NodeVisitor((short) 1) { // from class: org.teleal.common.xhtml.XHTMLParser.1
            @Override // org.teleal.common.xml.DOMParser.NodeVisitor
            public void visit(Node node) {
                String attribute = ((Element) node).getAttribute(XHTML.ATTR.id.name());
                if ("".equals(attribute)) {
                    return;
                }
                if (hashSet.contains(attribute)) {
                    throw new IllegalStateException("Duplicate identifier, override/change value: " + attribute);
                }
                hashSet.add(attribute);
            }
        });
    }

    public NamespaceContextMap createDefaultNamespaceContext(String... strArr) {
        NamespaceContextMap namespaceContextMap = new NamespaceContextMap() { // from class: org.teleal.common.xhtml.XHTMLParser.2
            @Override // org.teleal.common.xml.NamespaceContextMap
            protected String getDefaultNamespaceURI() {
                return XHTML.NAMESPACE_URI;
            }
        };
        for (String str : strArr) {
            namespaceContextMap.put(str, XHTML.NAMESPACE_URI);
        }
        return namespaceContextMap;
    }

    public XPath createXPath() {
        return super.createXPath(createDefaultNamespaceContext(XHTMLElement.XPATH_PREFIX));
    }
}

