package com.netfeige.common;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class DiscussInfo implements Parcelable {
    public static final Parcelable.Creator<DiscussInfo> CREATOR = new Parcelable.Creator<DiscussInfo>() { // from class: com.netfeige.common.DiscussInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DiscussInfo createFromParcel(Parcel parcel) {
            return new DiscussInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DiscussInfo[] newArray(int i) {
            return new DiscussInfo[i];
        }
    };
    private boolean mBExit;
    public boolean mBUnreadMsg;
    private long mLCreateTime;
    private long mLEndTime;
    private String mStrAuthor;
    private String mStrId;
    private String mStrName;
    private String mStrsMember;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DiscussInfo() {
        this.mLEndTime = 0L;
        this.mBExit = false;
        this.mBUnreadMsg = false;
    }

    public DiscussInfo(Parcel parcel) {
        this.mLEndTime = 0L;
        this.mBExit = false;
        this.mBUnreadMsg = false;
        this.mStrId = parcel.readString();
        this.mStrName = parcel.readString();
        this.mStrAuthor = parcel.readString();
        this.mStrsMember = parcel.readString();
        this.mLCreateTime = parcel.readLong();
        this.mLEndTime = parcel.readLong();
        this.mBExit = parcel.readInt() == 1;
        this.mBUnreadMsg = parcel.readInt() == 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mStrId);
        parcel.writeString(this.mStrName);
        parcel.writeString(this.mStrAuthor);
        parcel.writeString(this.mStrsMember);
        parcel.writeLong(this.mLCreateTime);
        parcel.writeLong(this.mLEndTime);
        parcel.writeInt(this.mBExit ? 1 : 0);
        parcel.writeInt(this.mBUnreadMsg ? 1 : 0);
    }

    public String getStrId() {
        return this.mStrId;
    }

    public void setStrId(String str) {
        this.mStrId = str;
    }

    public String getStrName() {
        return this.mStrName;
    }

    public void setStrName(String str) {
        this.mStrName = str;
    }

    public String getStrAuthor() {
        return this.mStrAuthor;
    }

    public void setStrAuthor(String str) {
        this.mStrAuthor = str;
    }

    public String getStrsMember() {
        return this.mStrsMember;
    }

    public void setStrsMember(String str) {
        this.mStrsMember = str;
    }

    public long getLCreateTime() {
        return this.mLCreateTime;
    }

    public void setLCreateTime(long j) {
        this.mLCreateTime = j;
    }

    public long getLEndTime() {
        return this.mLEndTime;
    }

    public void setLEndTime(long j) {
        this.mLEndTime = j;
    }

    public boolean isBExit() {
        return this.mBExit;
    }

    public void setBExit(boolean z) {
        this.mBExit = z;
    }

    public boolean isBUnreadMsg() {
        return this.mBUnreadMsg;
    }

    public void setBUnreadMsg(boolean z) {
        this.mBUnreadMsg = z;
    }
}

