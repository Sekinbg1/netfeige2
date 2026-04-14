package org.teleal.cling.support.avtransport.lastchange;

import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.support.lastchange.EventedValue;
import org.teleal.cling.support.lastchange.LastChangeParser;

/* JADX INFO: loaded from: classes.dex */
public class AVTransportLastChangeParser extends LastChangeParser {
    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/AVT/";
    public static final String SCHEMA_RESOURCE = "org/teleal/cling/support/avtransport/metadata-1.0-avt.xsd";

    @Override // org.teleal.cling.support.lastchange.LastChangeParser
    protected String getNamespace() {
        return NAMESPACE_URI;
    }

    @Override // org.teleal.common.xml.SAXParser
    protected Source[] getSchemaSources() {
        if (ModelUtil.ANDROID_RUNTIME) {
            return null;
        }
        return new Source[]{new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(SCHEMA_RESOURCE))};
    }

    @Override // org.teleal.cling.support.lastchange.LastChangeParser
    protected Set<Class<? extends EventedValue>> getEventedVariables() {
        return AVTransportVariable.ALL;
    }
}

