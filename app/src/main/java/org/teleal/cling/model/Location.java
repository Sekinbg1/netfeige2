package org.teleal.cling.model;

import java.net.URI;
import java.net.URL;
import org.teleal.common.util.URIUtil;

/* JADX INFO: loaded from: classes.dex */
public class Location {
    protected NetworkAddress networkAddress;
    protected URI path;

    public Location(NetworkAddress networkAddress, URI uri) {
        this.networkAddress = networkAddress;
        this.path = uri;
    }

    public NetworkAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public URI getPath() {
        return this.path;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Location location = (Location) obj;
        return this.networkAddress.equals(location.networkAddress) && this.path.equals(location.path);
    }

    public int hashCode() {
        return (this.networkAddress.hashCode() * 31) + this.path.hashCode();
    }

    public URL getURL() {
        return URIUtil.createAbsoluteURL(this.networkAddress.getAddress(), this.networkAddress.getPort(), this.path);
    }
}

