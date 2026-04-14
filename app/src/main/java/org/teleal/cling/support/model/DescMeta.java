package org.teleal.cling.support.model;

import java.net.URI;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public class DescMeta<M> {
    protected String id;
    protected M metadata;
    protected URI nameSpace;
    protected String type;

    public DescMeta() {
    }

    public DescMeta(String str, String str2, URI uri, M m) {
        this.id = str;
        this.type = str2;
        this.nameSpace = uri;
        this.metadata = m;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public URI getNameSpace() {
        return this.nameSpace;
    }

    public void setNameSpace(URI uri) {
        this.nameSpace = uri;
    }

    public M getMetadata() {
        return this.metadata;
    }

    public void setMetadata(M m) {
        this.metadata = m;
    }

    public Document createMetadataDocument() {
        try {
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setNamespaceAware(true);
            Document documentNewDocument = documentBuilderFactoryNewInstance.newDocumentBuilder().newDocument();
            documentNewDocument.appendChild(documentNewDocument.createElementNS(DIDLContent.DESC_WRAPPER_NAMESPACE_URI, "desc-wrapper"));
            return documentNewDocument;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

