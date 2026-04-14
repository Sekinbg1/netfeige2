package com.netfeige.protocol;

import android.content.ContentValues;
import android.content.Context;
import com.netfeige.R;
import com.netfeige.common.DBHelper;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.protocol.ProPackage;
import com.netfeige.service.IpmsgService;
import com.netfeige.transport.ITransNotify;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class Protocol_Discuss implements ITransNotify {
    public static final String smPreID = "Discuss_";
    private Context mContext;
    private CmdController mMsgController;
    private UILopperFreighter mUILopperFreighter;

    public Protocol_Discuss(Context context, CmdController cmdController, UILopperFreighter uILopperFreighter) {
        this.mContext = null;
        this.mMsgController = null;
        this.mUILopperFreighter = null;
        this.mContext = context;
        this.mMsgController = cmdController;
        this.mUILopperFreighter = uILopperFreighter;
    }

    @Override // com.netfeige.transport.ITransNotify
    public void Recv(ProPackage proPackage) {
        boolean zUpdateDataRecord;
        switch ((short) Public_Tools.getLowBitCmd(proPackage.nCommandID)) {
            case 240:
                DiscussInfo object = toObject(proPackage.strAdditionalSection);
                DiscussInfo discussInfo = getDiscussInfo(object.getStrId());
                if (discussInfo == null || discussInfo.isBExit()) {
                    this.mUILopperFreighter.onInvite(proPackage.HostInfo, object);
                }
                break;
            case 241:
                this.mUILopperFreighter.onReject(proPackage.strAdditionalSection, proPackage.HostInfo);
                break;
            case 242:
                ansInfo(proPackage.strAdditionalSection, proPackage.HostInfo.strMacAddr);
                break;
            case 243:
                DiscussInfo object2 = toObject(proPackage.strAdditionalSection);
                ContentValues contentValues = new ContentValues();
                contentValues.put("Name", object2.getStrName());
                contentValues.put("Author", object2.getStrAuthor());
                contentValues.put("MemList", object2.getStrsMember());
                contentValues.put("CreateTime", Long.valueOf(object2.getLCreateTime()));
                contentValues.put("EndTime", Long.valueOf(object2.getLEndTime()));
                contentValues.put("Exit", Integer.valueOf(object2.isBExit() ? 1 : 0));
                if (getDiscussInfo(object2.getStrId()) == null) {
                    contentValues.put("DiscussID", object2.getStrId());
                    zUpdateDataRecord = DBHelper.getInstance(this.mContext).insertDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussInfo(), null, contentValues);
                } else {
                    zUpdateDataRecord = DBHelper.getInstance(this.mContext).updateDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussInfo(), contentValues, "DiscussID = ?", new String[]{object2.getStrId()});
                }
                if (zUpdateDataRecord) {
                    ((IpmsgService) this.mContext).addOrReplaceDiscuss(object2);
                    this.mUILopperFreighter.onDiscussInfo();
                    addMemberForCase(object2);
                }
                break;
            case 244:
                String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
                if (strArrSplit != null && strArrSplit.length == 2) {
                    String str = strArrSplit[0];
                    String str2 = strArrSplit[1];
                    if (addMemberForLocal(str, str2)) {
                        this.mUILopperFreighter.onDiscussAdd(str, str2);
                    }
                    break;
                }
                break;
            case 245:
                String[] strArrSplit2 = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
                if (strArrSplit2 != null && strArrSplit2.length == 2) {
                    String str3 = strArrSplit2[0];
                    String str4 = strArrSplit2[1];
                    if (removeMemberForLocal(str3, str4) && (!str4.equals(Public_Tools.getLocalMacAddress()) || !proPackage.HostInfo.strMacAddr.equals(Public_Tools.getLocalMacAddress()))) {
                        this.mUILopperFreighter.onDiscussExit(str3, str4);
                    }
                    HostInformation hostInfo = ((IpmsgService) this.mContext).getHostInfo(proPackage.HostInfo.strMacAddr);
                    if (hostInfo != null) {
                        this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 247L, str3 + Public_MsgID.CUTAPART + str4));
                    }
                    break;
                }
                break;
            case 246:
                String[] strArrSplit3 = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
                if (strArrSplit3 != null && strArrSplit3.length == 3) {
                    String str5 = strArrSplit3[0];
                    String str6 = strArrSplit3[1];
                    String str7 = proPackage.HostInfo.strMacAddr;
                    boolean z = Integer.parseInt(strArrSplit3[2]) == 1;
                    DBHelper.getInstance(this.mContext).deleteDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussExeStatus(), "DestMac = ? and RecvMac = ? and DiscussID = ?", new String[]{str6, str7, str5});
                    if (!z && removeMemberForLocal(str5, str7) && !str7.equals(Public_Tools.getLocalMacAddress())) {
                        this.mUILopperFreighter.onDiscussExit(str5, str7);
                        break;
                    }
                }
                break;
            case 247:
                String[] strArrSplit4 = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
                if (strArrSplit4 != null && strArrSplit4.length == 2) {
                    DBHelper.getInstance(this.mContext).deleteDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussExeStatus(), "DestMac = ? and RecvMac = ? and DiscussID = ?", new String[]{strArrSplit4[1], proPackage.HostInfo.strMacAddr, strArrSplit4[0]});
                    break;
                }
                break;
        }
    }

    public boolean ansInfo(String str, String str2) {
        DiscussInfo discussInfo;
        HostInformation hostInfo;
        if (str.trim().isEmpty() || str2.trim().isEmpty() || (discussInfo = getDiscussInfo(str)) == null || (hostInfo = ((IpmsgService) this.mContext).getHostInfo(str2)) == null) {
            return false;
        }
        String strsMember = discussInfo.getStrsMember();
        if (!strsMember.contains(hostInfo.strMacAddr)) {
            strsMember = strsMember + Public_MsgID.PRO_SPACE_GROUP + hostInfo.strMacAddr;
        }
        discussInfo.setStrsMember(strsMember);
        discussInfo.setLEndTime(0L);
        return this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 243L, toProString(discussInfo)));
    }

    public boolean addMemberForCase(DiscussInfo discussInfo) {
        String[] strArrSplit = discussInfo.getStrsMember().split(Public_MsgID.PRO_SPACE_GROUP);
        String localMacAddress = Public_Tools.getLocalMacAddress();
        for (int i = 0; i < strArrSplit.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("DiscussID", discussInfo.getStrId());
            contentValues.put("DestMac", localMacAddress);
            contentValues.put("RecvMac", strArrSplit[i]);
            contentValues.put("ExeTime", Long.valueOf(new Date().getTime() / 1000));
            contentValues.put("IsJoin", (Integer) 1);
            contentValues.put("IsNotified", (Integer) 0);
            DBHelper.getInstance(this.mContext).insertDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussExeStatus(), null, contentValues);
            HostInformation hostInfo = ((IpmsgService) this.mContext).getHostInfo(strArrSplit[i]);
            if (hostInfo != null) {
                this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 244L, discussInfo.getStrId() + Public_MsgID.CUTAPART + localMacAddress));
            }
        }
        return true;
    }

    public boolean addMemberForLocal(String str, String str2) {
        DiscussInfo discussInfo = getDiscussInfo(str);
        int i = 1;
        boolean zUpdateDataRecord = false;
        if (discussInfo == null || discussInfo.isBExit()) {
            i = 0;
        } else if (!discussInfo.getStrsMember().contains(str2)) {
            discussInfo.setStrsMember(discussInfo.getStrsMember() + Public_MsgID.PRO_SPACE_GROUP + str2);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", discussInfo.getStrName());
            contentValues.put("Author", discussInfo.getStrAuthor());
            contentValues.put("MemList", discussInfo.getStrsMember());
            contentValues.put("CreateTime", Long.valueOf(discussInfo.getLCreateTime()));
            contentValues.put("EndTime", Long.valueOf(discussInfo.getLEndTime()));
            contentValues.put("Exit", Integer.valueOf(discussInfo.isBExit() ? 1 : 0));
            zUpdateDataRecord = DBHelper.getInstance(this.mContext).updateDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussInfo(), contentValues, "DiscussID = ?", new String[]{discussInfo.getStrId()});
            ((IpmsgService) this.mContext).addOrReplaceDiscuss(discussInfo);
        }
        HostInformation hostInfo = ((IpmsgService) this.mContext).getHostInfo(str2);
        if (hostInfo != null) {
            this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 246L, str + Public_MsgID.CUTAPART + str2 + Public_MsgID.CUTAPART + i));
        }
        return zUpdateDataRecord;
    }

    public boolean removeMemberForLocal(String str, String str2) {
        DiscussInfo discussInfo;
        if (str.isEmpty() || str2.isEmpty() || (discussInfo = getDiscussInfo(str)) == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", discussInfo.getStrName());
        contentValues.put("Author", discussInfo.getStrAuthor());
        contentValues.put("CreateTime", Long.valueOf(discussInfo.getLCreateTime()));
        contentValues.put("EndTime", Long.valueOf(discussInfo.getLEndTime()));
        if (str2.equals(Public_Tools.getLocalMacAddress())) {
            contentValues.put("MemList", discussInfo.getStrsMember());
            contentValues.put("Exit", (Boolean) true);
        } else if (discussInfo.getStrsMember().contains(str2)) {
            String[] strArrSplit = discussInfo.getStrsMember().split(Public_MsgID.PRO_SPACE_GROUP);
            String str3 = "";
            for (int i = 0; i < strArrSplit.length; i++) {
                if (!strArrSplit[i].equals(str2)) {
                    str3 = str3 + strArrSplit[i] + Public_MsgID.PRO_SPACE_GROUP;
                }
            }
            contentValues.put("MemList", str3.length() > 0 ? str3.substring(0, str3.length() - 1) : "");
            contentValues.put("Exit", Boolean.valueOf(discussInfo.isBExit()));
        }
        boolean zUpdateDataRecord = DBHelper.getInstance(this.mContext).updateDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussInfo(), contentValues, "DiscussID = ?", new String[]{discussInfo.getStrId()});
        ((IpmsgService) this.mContext).initDiscussList();
        return zUpdateDataRecord;
    }

    public boolean create(String str, Vector<String> vector, String str2) {
        if (str.isEmpty() || vector.size() == 0) {
            return false;
        }
        DiscussInfo discussInfo = new DiscussInfo();
        discussInfo.setStrId(str2);
        discussInfo.setStrName(str);
        discussInfo.setStrAuthor(Public_Tools.getLocalMacAddress());
        discussInfo.setStrsMember(Public_Tools.getLocalMacAddress());
        discussInfo.setLCreateTime(new Date().getTime() / 1000);
        discussInfo.setLEndTime(0L);
        discussInfo.setBExit(false);
        ContentValues contentValues = new ContentValues();
        contentValues.put("DiscussID", discussInfo.getStrId());
        contentValues.put("Name", discussInfo.getStrName());
        contentValues.put("Author", discussInfo.getStrAuthor());
        contentValues.put("MemList", discussInfo.getStrsMember());
        contentValues.put("CreateTime", Long.valueOf(discussInfo.getLCreateTime()));
        contentValues.put("EndTime", Long.valueOf(discussInfo.getLEndTime()));
        contentValues.put("Exit", Integer.valueOf(discussInfo.isBExit() ? 1 : 0));
        if (!DBHelper.getInstance(this.mContext).insertDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussInfo(), null, contentValues)) {
            return false;
        }
        ((IpmsgService) this.mContext).mListDiscuss.add(discussInfo);
        for (int i = 0; i < vector.size(); i++) {
            invite(discussInfo.getStrId(), vector.get(i));
        }
        return true;
    }

    public void invite(String str, String str2) {
        if (str2.equals(Public_Tools.getLocalMacAddress())) {
            return;
        }
        HostInformation hostInfo = ((IpmsgService) this.mContext).getHostInfo(str2);
        if (hostInfo == null) {
            Context context = this.mContext;
            Public_Tools.showToast(context, context.getString(R.string.offline_prompt), 0);
            return;
        }
        DiscussInfo discussInfo = getDiscussInfo(str);
        if (discussInfo != null) {
            ProPackage proPackage = new ProPackage();
            proPackage.Type = ProPackage.PackageType.UDP;
            proPackage.HostInfo = hostInfo;
            proPackage.nPackageID = Public_Tools.getCurrentTimeMillis();
            proPackage.nCommandID = 240L;
            proPackage.strAdditionalSection = toProString(discussInfo);
            this.mMsgController.m_Transport.SendMessage(proPackage);
        }
    }

    public void agree(String str, HostInformation hostInformation) {
        if (str.trim().isEmpty()) {
            return;
        }
        this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 242L, str));
    }

    public void reject(String str, HostInformation hostInformation) {
        if (str.trim().isEmpty()) {
            return;
        }
        this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 241L, str));
    }

    public boolean exit(String str, String str2) {
        if (str.isEmpty() || str2.isEmpty()) {
            return false;
        }
        DiscussInfo discussInfo = getDiscussInfo(str);
        if (discussInfo == null) {
            return true;
        }
        if (!Public_Tools.getLocalMacAddress().equals(str2) && !Public_Tools.getLocalMacAddress().equals(discussInfo.getStrAuthor())) {
            return false;
        }
        String[] strArrSplit = discussInfo.getStrsMember().split(Public_MsgID.PRO_SPACE_GROUP);
        removeMemberForLocal(str, str2);
        for (int i = 0; i < strArrSplit.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("DiscussID", discussInfo.getStrId());
            contentValues.put("DestMac", str2);
            contentValues.put("RecvMac", strArrSplit[i]);
            contentValues.put("ExeTime", Long.valueOf(new Date().getTime() / 1000));
            contentValues.put("IsJoin", (Integer) 0);
            contentValues.put("IsNotified", (Integer) 0);
            DBHelper.getInstance(this.mContext).insertDataRecord(DBHelper.getInstance(this.mContext).getM_strDiscussExeStatus(), null, contentValues);
            HostInformation hostInfo = ((IpmsgService) this.mContext).getHostInfo(strArrSplit[i]);
            if (hostInfo != null) {
                this.mMsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 245L, discussInfo.getStrId() + Public_MsgID.CUTAPART + str2));
            }
        }
        return true;
    }

    public DiscussInfo getDiscussInfo(String str) {
        ArrayList<DiscussInfo> discussInfoRecord = DBHelper.getInstance(this.mContext).getDiscussInfoRecord("DiscussID = '" + str + "'");
        if (discussInfoRecord.size() > 0) {
            return discussInfoRecord.get(0);
        }
        return null;
    }

    public String toProString(DiscussInfo discussInfo) {
        String str;
        if (discussInfo == null) {
            return "";
        }
        String str2 = "" + discussInfo.getStrId() + Public_MsgID.CUTAPART + discussInfo.getStrName() + Public_MsgID.CUTAPART + discussInfo.getStrAuthor() + Public_MsgID.CUTAPART;
        if (discussInfo.getStrsMember() == null || discussInfo.getStrsMember().length() <= 0) {
            str = str2 + Public_MsgID.CUTAPART;
        } else {
            str = str2 + discussInfo.getStrsMember() + Public_MsgID.CUTAPART;
        }
        return str + discussInfo.getLCreateTime() + Public_MsgID.CUTAPART + discussInfo.getLCreateTime();
    }

    private DiscussInfo toObject(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        DiscussInfo discussInfo = new DiscussInfo();
        String[] strArrSplit = str.split(Public_MsgID.CUTAPART);
        discussInfo.setStrId(strArrSplit[0]);
        discussInfo.setStrName(strArrSplit[1]);
        discussInfo.setStrAuthor(strArrSplit[2]);
        discussInfo.setStrsMember(strArrSplit[3]);
        discussInfo.setLCreateTime(Long.parseLong(strArrSplit[4]));
        discussInfo.setLEndTime(Long.parseLong(strArrSplit[5]));
        return discussInfo;
    }
}

