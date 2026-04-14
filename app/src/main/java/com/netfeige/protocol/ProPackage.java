package com.netfeige.protocol;

import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ProPackage {
    public HostInformation HostInfo;
    public Public_Def.TransStatus Status;
    public PackageType Type;
    public long nCommandID;
    public long nPackageID;
    public String strAdditionalSection;
    public ArrayList<HostInformation> userList;

    public enum PackageType {
        TCP,
        UDP
    }

    public ProPackage() {
        this.Status = Public_Def.TransStatus.Trans_Ready;
        this.HostInfo = new HostInformation();
        this.nPackageID = Public_Tools.getCurrentTimeMillis();
        this.userList = null;
    }

    public ProPackage(PackageType packageType, HostInformation hostInformation, long j, String str) {
        this.Status = Public_Def.TransStatus.Trans_Ready;
        this.HostInfo = new HostInformation();
        this.nPackageID = Public_Tools.getCurrentTimeMillis();
        this.userList = null;
        this.Type = packageType;
        this.HostInfo = hostInformation;
        this.nCommandID = j;
        this.strAdditionalSection = str;
    }

    public ProPackage(PackageType packageType, HostInformation hostInformation, long j, String str, ArrayList<HostInformation> arrayList) {
        this.Status = Public_Def.TransStatus.Trans_Ready;
        this.HostInfo = new HostInformation();
        this.nPackageID = Public_Tools.getCurrentTimeMillis();
        this.userList = null;
        this.Type = packageType;
        this.HostInfo = hostInformation;
        this.userList = arrayList;
        this.nCommandID = j;
        this.strAdditionalSection = str;
    }
}

