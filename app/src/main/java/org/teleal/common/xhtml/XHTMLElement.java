package org.teleal.common.xhtml;

import java.util.ArrayList;
import javax.xml.xpath.XPath;
import org.teleal.common.xhtml.XHTML;
import org.teleal.common.xml.DOMElement;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class XHTMLElement extends DOMElement<XHTMLElement, XHTMLElement> {
    public static final String XPATH_PREFIX = "h";

    public XHTMLElement(XPath xPath, Element element) {
        super(xPath, element);
    }

    @Override // org.teleal.common.xml.DOMElement
    protected DOMElement<XHTMLElement, XHTMLElement>.Builder<XHTMLElement> createParentBuilder(DOMElement dOMElement) {
        return new DOMElement<XHTMLElement, XHTMLElement>.Builder<XHTMLElement>(dOMElement) { // from class: org.teleal.common.xhtml.XHTMLElement.1
            @Override // org.teleal.common.xml.DOMElement.Builder
            public XHTMLElement build(Element element) {
                return new XHTMLElement(XHTMLElement.this.getXpath(), element);
            }
        };
    }

    @Override // org.teleal.common.xml.DOMElement
    protected DOMElement<XHTMLElement, XHTMLElement>.ArrayBuilder<XHTMLElement> createChildBuilder(DOMElement dOMElement) {
        return new DOMElement<XHTMLElement, XHTMLElement>.ArrayBuilder<XHTMLElement>(dOMElement) { // from class: org.teleal.common.xhtml.XHTMLElement.2
            @Override // org.teleal.common.xml.DOMElement.ArrayBuilder
            public XHTMLElement[] newChildrenArray(int i) {
                return new XHTMLElement[i];
            }

            @Override // org.teleal.common.xml.DOMElement.Builder
            public XHTMLElement build(Element element) {
                return new XHTMLElement(XHTMLElement.this.getXpath(), element);
            }
        };
    }

    @Override // org.teleal.common.xml.DOMElement
    protected String prefix(String str) {
        return "h:" + str;
    }

    public XHTML.ELEMENT getConstant() {
        return XHTML.ELEMENT.valueOf(getElementName());
    }

    public XHTMLElement[] getChildren(XHTML.ELEMENT element) {
        return (XHTMLElement[]) super.getChildren(element.name());
    }

    public XHTMLElement getFirstChild(XHTML.ELEMENT element) {
        return (XHTMLElement) super.getFirstChild(element.name());
    }

    public XHTMLElement[] findChildren(XHTML.ELEMENT element) {
        return (XHTMLElement[]) super.findChildren(element.name());
    }

    public XHTMLElement createChild(XHTML.ELEMENT element) {
        return (XHTMLElement) super.createChild(element.name(), XHTML.NAMESPACE_URI);
    }

    public String getAttribute(XHTML.ATTR attr) {
        return getAttribute(attr.name());
    }

    public XHTMLElement setAttribute(XHTML.ATTR attr, String str) {
        super.setAttribute(attr.name(), str);
        return this;
    }

    public String getId() {
        return getAttribute(XHTML.ATTR.id);
    }

    public XHTMLElement setId(String str) {
        setAttribute(XHTML.ATTR.id, str);
        return this;
    }

    public String getTitle() {
        return getAttribute(XHTML.ATTR.title);
    }

    public XHTMLElement setTitle(String str) {
        setAttribute(XHTML.ATTR.title, str);
        return this;
    }

    public XHTMLElement setClasses(String str) {
        setAttribute(XHTML.ATTR.CLASS, str);
        return this;
    }

    public XHTMLElement setClasses(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            sb.append(strArr[i]);
            if (i != strArr.length - 1) {
                sb.append(" ");
            }
        }
        setAttribute(XHTML.ATTR.CLASS, sb.toString());
        return this;
    }

    public String[] getClasses() {
        String attribute = getAttribute(XHTML.ATTR.CLASS);
        return attribute == null ? new String[0] : attribute.split(" ");
    }

    public Option[] getOptions() {
        return Option.fromString(getAttribute(XHTML.ATTR.style));
    }

    public Option getOption(String str) {
        for (Option option : getOptions()) {
            if (option.getKey().equals(str)) {
                return option;
            }
        }
        return null;
    }

    public Anchor[] findAllAnchors(String str) {
        return findAllAnchors(str, null);
    }

    public Anchor[] findAllAnchors(String str, String str2) {
        XHTMLElement[] xHTMLElementArrFindChildrenWithClass = findChildrenWithClass(XHTML.ELEMENT.a, str2);
        ArrayList arrayList = new ArrayList(xHTMLElementArrFindChildrenWithClass.length);
        for (XHTMLElement xHTMLElement : xHTMLElementArrFindChildrenWithClass) {
            String attribute = xHTMLElement.getAttribute(XHTML.ATTR.href);
            if (str == null || (attribute != null && attribute.startsWith(str))) {
                arrayList.add(new Anchor(getXpath(), xHTMLElement.getW3CElement()));
            }
        }
        return (Anchor[]) arrayList.toArray(new Anchor[arrayList.size()]);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public XHTMLElement[] findChildrenWithClass(XHTML.ELEMENT element, String str) {
        ArrayList arrayList = new ArrayList();
        for (XHTMLElement xHTMLElement : findChildren(element)) {
            if (str == null) {
                arrayList.add(xHTMLElement);
            } else {
                String[] classes = xHTMLElement.getClasses();
                int length = classes.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    if (classes[i].matches(str)) {
                        arrayList.add(xHTMLElement);
                        break;
                    }
                    i++;
                }
            }
        }
        return (XHTMLElement[]) arrayList.toArray(this.CHILD_BUILDER.newChildrenArray(arrayList.size()));
    }

    @Override // org.teleal.common.xml.DOMElement
    public XHTMLElement setContent(String str) {
        super.setContent(str);
        return this;
    }

    @Override // org.teleal.common.xml.DOMElement
    public XHTMLElement setAttribute(String str, String str2) {
        super.setAttribute(str, str2);
        return this;
    }
}

