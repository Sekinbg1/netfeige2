package org.teleal.cling.support.contentdirectory;

import com.netfeige.common.Public_MsgID;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.XMLUtil;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.support.model.DIDLAttribute;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.DescMeta;
import org.teleal.cling.support.model.Person;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.StorageMedium;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;
import org.teleal.common.io.IO;
import org.teleal.common.util.Exceptions;
import org.teleal.common.xhtml.XHTML;
import org.teleal.common.xml.SAXParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class DIDLParser extends SAXParser {
    private static final Logger log = Logger.getLogger(DIDLParser.class.getName());

    public DIDLContent parseResource(String str) throws Exception {
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(str);
            return parse(IO.readLines(resourceAsStream));
        } finally {
            if (resourceAsStream != null) {
                resourceAsStream.close();
            }
        }
    }

    public DIDLContent parse(String str) throws Exception {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("Null or empty XML");
        }
        DIDLContent dIDLContent = new DIDLContent();
        createRootHandler(dIDLContent, this);
        log.fine("Parsing DIDL XML content");
        parse(new InputSource(new StringReader(str)));
        return dIDLContent;
    }

    protected RootHandler createRootHandler(DIDLContent dIDLContent, SAXParser sAXParser) {
        return new RootHandler(dIDLContent, sAXParser);
    }

    protected ContainerHandler createContainerHandler(Container container, SAXParser.Handler handler) {
        return new ContainerHandler(container, handler);
    }

    protected ItemHandler createItemHandler(Item item, SAXParser.Handler handler) {
        return new ItemHandler(item, handler);
    }

    protected ResHandler createResHandler(Res res, SAXParser.Handler handler) {
        return new ResHandler(res, handler);
    }

    protected DescMetaHandler createDescMetaHandler(DescMeta descMeta, SAXParser.Handler handler) {
        return new DescMetaHandler(descMeta, handler);
    }

    protected Container createContainer(Attributes attributes) {
        Container container = new Container();
        container.setId(attributes.getValue("id"));
        container.setParentID(attributes.getValue("parentID"));
        if (attributes.getValue("childCount") != null) {
            container.setChildCount(Integer.valueOf(attributes.getValue("childCount")));
        }
        try {
            Boolean bool = (Boolean) Datatype.Builtin.BOOLEAN.getDatatype().valueOf(attributes.getValue("restricted"));
            if (bool != null) {
                container.setRestricted(bool.booleanValue());
            }
            Boolean bool2 = (Boolean) Datatype.Builtin.BOOLEAN.getDatatype().valueOf(attributes.getValue("searchable"));
            if (bool2 != null) {
                container.setSearchable(bool2.booleanValue());
            }
        } catch (Exception unused) {
        }
        return container;
    }

    protected Item createItem(Attributes attributes) {
        Item item = new Item();
        item.setId(attributes.getValue("id"));
        item.setParentID(attributes.getValue("parentID"));
        try {
            Boolean bool = (Boolean) Datatype.Builtin.BOOLEAN.getDatatype().valueOf(attributes.getValue("restricted"));
            if (bool != null) {
                item.setRestricted(bool.booleanValue());
            }
        } catch (Exception unused) {
        }
        if (attributes.getValue("refID") != null) {
            item.setRefID(attributes.getValue("refID"));
        }
        return item;
    }

    protected Res createResource(Attributes attributes) {
        Res res = new Res();
        if (attributes.getValue("importUri") != null) {
            res.setImportUri(URI.create(attributes.getValue("importUri")));
        }
        try {
            res.setProtocolInfo(new ProtocolInfo(attributes.getValue("protocolInfo")));
            if (attributes.getValue("size") != null) {
                res.setSize(Long.valueOf(attributes.getValue("size")));
            }
            if (attributes.getValue("duration") != null) {
                res.setDuration(attributes.getValue("duration"));
            }
            if (attributes.getValue("bitrate") != null) {
                res.setBitrate(Long.valueOf(attributes.getValue("bitrate")));
            }
            if (attributes.getValue("sampleFrequency") != null) {
                res.setSampleFrequency(Long.valueOf(attributes.getValue("sampleFrequency")));
            }
            if (attributes.getValue("bitsPerSample") != null) {
                res.setBitsPerSample(Long.valueOf(attributes.getValue("bitsPerSample")));
            }
            if (attributes.getValue("nrAudioChannels") != null) {
                res.setNrAudioChannels(Long.valueOf(attributes.getValue("nrAudioChannels")));
            }
            if (attributes.getValue("colorDepth") != null) {
                res.setColorDepth(Long.valueOf(attributes.getValue("colorDepth")));
            }
            if (attributes.getValue("protection") != null) {
                res.setProtection(attributes.getValue("protection"));
            }
            if (attributes.getValue("resolution") != null) {
                res.setResolution(attributes.getValue("resolution"));
            }
            return res;
        } catch (InvalidValueException e) {
            log.warning("In DIDL content, invalid resource protocol info: " + Exceptions.unwrap(e));
            return null;
        }
    }

    protected DescMeta createDescMeta(Attributes attributes) {
        DescMeta descMeta = new DescMeta();
        descMeta.setId(attributes.getValue("id"));
        if (attributes.getValue(a.a) != null) {
            descMeta.setType(attributes.getValue(a.a));
        }
        if (attributes.getValue("nameSpace") != null) {
            descMeta.setNameSpace(URI.create(attributes.getValue("nameSpace")));
        }
        return descMeta;
    }

    public String generate(DIDLContent dIDLContent) throws Exception {
        return generate(dIDLContent, false);
    }

    public String generate(DIDLContent dIDLContent, boolean z) throws Exception {
        return documentToString(buildDOM(dIDLContent, z), true);
    }

    protected String documentToString(Document document, boolean z) throws Exception {
        Transformer transformerNewTransformer = TransformerFactory.newInstance().newTransformer();
        if (z) {
            transformerNewTransformer.setOutputProperty("omit-xml-declaration", "yes");
        }
        StringWriter stringWriter = new StringWriter();
        transformerNewTransformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    protected Document buildDOM(DIDLContent dIDLContent, boolean z) throws Exception {
        DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
        documentBuilderFactoryNewInstance.setNamespaceAware(true);
        Document documentNewDocument = documentBuilderFactoryNewInstance.newDocumentBuilder().newDocument();
        generateRoot(dIDLContent, documentNewDocument, z);
        return documentNewDocument;
    }

    protected void generateRoot(DIDLContent dIDLContent, Document document, boolean z) {
        Element elementCreateElementNS = document.createElementNS(DIDLContent.NAMESPACE_URI, "DIDL-Lite");
        document.appendChild(elementCreateElementNS);
        elementCreateElementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:upnp", DIDLObject.Property.UPNP.NAMESPACE.URI);
        elementCreateElementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", DIDLObject.Property.DC.NAMESPACE.URI);
        for (Container container : dIDLContent.getContainers()) {
            if (container != null) {
                generateContainer(container, document, elementCreateElementNS, z);
            }
        }
        for (Item item : dIDLContent.getItems()) {
            if (item != null) {
                generateItem(item, document, elementCreateElementNS);
            }
        }
        for (DescMeta descMeta : dIDLContent.getDescMetadata()) {
            if (descMeta != null) {
                generateDescMetadata(descMeta, document, elementCreateElementNS);
            }
        }
    }

    protected void generateContainer(Container container, Document document, Element element, boolean z) {
        if (container.getTitle() == null) {
            throw new RuntimeException("Missing 'dc:title' element for container: " + container.getId());
        }
        if (container.getClazz() == null) {
            throw new RuntimeException("Missing 'upnp:class' element for container: " + container.getId());
        }
        Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, "container");
        if (container.getId() == null) {
            throw new NullPointerException("Missing id on container: " + container);
        }
        elementAppendNewElement.setAttribute("id", container.getId());
        if (container.getParentID() == null) {
            throw new NullPointerException("Missing parent id on container: " + container);
        }
        elementAppendNewElement.setAttribute("parentID", container.getParentID());
        if (container.getChildCount() != null) {
            elementAppendNewElement.setAttribute("childCount", Integer.toString(container.getChildCount().intValue()));
        }
        elementAppendNewElement.setAttribute("restricted", Boolean.toString(container.isRestricted()));
        elementAppendNewElement.setAttribute("searchable", Boolean.toString(container.isSearchable()));
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "dc:title", container.getTitle(), DIDLObject.Property.DC.NAMESPACE.URI);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "dc:creator", container.getCreator(), DIDLObject.Property.DC.NAMESPACE.URI);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "upnp:writeStatus", container.getWriteStatus(), DIDLObject.Property.UPNP.NAMESPACE.URI);
        appendClass(document, elementAppendNewElement, container.getClazz(), "upnp:class", false);
        Iterator<DIDLObject.Class> it = container.getSearchClasses().iterator();
        while (it.hasNext()) {
            appendClass(document, elementAppendNewElement, it.next(), "upnp:searchClass", true);
        }
        Iterator<DIDLObject.Class> it2 = container.getCreateClasses().iterator();
        while (it2.hasNext()) {
            appendClass(document, elementAppendNewElement, it2.next(), "upnp:createClass", true);
        }
        appendProperties(document, elementAppendNewElement, container, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, DIDLObject.Property.UPNP.NAMESPACE.URI);
        appendProperties(document, elementAppendNewElement, container, "dc", DIDLObject.Property.DC.NAMESPACE.class, DIDLObject.Property.DC.NAMESPACE.URI);
        if (z) {
            for (Item item : container.getItems()) {
                if (item != null) {
                    generateItem(item, document, elementAppendNewElement);
                }
            }
        }
        for (Res res : container.getResources()) {
            if (res != null) {
                generateResource(res, document, elementAppendNewElement);
            }
        }
        for (DescMeta descMeta : container.getDescMetadata()) {
            if (descMeta != null) {
                generateDescMetadata(descMeta, document, elementAppendNewElement);
            }
        }
    }

    protected void generateItem(Item item, Document document, Element element) {
        if (item.getTitle() == null) {
            throw new RuntimeException("Missing 'dc:title' element for item: " + item.getId());
        }
        if (item.getClazz() == null) {
            throw new RuntimeException("Missing 'upnp:class' element for item: " + item.getId());
        }
        Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, "item");
        if (item.getId() == null) {
            throw new NullPointerException("Missing id on item: " + item);
        }
        elementAppendNewElement.setAttribute("id", item.getId());
        if (item.getParentID() == null) {
            throw new NullPointerException("Missing parent id on item: " + item);
        }
        elementAppendNewElement.setAttribute("parentID", item.getParentID());
        if (item.getRefID() != null) {
            elementAppendNewElement.setAttribute("refID", item.getRefID());
        }
        elementAppendNewElement.setAttribute("restricted", Boolean.toString(item.isRestricted()));
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "dc:title", item.getTitle(), DIDLObject.Property.DC.NAMESPACE.URI);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "dc:creator", item.getCreator(), DIDLObject.Property.DC.NAMESPACE.URI);
        XMLUtil.appendNewElementIfNotNull(document, elementAppendNewElement, "upnp:writeStatus", item.getWriteStatus(), DIDLObject.Property.UPNP.NAMESPACE.URI);
        appendClass(document, elementAppendNewElement, item.getClazz(), "upnp:class", false);
        appendProperties(document, elementAppendNewElement, item, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, DIDLObject.Property.UPNP.NAMESPACE.URI);
        appendProperties(document, elementAppendNewElement, item, "dc", DIDLObject.Property.DC.NAMESPACE.class, DIDLObject.Property.DC.NAMESPACE.URI);
        for (Res res : item.getResources()) {
            if (res != null) {
                generateResource(res, document, elementAppendNewElement);
            }
        }
        for (DescMeta descMeta : item.getDescMetadata()) {
            if (descMeta != null) {
                generateDescMetadata(descMeta, document, elementAppendNewElement);
            }
        }
    }

    protected void generateResource(Res res, Document document, Element element) {
        if (res.getValue() == null) {
            throw new RuntimeException("Missing resource URI value" + res);
        }
        if (res.getProtocolInfo() == null) {
            throw new RuntimeException("Missing resource protocol info: " + res);
        }
        Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, "res", res.getValue());
        elementAppendNewElement.setAttribute("protocolInfo", res.getProtocolInfo().toString());
        if (res.getImportUri() != null) {
            elementAppendNewElement.setAttribute("importUri", res.getImportUri().toString());
        }
        if (res.getSize() != null) {
            elementAppendNewElement.setAttribute("size", res.getSize().toString());
        }
        if (res.getDuration() != null) {
            elementAppendNewElement.setAttribute("duration", res.getDuration());
        }
        if (res.getBitrate() != null) {
            elementAppendNewElement.setAttribute("bitrate", res.getBitrate().toString());
        }
        if (res.getSampleFrequency() != null) {
            elementAppendNewElement.setAttribute("sampleFrequency", res.getSampleFrequency().toString());
        }
        if (res.getBitsPerSample() != null) {
            elementAppendNewElement.setAttribute("bitsPerSample", res.getBitsPerSample().toString());
        }
        if (res.getNrAudioChannels() != null) {
            elementAppendNewElement.setAttribute("nrAudioChannels", res.getNrAudioChannels().toString());
        }
        if (res.getColorDepth() != null) {
            elementAppendNewElement.setAttribute("colorDepth", res.getColorDepth().toString());
        }
        if (res.getProtection() != null) {
            elementAppendNewElement.setAttribute("protection", res.getProtection());
        }
        if (res.getResolution() != null) {
            elementAppendNewElement.setAttribute("resolution", res.getResolution());
        }
    }

    protected void generateDescMetadata(DescMeta descMeta, Document document, Element element) {
        if (descMeta.getId() == null) {
            throw new RuntimeException("Missing id of description metadata: " + descMeta);
        }
        if (descMeta.getNameSpace() == null) {
            throw new RuntimeException("Missing namespace of description metadata: " + descMeta);
        }
        Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, "desc");
        elementAppendNewElement.setAttribute("id", descMeta.getId());
        elementAppendNewElement.setAttribute("nameSpace", descMeta.getNameSpace().toString());
        if (descMeta.getType() != null) {
            elementAppendNewElement.setAttribute(a.a, descMeta.getType());
        }
        populateDescMetadata(elementAppendNewElement, descMeta);
    }

    protected void populateDescMetadata(Element element, DescMeta descMeta) {
        if (descMeta.getMetadata() instanceof Document) {
            NodeList childNodes = ((Document) descMeta.getMetadata()).getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node nodeItem = childNodes.item(i);
                if (nodeItem.getNodeType() == 1) {
                    element.appendChild(element.getOwnerDocument().importNode(nodeItem, true));
                }
            }
            return;
        }
        log.warning("Unknown desc metadata content, please override populateDescMetadata(): " + descMeta.getMetadata());
    }

    protected void appendProperties(Document document, Element element, DIDLObject dIDLObject, String str, Class<? extends DIDLObject.Property.NAMESPACE> cls, String str2) {
        for (DIDLObject.Property property : dIDLObject.getPropertiesByNamespace(cls)) {
            Element elementCreateElementNS = document.createElementNS(str2, str + Public_MsgID.PRO_SPACE + property.getDescriptorName());
            element.appendChild(elementCreateElementNS);
            property.setOnElement(elementCreateElementNS);
        }
    }

    protected void appendClass(Document document, Element element, DIDLObject.Class r5, String str, boolean z) {
        Element elementAppendNewElementIfNotNull = XMLUtil.appendNewElementIfNotNull(document, element, str, r5.getValue(), DIDLObject.Property.UPNP.NAMESPACE.URI);
        if (r5.getFriendlyName() != null && r5.getFriendlyName().length() > 0) {
            elementAppendNewElementIfNotNull.setAttribute("name", r5.getFriendlyName());
        }
        if (z) {
            elementAppendNewElementIfNotNull.setAttribute("includeDerived", Boolean.toString(r5.isIncludeDerived()));
        }
    }

    public void debugXML(String str) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("-------------------------------------------------------------------------------------");
            log.fine("\n" + str);
            log.fine("-------------------------------------------------------------------------------------");
        }
    }

    public abstract class DIDLObjectHandler<I extends DIDLObject> extends SAXParser.Handler<I> {
        protected DIDLObjectHandler(I i, SAXParser.Handler handler) {
            super(i, handler);
        }

        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String str, String str2, String str3) throws SAXException {
            super.endElement(str, str2, str3);
            if (DIDLObject.Property.DC.NAMESPACE.URI.equals(str)) {
                if ("title".equals(str2)) {
                    getInstance().setTitle(getCharacters());
                    return;
                }
                if ("creator".equals(str2)) {
                    getInstance().setCreator(getCharacters());
                    return;
                }
                if ("description".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.DESCRIPTION(getCharacters()));
                    return;
                }
                if ("publisher".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.PUBLISHER(new Person(getCharacters())));
                    return;
                }
                if ("contributor".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.CONTRIBUTOR(new Person(getCharacters())));
                    return;
                }
                if ("date".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.DATE(getCharacters()));
                    return;
                }
                if ("language".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.LANGUAGE(getCharacters()));
                    return;
                } else if ("rights".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.RIGHTS(getCharacters()));
                    return;
                } else {
                    if ("relation".equals(str2)) {
                        getInstance().addProperty(new DIDLObject.Property.DC.RELATION(URI.create(getCharacters())));
                        return;
                    }
                    return;
                }
            }
            if (DIDLObject.Property.UPNP.NAMESPACE.URI.equals(str)) {
                if ("writeStatus".equals(str2)) {
                    try {
                        getInstance().setWriteStatus(WriteStatus.valueOf(getCharacters()));
                        return;
                    } catch (Exception unused) {
                        DIDLParser.log.info("Ignoring invalid writeStatus value: " + getCharacters());
                        return;
                    }
                }
                if (XHTML.ATTR.CLASS.equals(str2)) {
                    getInstance().setClazz(new DIDLObject.Class(getCharacters(), getAttributes().getValue("name")));
                    return;
                }
                if ("artist".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.ARTIST(new PersonWithRole(getCharacters(), getAttributes().getValue("role"))));
                    return;
                }
                if ("actor".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.ACTOR(new PersonWithRole(getCharacters(), getAttributes().getValue("role"))));
                    return;
                }
                if ("author".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.AUTHOR(new PersonWithRole(getCharacters(), getAttributes().getValue("role"))));
                    return;
                }
                if ("producer".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.PRODUCER(new Person(getCharacters())));
                    return;
                }
                if ("director".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.DIRECTOR(new Person(getCharacters())));
                    return;
                }
                if ("longDescription".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.LONG_DESCRIPTION(getCharacters()));
                    return;
                }
                if ("storageUsed".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_USED(Long.valueOf(getCharacters())));
                    return;
                }
                if ("storageTotal".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_TOTAL(Long.valueOf(getCharacters())));
                    return;
                }
                if ("storageFree".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_FREE(Long.valueOf(getCharacters())));
                    return;
                }
                if ("storageMaxPartition".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION(Long.valueOf(getCharacters())));
                    return;
                }
                if ("storageMedium".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_MEDIUM(StorageMedium.valueOrVendorSpecificOf(getCharacters())));
                    return;
                }
                if ("genre".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.GENRE(getCharacters()));
                    return;
                }
                if ("album".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.ALBUM(getCharacters()));
                    return;
                }
                if ("playlist".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.PLAYLIST(getCharacters()));
                    return;
                }
                if ("region".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.REGION(getCharacters()));
                    return;
                }
                if ("rating".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.RATING(getCharacters()));
                    return;
                }
                if ("toc".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.TOC(getCharacters()));
                    return;
                }
                if ("albumArtURI".equals(str2)) {
                    DIDLObject.Property.UPNP.ALBUM_ART_URI album_art_uri = new DIDLObject.Property.UPNP.ALBUM_ART_URI(URI.create(getCharacters()));
                    Attributes attributes = getAttributes();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if ("profileID".equals(attributes.getLocalName(i))) {
                            album_art_uri.addAttribute(new DIDLObject.Property.DLNA.PROFILE_ID(new DIDLAttribute(DIDLObject.Property.DLNA.NAMESPACE.URI, Descriptor.Device.DLNA_PREFIX, attributes.getValue(i))));
                        }
                    }
                    getInstance().addProperty(album_art_uri);
                    return;
                }
                if ("artistDiscographyURI".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.ARTIST_DISCO_URI(URI.create(getCharacters())));
                    return;
                }
                if ("lyricsURI".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.LYRICS_URI(URI.create(getCharacters())));
                    return;
                }
                if ("icon".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.ICON(URI.create(getCharacters())));
                    return;
                }
                if ("radioCallSign".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.RADIO_CALL_SIGN(getCharacters()));
                    return;
                }
                if ("radioStationID".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.RADIO_STATION_ID(getCharacters()));
                    return;
                }
                if ("radioBand".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.RADIO_BAND(getCharacters()));
                    return;
                }
                if ("channelNr".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.CHANNEL_NR(Integer.valueOf(getCharacters())));
                    return;
                }
                if ("channelName".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.CHANNEL_NAME(getCharacters()));
                    return;
                }
                if ("scheduledStartTime".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.SCHEDULED_START_TIME(getCharacters()));
                    return;
                }
                if ("scheduledEndTime".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.SCHEDULED_END_TIME(getCharacters()));
                    return;
                }
                if ("DVDRegionCode".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.DVD_REGION_CODE(Integer.valueOf(getCharacters())));
                } else if ("originalTrackNumber".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.ORIGINAL_TRACK_NUMBER(Integer.valueOf(getCharacters())));
                } else if ("userAnnotation".equals(str2)) {
                    getInstance().addProperty(new DIDLObject.Property.UPNP.USER_ANNOTATION(getCharacters()));
                }
            }
        }
    }

    public class RootHandler extends SAXParser.Handler<DIDLContent> {
        RootHandler(DIDLContent dIDLContent, SAXParser sAXParser) {
            super(dIDLContent, sAXParser);
        }

        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
            super.startElement(str, str2, str3, attributes);
            if (DIDLContent.NAMESPACE_URI.equals(str)) {
                if (str2.equals("container")) {
                    Container containerCreateContainer = DIDLParser.this.createContainer(attributes);
                    getInstance().addContainer(containerCreateContainer);
                    DIDLParser.this.createContainerHandler(containerCreateContainer, this);
                } else if (str2.equals("item")) {
                    Item itemCreateItem = DIDLParser.this.createItem(attributes);
                    getInstance().addItem(itemCreateItem);
                    DIDLParser.this.createItemHandler(itemCreateItem, this);
                } else if (str2.equals("desc")) {
                    DescMeta descMetaCreateDescMeta = DIDLParser.this.createDescMeta(attributes);
                    getInstance().addDescMetadata(descMetaCreateDescMeta);
                    DIDLParser.this.createDescMetaHandler(descMetaCreateDescMeta, this);
                }
            }
        }

        @Override // org.teleal.common.xml.SAXParser.Handler
        protected boolean isLastElement(String str, String str2, String str3) {
            if (!DIDLContent.NAMESPACE_URI.equals(str) || !"DIDL-Lite".equals(str2)) {
                return false;
            }
            getInstance().replaceGenericContainerAndItems();
            return true;
        }
    }

    public class ContainerHandler extends DIDLObjectHandler<Container> {
        public ContainerHandler(Container container, SAXParser.Handler handler) {
            super(container, handler);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
            Res resCreateResource;
            super.startElement(str, str2, str3, attributes);
            if (DIDLContent.NAMESPACE_URI.equals(str)) {
                if (str2.equals("item")) {
                    Item itemCreateItem = DIDLParser.this.createItem(attributes);
                    ((Container) getInstance()).addItem(itemCreateItem);
                    DIDLParser.this.createItemHandler(itemCreateItem, this);
                } else if (str2.equals("desc")) {
                    DescMeta descMetaCreateDescMeta = DIDLParser.this.createDescMeta(attributes);
                    ((Container) getInstance()).addDescMetadata(descMetaCreateDescMeta);
                    DIDLParser.this.createDescMetaHandler(descMetaCreateDescMeta, this);
                } else {
                    if (!str2.equals("res") || (resCreateResource = DIDLParser.this.createResource(attributes)) == null) {
                        return;
                    }
                    ((Container) getInstance()).addResource(resCreateResource);
                    DIDLParser.this.createResHandler(resCreateResource, this);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.teleal.cling.support.contentdirectory.DIDLParser.DIDLObjectHandler, org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String str, String str2, String str3) throws SAXException {
            super.endElement(str, str2, str3);
            if (DIDLObject.Property.UPNP.NAMESPACE.URI.equals(str)) {
                if ("searchClass".equals(str2)) {
                    ((Container) getInstance()).getSearchClasses().add(new DIDLObject.Class(getCharacters(), getAttributes().getValue("name"), "true".equals(getAttributes().getValue("includeDerived"))));
                } else if ("createClass".equals(str2)) {
                    ((Container) getInstance()).getCreateClasses().add(new DIDLObject.Class(getCharacters(), getAttributes().getValue("name"), "true".equals(getAttributes().getValue("includeDerived"))));
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.teleal.common.xml.SAXParser.Handler
        protected boolean isLastElement(String str, String str2, String str3) {
            if (!DIDLContent.NAMESPACE_URI.equals(str) || !"container".equals(str2)) {
                return false;
            }
            if (((Container) getInstance()).getTitle() == null) {
                DIDLParser.log.warning("In DIDL content, missing 'dc:title' element for container: " + ((Container) getInstance()).getId());
            }
            if (((Container) getInstance()).getClazz() != null) {
                return true;
            }
            DIDLParser.log.warning("In DIDL content, missing 'upnp:class' element for container: " + ((Container) getInstance()).getId());
            return true;
        }
    }

    public class ItemHandler extends DIDLObjectHandler<Item> {
        public ItemHandler(Item item, SAXParser.Handler handler) {
            super(item, handler);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
            super.startElement(str, str2, str3, attributes);
            if (DIDLContent.NAMESPACE_URI.equals(str)) {
                if (str2.equals("res")) {
                    Res resCreateResource = DIDLParser.this.createResource(attributes);
                    if (resCreateResource != null) {
                        ((Item) getInstance()).addResource(resCreateResource);
                        DIDLParser.this.createResHandler(resCreateResource, this);
                        return;
                    }
                    return;
                }
                if (str2.equals("desc")) {
                    DescMeta descMetaCreateDescMeta = DIDLParser.this.createDescMeta(attributes);
                    ((Item) getInstance()).addDescMetadata(descMetaCreateDescMeta);
                    DIDLParser.this.createDescMetaHandler(descMetaCreateDescMeta, this);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.teleal.common.xml.SAXParser.Handler
        protected boolean isLastElement(String str, String str2, String str3) {
            if (!DIDLContent.NAMESPACE_URI.equals(str) || !"item".equals(str2)) {
                return false;
            }
            if (((Item) getInstance()).getTitle() == null) {
                DIDLParser.log.warning("In DIDL content, missing 'dc:title' element for item: " + ((Item) getInstance()).getId());
            }
            if (((Item) getInstance()).getClazz() != null) {
                return true;
            }
            DIDLParser.log.warning("In DIDL content, missing 'upnp:class' element for item: " + ((Item) getInstance()).getId());
            return true;
        }
    }

    protected class ResHandler extends SAXParser.Handler<Res> {
        public ResHandler(Res res, SAXParser.Handler handler) {
            super(res, handler);
        }

        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String str, String str2, String str3) throws SAXException {
            super.endElement(str, str2, str3);
            getInstance().setValue(getCharacters());
        }

        @Override // org.teleal.common.xml.SAXParser.Handler
        protected boolean isLastElement(String str, String str2, String str3) {
            return DIDLContent.NAMESPACE_URI.equals(str) && "res".equals(str2);
        }
    }

    public class DescMetaHandler extends SAXParser.Handler<DescMeta> {
        protected Element current;

        public DescMetaHandler(DescMeta descMeta, SAXParser.Handler handler) {
            super(descMeta, handler);
            descMeta.setMetadata(descMeta.createMetadataDocument());
            this.current = ((Document) getInstance().getMetadata()).getDocumentElement();
        }

        @Override // org.teleal.common.xml.SAXParser.Handler
        public DescMeta getInstance() {
            return (DescMeta) super.getInstance();
        }

        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
            super.startElement(str, str2, str3, attributes);
            Element elementCreateElementNS = ((Document) getInstance().getMetadata()).createElementNS(str, str3);
            for (int i = 0; i < attributes.getLength(); i++) {
                elementCreateElementNS.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
            }
            this.current.appendChild(elementCreateElementNS);
            this.current = elementCreateElementNS;
        }

        @Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String str, String str2, String str3) throws SAXException {
            super.endElement(str, str2, str3);
            if (isLastElement(str, str2, str3)) {
                return;
            }
            if (getCharacters().length() > 0 && !getCharacters().matches("[\\t\\n\\x0B\\f\\r\\s]+")) {
                this.current.appendChild(((Document) getInstance().getMetadata()).createTextNode(getCharacters()));
            }
            this.current = (Element) this.current.getParentNode();
            this.characters = new StringBuilder();
            this.attributes = null;
        }

        @Override // org.teleal.common.xml.SAXParser.Handler
        protected boolean isLastElement(String str, String str2, String str3) {
            return DIDLContent.NAMESPACE_URI.equals(str) && "desc".equals(str2);
        }
    }
}

