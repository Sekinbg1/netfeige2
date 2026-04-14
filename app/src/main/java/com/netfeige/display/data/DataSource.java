package com.netfeige.display.data;

import android.content.Context;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.FileInformation;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.MsgInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.protocol.IProtocol;
import com.netfeige.protocol.IProtocolNotify;
import com.netfeige.protocol.Protocol;
import com.netfeige.service.IpmsgService;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class DataSource implements IProtocolNotify {
    private static final String s_strDuifang = "瀵规柟";
    private static final String s_strPrintAnswer = "接受了您的打印请求。";
    private static final String s_strPrintFinish = "为您打印已完成。";
    private static final String s_strPrintRefused = "拒绝了您的打印请求。";
    private static final String s_strPrintTimeout = "长时间未响应，您可以再次请求或选择其他打印机。";
    private Context mContext;
    public final IProtocol m_Protocol;

    public boolean Init() {
        return true;
    }

    public DataSource(Context context) {
        IProtocol protocol = Protocol.getInstance();
        this.m_Protocol = protocol;
        this.mContext = context;
        try {
            protocol.Init(this, context);
        } catch (Public_Def.WifiConnectFailException | IOException unused) {
        } catch (SocketException e) {
            if (e.getClass().getName().equals("java.net.BindException")) {
                Public_Tools.showToast(context, context.getString(R.string.post_using), 1);
            }
        }
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyEntryService(HostInformation hostInformation) {
        ((IpmsgService) this.mContext).addOrModifyHostInfo(hostInformation);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyAnsEntry(HostInformation hostInformation) {
        ((IpmsgService) this.mContext).addOrModifyHostInfo(hostInformation);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyExitService(HostInformation hostInformation) {
        ((IpmsgService) this.mContext).removeHostInfo(hostInformation);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifySendMsg(HostInformation hostInformation, MsgInformation msgInformation) {
        ((IpmsgService) this.mContext).processMessage(hostInformation, msgInformation.strDiscussID, msgInformation.strMsg, false, false, msgInformation.nPackageID);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifySendMsgFail(HostInformation hostInformation, long j) {
        ((IpmsgService) this.mContext).notifySendMsgFail(hostInformation, j);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifySendFile(HostInformation hostInformation, Vector<FileInformation> vector, boolean z) {
        if (vector.isEmpty()) {
            return;
        }
        boolean z2 = false;
        if (vector.get(0).status == Public_Def.TransStatus.Trans_SendFailed) {
            z = true;
            z2 = true;
        }
        ((IpmsgService) this.mContext).processFile(hostInformation, vector, z, z2);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifySendFileFail(HostInformation hostInformation, long j) {
        ((IpmsgService) this.mContext).notifySendFileFail(hostInformation, j);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyCancelRecvFile(HostInformation hostInformation, long j, long j2) {
        if (-1 == j || -1 == j2) {
            return;
        }
        ((IpmsgService) this.mContext).processCancelRecvFile(hostInformation, j, j2);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyCancelSendFile(HostInformation hostInformation, long j, long j2) {
        if (-1 == j || -1 == j2) {
            return;
        }
        ((IpmsgService) this.mContext).processCancelSendFile(hostInformation, j, j2);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyPrintAnswer(HostInformation hostInformation) {
        if (Global.g_hostInformation != null) {
            Toast.makeText(this.mContext, Global.g_hostInformation.pszUserName + s_strPrintAnswer, 1).show();
            return;
        }
        Toast.makeText(this.mContext, "对方拒绝了您的打印请求。", 1).show();
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyPrintRefused(HostInformation hostInformation) {
        if (Global.g_hostInformation != null) {
            Toast.makeText(this.mContext, Global.g_hostInformation.pszUserName + s_strPrintRefused, 1).show();
            return;
        }
        Toast.makeText(this.mContext, "对方拒绝了您的打印请求。", 1).show();
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyPrintTimeout(HostInformation hostInformation) {
        if (Global.g_hostInformation != null) {
            Toast.makeText(this.mContext, Global.g_hostInformation.pszUserName + s_strPrintTimeout, 1).show();
            return;
        }
        Toast.makeText(this.mContext, "对方拒绝了您的打印请求。", 1).show();
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyPrintFinish(HostInformation hostInformation) {
        if (Global.g_hostInformation != null) {
            Toast.makeText(this.mContext, Global.g_hostInformation.pszUserName + s_strPrintFinish, 1).show();
            return;
        }
        Toast.makeText(this.mContext, "对方拒绝了您的打印请求。", 1).show();
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyEntryBroadcastFinish(boolean z) {
        ((IpmsgService) this.mContext).onEntryBroadcastEventHandler(z);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifyFileShareListAns(HashMap<String, Object> map) {
        ((IpmsgService) this.mContext).onFileShareListAns(map);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void notifySubFileShareListAns(HashMap<String, Object> map) {
        ((IpmsgService) this.mContext).onSubFileShareListAns(map);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void onInvite(HostInformation hostInformation, DiscussInfo discussInfo) {
        ((IpmsgService) this.mContext).onInvite(hostInformation, discussInfo);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void onReject(String str, HostInformation hostInformation) {
        ((IpmsgService) this.mContext).onReject(str, hostInformation);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void onDiscussInfo() {
        ((IpmsgService) this.mContext).onDiscussInfo();
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void onDiscussAdd(String str, String str2) {
        ((IpmsgService) this.mContext).onDiscussAdd(str, str2);
    }

    @Override // com.netfeige.protocol.IProtocolNotify
    public void onDiscussExit(String str, String str2) {
        ((IpmsgService) this.mContext).onDiscussExit(str, str2);
    }
}

