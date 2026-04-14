package org.teleal.cling.model.types;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.UUID;
import org.teleal.cling.model.ModelUtil;

/* JADX INFO: loaded from: classes.dex */
public class UDN {
    public static final String PREFIX = "uuid:";
    private String identifierString;

    public UDN(String str) {
        this.identifierString = str;
    }

    public UDN(UUID uuid) {
        this.identifierString = uuid.toString();
    }

    public boolean isUDA11Compliant() {
        try {
            UUID.fromString(this.identifierString);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public String getIdentifierString() {
        return this.identifierString;
    }

    public static UDN valueOf(String str) {
        if (str.startsWith("uuid:")) {
            str = str.substring(5);
        }
        return new UDN(str);
    }

    public static UDN uniqueSystemIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        try {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                sb.append(localHost.getHostName());
                sb.append(localHost.getHostAddress());
            } catch (Exception unused) {
                sb.append(new String(ModelUtil.getFirstNetworkInterfaceHardwareAddress()));
            }
        } catch (Throwable unused2) {
        }
        sb.append(System.getProperty("os.name"));
        sb.append(System.getProperty("os.version"));
        try {
            return new UDN(new UUID(new BigInteger(-1, MessageDigest.getInstance("MD5").digest(sb.toString().getBytes())).longValue(), str.hashCode()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "uuid:" + getIdentifierString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UDN)) {
            return false;
        }
        return this.identifierString.equals(((UDN) obj).identifierString);
    }

    public int hashCode() {
        return this.identifierString.hashCode();
    }
}

