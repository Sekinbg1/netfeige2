package org.teleal.common.xhtml;

import javax.xml.xpath.XPath;
import org.teleal.common.xhtml.XHTML;
import org.teleal.common.xml.DOMElement;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class Root extends XHTMLElement {
    public Root(XPath xPath, Element element) {
        super(xPath, element);
    }

    public Head getHead() {
        return (Head) new DOMElement<XHTMLElement, XHTMLElement>.Builder<Head>(this) { // from class: org.teleal.common.xhtml.Root.1
            @Override // org.teleal.common.xml.DOMElement.Builder
            public Head build(Element element) {
                return new Head(Root.this.getXpath(), element);
            }
        }.firstChildOrNull(XHTML.ELEMENT.head.name());
    }

    public Body getBody() {
        return (Body) new DOMElement<XHTMLElement, XHTMLElement>.Builder<Body>(this) { // from class: org.teleal.common.xhtml.Root.2
            @Override // org.teleal.common.xml.DOMElement.Builder
            public Body build(Element element) {
                return new Body(Root.this.getXpath(), element);
            }
        }.firstChildOrNull(XHTML.ELEMENT.body.name());
    }
}

