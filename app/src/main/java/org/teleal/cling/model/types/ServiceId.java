package org.teleal.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teleal.cling.model.Constants;

/* JADX INFO: loaded from: classes.dex */
public class ServiceId {
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:([a-zA-Z_0-9\\-:\\.]{1,64})");
    private String id;
    private String namespace;

    public ServiceId(String str, String str2) {
        if (str != null && !str.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service ID namespace contains illegal characters");
        }
        this.namespace = str;
        if (str2 != null && !str2.matches(Constants.REGEX_ID)) {
            throw new IllegalArgumentException("Service ID suffix too long (64) or contains illegal characters");
        }
        this.id = str2;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getId() {
        return this.id;
    }

    public static ServiceId valueOf(String str) throws InvalidValueException {
        UDAServiceId uDAServiceIdValueOf;
        try {
            uDAServiceIdValueOf = UDAServiceId.valueOf(str);
        } catch (Exception unused) {
            uDAServiceIdValueOf = null;
        }
        if (uDAServiceIdValueOf != null) {
            return uDAServiceIdValueOf;
        }
        Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            return new ServiceId(matcher.group(1), matcher.group(2));
        }
        throw new InvalidValueException("Can't parse Service ID string (namespace/id): " + str);
    }

    public String toString() {
        return "urn:" + getNamespace() + ":serviceId:" + getId();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ServiceId)) {
            return false;
        }
        ServiceId serviceId = (ServiceId) obj;
        return this.id.equals(serviceId.id) && this.namespace.equals(serviceId.namespace);
    }

    public int hashCode() {
        return (this.namespace.hashCode() * 31) + this.id.hashCode();
    }
}

