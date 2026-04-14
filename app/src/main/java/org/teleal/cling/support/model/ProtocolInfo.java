package org.teleal.cling.support.model;

import com.netfeige.common.Public_MsgID;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.common.util.MimeType;

/* JADX INFO: loaded from: classes.dex */
public class ProtocolInfo {
    public static final String TRAILING_ZEROS = "000000000000000000000000";
    public static final String WILDCARD = "*";
    protected String additionalInfo;
    protected String contentFormat;
    protected String network;
    protected Protocol protocol;

    public static final class DLNAFlags {
        public static final int BACKGROUND_TRANSFERT_MODE = 4194304;
        public static final int BYTE_BASED_SEEK = 536870912;
        public static final int CONNECTION_STALL = 2097152;
        public static final int DLNA_V15 = 1048576;
        public static final int FLAG_PLAY_CONTAINER = 268435456;
        public static final int INTERACTIVE_TRANSFERT_MODE = 8388608;
        public static final int RTSP_PAUSE = 33554432;
        public static final int S0_INCREASE = 134217728;
        public static final int SENDER_PACED = Integer.MIN_VALUE;
        public static final int SN_INCREASE = 67108864;
        public static final int STREAMING_TRANSFER_MODE = 16777216;
        public static final int TIME_BASED_SEEK = 1073741824;
    }

    public ProtocolInfo(String str) throws InvalidValueException {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        if (str == null) {
            throw null;
        }
        String strTrim = str.trim();
        String[] strArrSplit = strTrim.split(Public_MsgID.PRO_SPACE);
        if (strArrSplit.length != 4) {
            throw new InvalidValueException("Can't parse ProtocolInfo string: " + strTrim);
        }
        this.protocol = Protocol.valueOrNullOf(strArrSplit[0]);
        this.network = strArrSplit[1];
        this.contentFormat = strArrSplit[2];
        this.additionalInfo = strArrSplit[3];
    }

    public ProtocolInfo(MimeType mimeType) {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        this.protocol = Protocol.HTTP_GET;
        this.contentFormat = mimeType.toString();
    }

    public ProtocolInfo(Protocol protocol, String str, String str2, String str3) {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        this.protocol = protocol;
        this.network = str;
        this.contentFormat = str2;
        this.additionalInfo = str3;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public String getNetwork() {
        return this.network;
    }

    public String getContentFormat() {
        return this.contentFormat;
    }

    public MimeType getContentFormatMimeType() throws IllegalArgumentException {
        return MimeType.valueOf(this.contentFormat);
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProtocolInfo protocolInfo = (ProtocolInfo) obj;
        return this.additionalInfo.equals(protocolInfo.additionalInfo) && this.contentFormat.equals(protocolInfo.contentFormat) && this.network.equals(protocolInfo.network) && this.protocol == protocolInfo.protocol;
    }

    public int hashCode() {
        return (((((this.protocol.hashCode() * 31) + this.network.hashCode()) * 31) + this.contentFormat.hashCode()) * 31) + this.additionalInfo.hashCode();
    }

    public String toString() {
        return this.protocol.toString() + Public_MsgID.PRO_SPACE + this.network + Public_MsgID.PRO_SPACE + this.contentFormat + Public_MsgID.PRO_SPACE + this.additionalInfo;
    }
}

