package org.teleal.common.xhtml;

import javax.xml.xpath.XPath;
import org.teleal.common.xhtml.XHTML;
import org.teleal.common.xml.DOMElement;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class Head extends XHTMLElement {
    public Head(XPath xPath, Element element) {
        super(xPath, element);
    }

    public XHTMLElement getHeadTitle() {
        return (XHTMLElement) this.CHILD_BUILDER.firstChildOrNull(XHTML.ELEMENT.title.name());
    }

    public Link[] getLinks() {
        return (Link[]) new DOMElement<XHTMLElement, XHTMLElement>.ArrayBuilder<Link>(this) { // from class: org.teleal.common.xhtml.Head.1
            @Override // org.teleal.common.xml.DOMElement.Builder
            public Link build(Element element) {
                return new Link(Head.this.getXpath(), element);
            }

            @Override // org.teleal.common.xml.DOMElement.ArrayBuilder
            public Link[] newChildrenArray(int i) {
                return new Link[i];
            }
        }.getChildElements(XHTML.ELEMENT.link.name());
    }

    public Meta[] getMetas() {
        return (Meta[]) new DOMElement<XHTMLElement, XHTMLElement>.ArrayBuilder<Meta>(this) { // from class: org.teleal.common.xhtml.Head.2
            @Override // org.teleal.common.xml.DOMElement.Builder
            public Meta build(Element element) {
                return new Meta(Head.this.getXpath(), element);
            }

            @Override // org.teleal.common.xml.DOMElement.ArrayBuilder
            public Meta[] newChildrenArray(int i) {
                return new Meta[i];
            }
        }.getChildElements(XHTML.ELEMENT.meta.name());
    }

    public XHTMLElement[] getDocumentStyles() {
        return (XHTMLElement[]) this.CHILD_BUILDER.getChildElements(XHTML.ELEMENT.style.name());
    }

    public XHTMLElement[] getScripts() {
        return (XHTMLElement[]) this.CHILD_BUILDER.getChildElements(XHTML.ELEMENT.script.name());
    }
}

