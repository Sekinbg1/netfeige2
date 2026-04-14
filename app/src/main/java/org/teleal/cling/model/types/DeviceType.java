package org.teleal.cling.model.types;

import com.netfeige.common.Public_MsgID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teleal.cling.model.Constants;

/* JADX INFO: loaded from: classes.dex */
public class DeviceType {
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):device:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    private String namespace;
    private String type;
    private int version;

    public DeviceType(String str, String str2) {
        this(str, str2, 1);
    }

    public DeviceType(String str, String str2, int i) {
        this.version = 1;
        if (str != null && !str.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Device type namespace contains illegal characters");
        }
        this.namespace = str;
        if (str2 != null && !str2.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Device type suffix too long (64) or contains illegal characters");
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

    public static DeviceType valueOf(String str) throws InvalidValueException {
        UDADeviceType uDADeviceTypeValueOf;
        String strReplaceAll = str.replaceAll("\\s", "");
        try {
            uDADeviceTypeValueOf = UDADeviceType.valueOf(strReplaceAll);
        } catch (Exception unused) {
            uDADeviceTypeValueOf = null;
        }
        if (uDADeviceTypeValueOf != null) {
            return uDADeviceTypeValueOf;
        }
        Matcher matcher = PATTERN.matcher(strReplaceAll);
        if (matcher.matches()) {
            return new DeviceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)).intValue());
        }
        throw new InvalidValueException("Can't parse device type string (namespace/type/version): " + strReplaceAll);
    }

    public boolean implementsVersion(DeviceType deviceType) {
        return this.namespace.equals(deviceType.namespace) && this.type.equals(deviceType.type) && this.version >= deviceType.version;
    }

    public String getDisplayString() {
        return getType();
    }

    public String toString() {
        return "urn:" + getNamespace() + ":device:" + getType() + Public_MsgID.PRO_SPACE + getVersion();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof DeviceType)) {
            return false;
        }
        DeviceType deviceType = (DeviceType) obj;
        return this.version == deviceType.version && this.namespace.equals(deviceType.namespace) && this.type.equals(deviceType.type);
    }

    public int hashCode() {
        return (((this.namespace.hashCode() * 31) + this.type.hashCode()) * 31) + this.version;
    }
}

