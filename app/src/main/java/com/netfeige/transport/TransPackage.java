package com.netfeige.transport;

import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.protocol.ProPackage;
import java.net.InetAddress;

/* JADX INFO: loaded from: classes.dex */
public class TransPackage {
    public InetAddress Addr;
    public String m_additionalSection;
    public long m_nCommandID;
    public long m_nPacketID;
    public String m_senderHost;
    public String m_senderName;
    public long m_sendtimeStamp;
    public String m_version;
    public int netPort;

    public TransPackage() {
        this.m_version = Public_Tools.getVersion();
        this.m_nPacketID = 0L;
        this.m_senderName = "";
        this.m_senderHost = "";
        this.m_nCommandID = 0L;
        this.m_sendtimeStamp = System.currentTimeMillis();
    }

    public TransPackage(ProPackage proPackage) {
        this.m_version = Public_Tools.getVersion();
        this.m_nPacketID = 0L;
        this.m_senderName = "";
        this.m_senderHost = "";
        this.m_nCommandID = 0L;
        this.m_sendtimeStamp = System.currentTimeMillis();
        if (proPackage != null) {
            this.m_nPacketID = 0 == proPackage.nPackageID ? Public_Tools.getCurrentTimeMillis() : proPackage.nPackageID;
            this.m_nCommandID = proPackage.nCommandID;
            this.m_senderHost = proPackage.HostInfo.pszHostName;
            this.m_senderName = proPackage.HostInfo.pszUserName;
            this.Addr = proPackage.HostInfo.IpAddr.netAddr;
            this.netPort = proPackage.HostInfo.IpAddr.listenPort;
            this.m_additionalSection = proPackage.strAdditionalSection;
        }
    }

    public TransPackage(String str, short s) {
        this.m_version = Public_Tools.getVersion();
        this.m_nPacketID = 0L;
        this.m_senderName = "";
        this.m_senderHost = "";
        this.m_nCommandID = 0L;
        this.m_sendtimeStamp = System.currentTimeMillis();
        this.m_version = str;
        this.m_nPacketID = Public_Tools.getCurrentTimeMillis();
        this.m_nCommandID = s;
    }

    public TransPackage(String str, long j, int i) {
        this.m_version = Public_Tools.getVersion();
        this.m_nPacketID = 0L;
        this.m_senderName = "";
        this.m_senderHost = "";
        this.m_nCommandID = 0L;
        this.m_sendtimeStamp = System.currentTimeMillis();
        this.m_version = str;
        this.m_nPacketID = j;
        this.m_nCommandID = i;
    }

    public TransPackage(String str, long j, long j2, String str2, String str3, String str4, InetAddress inetAddress, int i) {
        this.m_version = Public_Tools.getVersion();
        this.m_nPacketID = 0L;
        this.m_senderName = "";
        this.m_senderHost = "";
        this.m_nCommandID = 0L;
        this.m_sendtimeStamp = System.currentTimeMillis();
        this.m_version = str;
        this.m_nPacketID = j;
        this.m_nCommandID = j2;
        this.m_senderHost = str3;
        this.m_senderName = str2;
        this.m_additionalSection = str4;
        this.Addr = inetAddress;
        this.netPort = i;
    }

    public TransPackage(String str, int i, String str2, String str3, String str4) {
        this.m_version = Public_Tools.getVersion();
        this.m_nPacketID = 0L;
        this.m_senderName = "";
        this.m_senderHost = "";
        this.m_nCommandID = 0L;
        this.m_sendtimeStamp = System.currentTimeMillis();
        this.m_version = str;
        this.m_nPacketID = Public_Tools.getCurrentTimeMillis();
        this.m_nCommandID = i;
        this.m_senderHost = str3;
        this.m_senderName = str2;
        this.m_additionalSection = str4;
    }

    public String toString() {
        return this.m_version + Public_MsgID.PRO_SPACE + Long.toString(this.m_nPacketID) + Public_MsgID.PRO_SPACE + this.m_senderName + Public_MsgID.PRO_SPACE + this.m_senderHost + Public_MsgID.PRO_SPACE + Long.toString(this.m_nCommandID) + Public_MsgID.PRO_SPACE + this.m_additionalSection;
    }

    public ProPackage DataPackToProPackage(ProPackage.PackageType packageType) {
        ProPackage proPackage = new ProPackage();
        proPackage.Type = packageType;
        proPackage.HostInfo.IpAddr.netAddr = this.Addr;
        proPackage.HostInfo.IpAddr.listenPort = this.netPort;
        proPackage.HostInfo.IpAddr.RoutePort = this.netPort;
        proPackage.HostInfo.pszHostName = this.m_senderHost;
        proPackage.HostInfo.pszUserName = this.m_senderName;
        proPackage.HostInfo.version = this.m_version;
        proPackage.nPackageID = this.m_nPacketID;
        proPackage.nCommandID = this.m_nCommandID;
        proPackage.strAdditionalSection = this.m_additionalSection;
        return proPackage;
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x0056, code lost:
    
        r0 = r8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.netfeige.transport.TransPackage makeDataPack(java.lang.String r21, java.net.InetAddress r22, int r23) {
        /*
            if (r21 != 0) goto L4
            r0 = 0
            return r0
        L4:
            r0 = 0
            r2 = 0
            java.lang.String r3 = ""
            java.lang.String r4 = "1"
            r10 = 0
            r5 = r0
            r7 = r3
            r8 = r7
            r9 = r4
            r0 = r21
            r1 = r8
            r3 = r5
        L14:
            int r11 = r0.length()     // Catch: java.lang.Exception -> L60
            if (r11 == 0) goto L56
            java.lang.String r11 = ":"
            int r11 = r0.indexOf(r11)     // Catch: java.lang.Exception -> L60
            r12 = -1
            r13 = 5
            if (r11 == r12) goto L53
            java.lang.String r12 = r0.substring(r2, r11)     // Catch: java.lang.Exception -> L60
            if (r10 == 0) goto L49
            r14 = 1
            if (r10 == r14) goto L44
            r14 = 2
            if (r10 == r14) goto L42
            r14 = 3
            if (r10 == r14) goto L40
            r14 = 4
            if (r10 == r14) goto L3b
            if (r10 == r13) goto L39
            goto L4a
        L39:
            r8 = r0
            goto L4a
        L3b:
            long r5 = java.lang.Long.parseLong(r12)     // Catch: java.lang.Exception -> L60
            goto L4a
        L40:
            r7 = r12
            goto L4a
        L42:
            r1 = r12
            goto L4a
        L44:
            long r3 = java.lang.Long.parseLong(r12)     // Catch: java.lang.Exception -> L60
            goto L4a
        L49:
            r9 = r12
        L4a:
            int r10 = r10 + 1
            int r11 = r11 + 1
            java.lang.String r0 = r0.substring(r11)     // Catch: java.lang.Exception -> L60
            goto L14
        L53:
            if (r10 != r13) goto L56
            goto L57
        L56:
            r0 = r8
        L57:
            r18 = r0
            r16 = r1
            r12 = r3
            r14 = r5
            r17 = r7
            goto L6c
        L60:
            r0 = move-exception
            r0.printStackTrace()
            r16 = r1
            r12 = r3
            r14 = r5
            r17 = r7
            r18 = r8
        L6c:
            r11 = r9
            com.netfeige.transport.TransPackage r0 = new com.netfeige.transport.TransPackage
            r10 = r0
            r19 = r22
            r20 = r23
            r10.<init>(r11, r12, r14, r16, r17, r18, r19, r20)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.transport.TransPackage.makeDataPack(java.lang.String, java.net.InetAddress, int):com.netfeige.transport.TransPackage");
    }
}

