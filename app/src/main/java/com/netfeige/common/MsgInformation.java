package com.netfeige.common;

import android.os.Parcel;
import android.os.Parcelable;
import com.netfeige.common.Public_Def;

/* JADX INFO: loaded from: classes.dex */
public class MsgInformation implements Parcelable {
    public static final Parcelable.Creator<MsgInformation> CREATOR = new Parcelable.Creator<MsgInformation>() { // from class: com.netfeige.common.MsgInformation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MsgInformation createFromParcel(Parcel parcel) {
            return new MsgInformation(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MsgInformation[] newArray(int i) {
            return new MsgInformation[i];
        }
    };
    public long nPackageID;
    public String strDiscussID;
    public String strMsg;
    public Public_Def.TransStatus transStatus;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MsgInformation(String str, String str2, long j, Public_Def.TransStatus transStatus) {
        this.strMsg = null;
        this.transStatus = Public_Def.TransStatus.Trans_Ready;
        this.strMsg = str;
        this.strDiscussID = str2;
        this.nPackageID = j;
        this.transStatus = transStatus;
    }

    public MsgInformation(Parcel parcel) {
        this.strMsg = null;
        this.transStatus = Public_Def.TransStatus.Trans_Ready;
        this.strDiscussID = parcel.readString();
        this.strMsg = parcel.readString();
        this.nPackageID = parcel.readLong();
        this.transStatus = (Public_Def.TransStatus) parcel.readValue(null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.strDiscussID);
        parcel.writeString(this.strMsg);
        parcel.writeLong(this.nPackageID);
        parcel.writeValue(this.transStatus);
    }
}

