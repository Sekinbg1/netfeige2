package org.teleal.cling.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.resource.Resource;
import org.teleal.common.util.URIUtil;

/* JADX INFO: loaded from: classes.dex */
public class Namespace {
    public static final String CALLBACK_FILE = "/cb.xml";
    public static final String CONTROL = "/action";
    public static final String DESCRIPTOR_FILE = "/desc.xml";
    public static final String DEVICE = "/dev";
    public static final String EVENTS = "/event";
    public static final String SERVICE = "/svc";
    private static final Logger log = Logger.getLogger(Namespace.class.getName());
    protected final URI basePath;

    public Namespace() {
        this.basePath = URI.create("");
    }

    public Namespace(String str) {
        this(URI.create(str));
    }

    public Namespace(URI uri) {
        this.basePath = uri;
    }

    public URI getBasePath() {
        return this.basePath;
    }

    public URI getPath(Device device) {
        if (device.getIdentity().getUdn() == null) {
            throw new IllegalStateException("Can't generate local URI prefix without UDN");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getBasePath().toString());
        sb.append(DEVICE + ServiceReference.DELIMITER + URIUtil.encodePathSegment(device.getIdentity().getUdn().getIdentifierString()));
        return URI.create(sb.toString());
    }

    public URI getPath(Service service) {
        if (service.getServiceId() == null) {
            throw new IllegalStateException("Can't generate local URI prefix without service ID");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getPath(service.getDevice()).toString());
        sb.append(SERVICE + ServiceReference.DELIMITER + service.getServiceId().getNamespace() + ServiceReference.DELIMITER + service.getServiceId().getId());
        return URI.create(sb.toString());
    }

    public URI getDescriptorPath(Device device) {
        return URI.create(getPath(device.getRoot()).toString() + DESCRIPTOR_FILE);
    }

    public URI getDescriptorPath(Service service) {
        return URI.create(getPath(service).toString() + DESCRIPTOR_FILE);
    }

    public URI getControlPath(Service service) {
        return URI.create(getPath(service).toString() + CONTROL);
    }

    public URI getEventSubscriptionPath(Service service) {
        return URI.create(getPath(service).toString() + EVENTS);
    }

    public URI getEventCallbackPath(Service service) {
        return URI.create(getEventSubscriptionPath(service).toString() + CALLBACK_FILE);
    }

    public URI prefixIfRelative(Device device, URI uri) {
        if (uri.isAbsolute() || uri.getPath().startsWith(ServiceReference.DELIMITER)) {
            return uri;
        }
        return URI.create(getPath(device).toString() + ServiceReference.DELIMITER + uri.toString());
    }

    public boolean isControlPath(URI uri) {
        return uri.toString().endsWith(CONTROL);
    }

    public boolean isEventSubscriptionPath(URI uri) {
        return uri.toString().endsWith(EVENTS);
    }

    public boolean isEventCallbackPath(URI uri) {
        return uri.toString().endsWith(CALLBACK_FILE);
    }

    public Resource[] getResources(Device device) throws ValidationException {
        if (!device.isRoot()) {
            return null;
        }
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        log.fine("Discovering local resources of device graph");
        for (Resource resource : device.discoverResources(this)) {
            log.finer("Discovered: " + resource);
            if (!hashSet.add(resource)) {
                log.finer("Local resource already exists, queueing validation error");
                arrayList.add(new ValidationError(getClass(), "resources", "Local URI namespace conflict between resources of device: " + resource));
            }
        }
        if (arrayList.size() > 0) {
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", arrayList);
        }
        return (Resource[]) hashSet.toArray(new Resource[hashSet.size()]);
    }
}

