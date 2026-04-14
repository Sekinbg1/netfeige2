package com.netfeige.common;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;

/* JADX INFO: loaded from: classes.dex */
public class HostInformation implements Parcelable {
    public static final Parcelable.Creator<HostInformation> CREATOR = new Parcelable.Creator<HostInformation>() { // from class: com.netfeige.common.HostInformation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HostInformation createFromParcel(Parcel parcel) {
            return new HostInformation(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HostInformation[] newArray(int i) {
            return new HostInformation[i];
        }
    };
    public IP_ADDR IpAddr;
    public String groupName;
    public String headImage;
    public boolean isChecked;
    public boolean isChoiced;
    public String platformType;
    public String pszHostName;
    public String pszHostUserName;
    public String pszUserName;
    public String strMacAddr;
    public String strSharePrinter;
    public boolean unreadMsg;
    public String version;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public class IP_ADDR {
        public int RoutePort;
        public int listenPort;
        public InetAddress netAddr;
        public InetAddress realNetAddr;

        public IP_ADDR() {
        }
    }

    public HostInformation() {
        this.IpAddr = new IP_ADDR();
        this.unreadMsg = false;
        this.isChecked = false;
        this.isChoiced = false;
        this.headImage = "-1";
    }

    public HostInformation(Parcel parcel) {
        this.IpAddr = new IP_ADDR();
        this.unreadMsg = false;
        this.isChecked = false;
        this.isChoiced = false;
        this.headImage = "-1";
        this.pszHostUserName = parcel.readString();
        this.pszHostName = parcel.readString();
        this.pszUserName = parcel.readString();
        this.groupName = parcel.readString();
        this.strSharePrinter = parcel.readString();
        this.strMacAddr = parcel.readString();
        this.IpAddr = (IP_ADDR) parcel.readValue(null);
        this.unreadMsg = 1 == parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.pszHostUserName);
        parcel.writeString(this.pszHostName);
        parcel.writeString(this.pszUserName);
        parcel.writeString(this.groupName);
        parcel.writeString(this.strSharePrinter);
        parcel.writeString(this.strMacAddr);
        parcel.writeValue(this.IpAddr);
        parcel.writeInt(this.unreadMsg ? 1 : 0);
        parcel.writeInt(this.isChecked ? 1 : 0);
        parcel.writeInt(this.isChoiced ? 1 : 0);
        parcel.writeString(this.platformType);
        parcel.writeString(this.headImage);
        parcel.writeString(this.version);
    }
}

