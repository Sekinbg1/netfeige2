package com.netfeige.protocol;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.service.IpmsgService;
import com.netfeige.transport.ITransNotify;
import com.netfeige.transport.ITransport;
import com.netfeige.transport.Transport;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class CmdController {
    private Context m_Context;
    private ITransNotify m_Transport_Recv = new Transport_Recv();
    public final ITransport m_Transport = Transport.getInstance();
    private SparseArray<ITransNotify> m_mapMessageList = new SparseArray<>();

    public CmdController(Context context) {
        this.m_Context = null;
        this.m_Context = context;
    }

    public void init() throws Public_Def.WifiConnectFailException, IOException {
        this.m_Transport.Init(this.m_Context, this.m_Transport_Recv);
    }

    private class Transport_Recv implements ITransNotify {
        private Transport_Recv() {
        }

        @Override // com.netfeige.transport.ITransNotify
        public void Recv(ProPackage proPackage) {
            CmdController.this.DistributeMessage(proPackage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean DistributeMessage(ProPackage proPackage) {
        ITransNotify iTransNotifyValueAt;
        HostInformation hostInfoByIpPort;
        int i = 0;
        if ((this.m_mapMessageList == null) || (proPackage == null)) {
            return false;
        }
        while (true) {
            if (i >= this.m_mapMessageList.size()) {
                break;
            }
            long jKeyAt = this.m_mapMessageList.keyAt(i);
            if (Public_Tools.getLowBitCmd(jKeyAt) != Public_Tools.getLowBitCmd(proPackage.nCommandID) || (iTransNotifyValueAt = this.m_mapMessageList.valueAt(i)) == null) {
                i++;
            } else {
                if (1 != jKeyAt && 3 != jKeyAt && 18 != jKeyAt && 19 != jKeyAt && (hostInfoByIpPort = ((IpmsgService) this.m_Context).getHostInfoByIpPort(proPackage.HostInfo.IpAddr.netAddr.getHostAddress(), proPackage.HostInfo.IpAddr.listenPort)) != null) {
                    hostInfoByIpPort.version = proPackage.HostInfo.version;
                    proPackage.HostInfo = hostInfoByIpPort;
                }
                if (-2147483510 == Public_Tools.getLowBitCmd(proPackage.nCommandID)) {
                    Log.e("test", "test");
                }
                iTransNotifyValueAt.Recv(proPackage);
            }
        }
        return true;
    }

    public boolean BindMessage(int i, ITransNotify iTransNotify) {
        if (iTransNotify == null) {
            return false;
        }
        this.m_mapMessageList.put(i, iTransNotify);
        return true;
    }
}

