package org.teleal.common.xhtml;

import javax.xml.xpath.XPath;
import org.teleal.common.xhtml.XHTML;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class Link extends XHTMLElement {
    public Link(XPath xPath, Element element) {
        super(xPath, element);
    }

    public Href getHref() {
        return Href.fromString(getAttribute(XHTML.ATTR.href));
    }

    public String getRel() {
        return getAttribute(XHTML.ATTR.rel);
    }

    public String getRev() {
        return getAttribute(XHTML.ATTR.rev);
    }
}

