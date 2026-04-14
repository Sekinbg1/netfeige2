package org.teleal.cling.support.messagebox.parser;

import javax.xml.xpath.XPath;
import org.teleal.common.xml.DOMElement;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class MessageElement extends DOMElement<MessageElement, MessageElement> {
    public static final String XPATH_PREFIX = "m";

    public MessageElement(XPath xPath, Element element) {
        super(xPath, element);
    }

    @Override // org.teleal.common.xml.DOMElement
    protected String prefix(String str) {
        return "m:" + str;
    }

    @Override // org.teleal.common.xml.DOMElement
    protected DOMElement<MessageElement, MessageElement>.Builder<MessageElement> createParentBuilder(DOMElement dOMElement) {
        return new DOMElement<MessageElement, MessageElement>.Builder<MessageElement>(dOMElement) { // from class: org.teleal.cling.support.messagebox.parser.MessageElement.1
            @Override // org.teleal.common.xml.DOMElement.Builder
            public MessageElement build(Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }

    @Override // org.teleal.common.xml.DOMElement
    protected DOMElement<MessageElement, MessageElement>.ArrayBuilder<MessageElement> createChildBuilder(DOMElement dOMElement) {
        return new DOMElement<MessageElement, MessageElement>.ArrayBuilder<MessageElement>(dOMElement) { // from class: org.teleal.cling.support.messagebox.parser.MessageElement.2
            @Override // org.teleal.common.xml.DOMElement.ArrayBuilder
            public MessageElement[] newChildrenArray(int i) {
                return new MessageElement[i];
            }

            @Override // org.teleal.common.xml.DOMElement.Builder
            public MessageElement build(Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }
}

