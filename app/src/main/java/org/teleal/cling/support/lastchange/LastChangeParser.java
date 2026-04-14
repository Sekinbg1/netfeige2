package org.teleal.cling.support.lastchange;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.teleal.cling.model.XMLUtil;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.shared.AbstractMap;
import org.teleal.common.io.IO;
import org.teleal.common.util.Exceptions;
import org.teleal.common.xml.SAXParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public abstract class LastChangeParser extends SAXParser {
	private static final Logger log = Logger.getLogger(LastChangeParser.class.getName());

	protected abstract String getNamespace();

	public enum CONSTANTS {
		Event,
		InstanceID,
		val;

		public boolean equals(String str) {
			return name().equals(str);
		}
	}

	protected Set<Class<? extends EventedValue>> getEventedVariables() {
		return Collections.EMPTY_SET;
	}

	protected EventedValue createValue(String str, Map.Entry<String, String>[] entryArr) throws Exception {
		for (Class<? extends EventedValue> cls : getEventedVariables()) {
			if (cls.getSimpleName().equals(str)) {
				return cls.getConstructor(Map.Entry[].class).newInstance((Object) entryArr);
			}
		}
		return null;
	}

	public Event parseResource(String str) throws Exception {
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

	public Event parse(String str) throws Exception {
		if (str == null || str.length() == 0) {
			throw new RuntimeException("Null or empty XML");
		}
		Event event = new Event();
		new RootHandler(event, this);
		log.fine("Parsing 'LastChange' event XML content");
		parse(new InputSource(new StringReader(str)));
		log.fine("Parsed event with instances IDs: " + event.getInstanceIDs().size());
		if (log.isLoggable(Level.FINEST)) {
			for (InstanceID instanceID : event.getInstanceIDs()) {
				log.finest("InstanceID '" + instanceID.getId() + "' has values: " + instanceID.getValues().size());
				for (EventedValue eventedValue : instanceID.getValues()) {
					log.finest(eventedValue.getName() + " => " + eventedValue.getValue());
				}
			}
		}
		return event;
	}

	class RootHandler extends SAXParser.Handler<Event> {
		RootHandler(Event event, SAXParser sAXParser) {
			super(event, sAXParser);
		}

		RootHandler(Event event) {
			super(event);
		}

		@Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
		public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
			String value;
			super.startElement(str, str2, str3, attributes);
			if (!CONSTANTS.InstanceID.equals(str2) || (value = attributes.getValue(CONSTANTS.val.name())) == null) {
				return;
			}
			InstanceID instanceID = new InstanceID(new UnsignedIntegerFourBytes(value));
			getInstance().getInstanceIDs().add(instanceID);
			LastChangeParser.this.new InstanceIDHandler(instanceID, this);
		}
	}

	class InstanceIDHandler extends SAXParser.Handler<InstanceID> {
		InstanceIDHandler(InstanceID instanceID, SAXParser.Handler handler) {
			super(instanceID, handler);
		}

		@Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
		public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
			super.startElement(str, str2, str3, attributes);
			int length = attributes.getLength();
			Map.Entry<String, String>[] entryArr = new Map.Entry[length];
			for (int i = 0; i < length; i++) {
				entryArr[i] = new AbstractMap.SimpleEntry(attributes.getLocalName(i), attributes.getValue(i));
			}
			try {
				EventedValue eventedValueCreateValue = LastChangeParser.this.createValue(str2, entryArr);
				if (eventedValueCreateValue != null) {
					getInstance().getValues().add(eventedValueCreateValue);
				}
			} catch (Exception e) {
				LastChangeParser.log.warning("Error reading event XML, ignoring value: " + Exceptions.unwrap(e));
			}
		}

		@Override // org.teleal.common.xml.SAXParser.Handler
		protected boolean isLastElement(String str, String str2, String str3) {
			return CONSTANTS.InstanceID.equals(str2);
		}
	}

	public String generate(Event event) throws Exception {
		return XMLUtil.documentToFragmentString(buildDOM(event));
	}

	protected Document buildDOM(Event event) throws Exception {
		DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
		documentBuilderFactoryNewInstance.setNamespaceAware(true);
		Document documentNewDocument = documentBuilderFactoryNewInstance.newDocumentBuilder().newDocument();
		generateRoot(event, documentNewDocument);
		return documentNewDocument;
	}

	protected void generateRoot(Event event, Document document) {
		Element elementCreateElementNS = document.createElementNS(getNamespace(), CONSTANTS.Event.name());
		document.appendChild(elementCreateElementNS);
		generateInstanceIDs(event, document, elementCreateElementNS);
	}

	protected void generateInstanceIDs(Event event, Document document, Element element) {
		for (InstanceID instanceID : event.getInstanceIDs()) {
			if (instanceID.getId() != null) {
				Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, CONSTANTS.InstanceID.name());
				elementAppendNewElement.setAttribute(CONSTANTS.val.name(), instanceID.getId().toString());
				Iterator<EventedValue> it = instanceID.getValues().iterator();
				while (it.hasNext()) {
					generateEventedValue(it.next(), document, elementAppendNewElement);
				}
			}
		}
	}

	protected void generateEventedValue(EventedValue eventedValue, Document document, Element element) {
		String name = eventedValue.getName();
		Map.Entry<String, String>[] attributes = eventedValue.getAttributes();
		if (attributes == null || attributes.length <= 0) {
			return;
		}
		Element elementAppendNewElement = XMLUtil.appendNewElement(document, element, name);
		for (Map.Entry<String, String> entry : attributes) {
			elementAppendNewElement.setAttribute(entry.getKey(), entry.getValue());
		}
	}
}

