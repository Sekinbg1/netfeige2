package org.teleal.cling.model.message.header;

import com.netfeige.common.Public_MsgID;
import org.teleal.cling.model.Constants;
import org.teleal.cling.model.types.HostPort;

/* JADX INFO: loaded from: classes.dex */
public class HostHeader extends UpnpHeader<HostPort> {
    int port = Constants.UPNP_MULTICAST_PORT;
    String group = Constants.IPV4_UPNP_MULTICAST_GROUP;

    public HostHeader() {
        setValue(new HostPort(this.group, this.port));
    }

    public HostHeader(int i) {
        setValue(new HostPort(this.group, i));
    }

    public HostHeader(String str, int i) {
        setValue(new HostPort(str, i));
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (str.contains(Public_MsgID.PRO_SPACE)) {
            try {
                this.port = Integer.valueOf(str.substring(str.indexOf(Public_MsgID.PRO_SPACE) + 1)).intValue();
                this.group = str.substring(0, str.indexOf(Public_MsgID.PRO_SPACE));
                setValue(new HostPort(this.group, this.port));
                return;
            } catch (NumberFormatException e) {
                throw new InvalidHeaderException("Invalid HOST header value, can't parse port: " + str + " - " + e.getMessage());
            }
        }
        this.group = str;
        setValue(new HostPort(this.group, this.port));
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}

