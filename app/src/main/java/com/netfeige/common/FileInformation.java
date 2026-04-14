package com.netfeige.common;

import android.os.Parcel;
import android.os.Parcelable;
import com.netfeige.common.Public_Def;
import com.netfeige.protocol.IFileTransNotify;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class FileInformation implements Cloneable, Parcelable {
	public static final Parcelable.Creator<FileInformation> CREATOR = new Parcelable.Creator<FileInformation>() { // from class: com.netfeige.common.FileInformation.1
		/* JADX WARN: Can't rename method to resolve collision */
		@Override // android.os.Parcelable.Creator
		public FileInformation createFromParcel(Parcel parcel) {
			return new FileInformation(parcel);
		}

		/* JADX WARN: Can't rename method to resolve collision */
		@Override // android.os.Parcelable.Creator
		public FileInformation[] newArray(int i) {
			return new FileInformation[i];
		}
	};
	public String FileName;
	public long Id;
	public String Path;
	public Public_Def.FileTransMode fileTransMode;
	public String mStrDiscussID;
	public int nFileAttr;
	public long nPackageID;
	public long size;
	public long startPos;
	public Public_Def.TransStatus status;
	public String strOriginalFileName;
	public long time;
	public IFileTransNotify transNotify;
	public Vector<String> vecFilterType;

	@Override // android.os.Parcelable
	public int describeContents() {
		return 0;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public FileInformation() {
		try {
			this.Path = Public_Tools.getDefaultDownloadPath();
		} catch (Throwable e) {
			this.Path = "";
		}
		this.strOriginalFileName = "";
		this.startPos = 0L;
		this.status = Public_Def.TransStatus.Trans_Ready;
		this.fileTransMode = Public_Def.FileTransMode.FILETRANS_ORDER;
		this.mStrDiscussID = "";
		this.transNotify = null;
	}

	public FileInformation(Parcel parcel) {
		try {
			this.Path = Public_Tools.getDefaultDownloadPath();
		} catch (Throwable e) {
			this.Path = "";
		}
		this.strOriginalFileName = "";
		this.startPos = 0L;
		this.status = Public_Def.TransStatus.Trans_Ready;
		this.fileTransMode = Public_Def.FileTransMode.FILETRANS_ORDER;
		this.mStrDiscussID = "";
		this.transNotify = null;
		this.Id = parcel.readLong();
		this.Path = parcel.readString();
		this.FileName = parcel.readString();
		this.strOriginalFileName = parcel.readString();
		this.size = parcel.readLong();
		this.startPos = parcel.readLong();
		this.status = Public_Def.TransStatus.valueOf(parcel.readString());
		this.fileTransMode = Public_Def.FileTransMode.valueOf(parcel.readString());
		this.nPackageID = parcel.readLong();
		this.time = parcel.readLong();
		this.nFileAttr = parcel.readInt();
		this.mStrDiscussID = parcel.readString();
		this.transNotify = (IFileTransNotify) parcel.readValue(null);
	}

	@Override // android.os.Parcelable
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeLong(this.Id);
		parcel.writeString(this.Path);
		parcel.writeString(this.FileName);
		parcel.writeString(this.strOriginalFileName);
		parcel.writeLong(this.size);
		parcel.writeLong(this.startPos);
		parcel.writeString(this.status.toString());
		parcel.writeString(this.fileTransMode.toString());
		parcel.writeLong(this.nPackageID);
		parcel.writeLong(this.time);
		parcel.writeInt(this.nFileAttr);
		parcel.writeString(this.mStrDiscussID);
		parcel.writeValue(this.transNotify);
	}

	public static String toTmpFileName(String str) {
		if (isTmpFile(str)) {
			return str;
		}
		return str + ".tmp";
	}

	public static boolean isTmpFile(String str) {
		return str.length() > 4 && str.endsWith(".tmp");
	}
}
