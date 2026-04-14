package org.teleal.cling.model.types;

import com.netfeige.common.Public_MsgID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teleal.cling.model.Constants;

/* JADX INFO: loaded from: classes.dex */
public class ServiceType {
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    private String namespace;
    private String type;
    private int version;

    public ServiceType(String str, String str2) {
        this(str, str2, 1);
    }

    public ServiceType(String str, String str2, int i) {
        this.version = 1;
        if (str != null && !str.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service type namespace contains illegal characters");
        }
        this.namespace = str;
        if (str2 != null && !str2.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Service type suffix too long (64) or contains illegal characters");
        }
        this.type = str2;
        this.version = i;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public static ServiceType valueOf(String str) throws InvalidValueException {
        UDAServiceType uDAServiceTypeValueOf;
        String strReplaceAll = str.replaceAll("\\s", "");
        try {
            uDAServiceTypeValueOf = UDAServiceType.valueOf(strReplaceAll);
        } catch (Exception unused) {
            uDAServiceTypeValueOf = null;
        }
        if (uDAServiceTypeValueOf != null) {
            return uDAServiceTypeValueOf;
        }
        Matcher matcher = PATTERN.matcher(strReplaceAll);
        if (matcher.matches()) {
            return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)).intValue());
        }
        throw new InvalidValueException("Can't parse service type string (namespace/type/version): " + strReplaceAll);
    }

    public boolean implementsVersion(ServiceType serviceType) {
        return serviceType != null && this.namespace.equals(serviceType.namespace) && this.type.equals(serviceType.type) && this.version >= serviceType.version;
    }

    public String toFriendlyString() {
        return getNamespace() + Public_MsgID.PRO_SPACE + getType() + Public_MsgID.PRO_SPACE + getVersion();
    }

    public String toString() {
        return "urn:" + getNamespace() + ":service:" + getType() + Public_MsgID.PRO_SPACE + getVersion();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ServiceType)) {
            return false;
        }
        ServiceType serviceType = (ServiceType) obj;
        return this.version == serviceType.version && this.namespace.equals(serviceType.namespace) && this.type.equals(serviceType.type);
    }

    public int hashCode() {
        return (((this.namespace.hashCode() * 31) + this.type.hashCode()) * 31) + this.version;
    }
}

