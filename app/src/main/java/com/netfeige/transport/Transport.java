package com.netfeige.transport;

import android.content.Context;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Def.WifiConnectFailException;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.protocol.ProPackage;
import java.io.IOException;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class Transport implements ITransport {
	private static ITransport m_transport = new Transport();
	private TransMsg m_transportMsg = new TransMsg();
	FileTransManager m_fileTranManager = FileTransManager.getInstance();

	public static ITransport getInstance() {
		if (m_transport == null) {
			m_transport = new Transport();
		}
		return m_transport;
	}

	private Transport() {
	}

	@Override // com.netfeige.transport.ITransport
	public boolean Init(Context context, ITransNotify iTransNotify) throws Public_Def.WifiConnectFailException, IOException {
		if (iTransNotify == null) {
			return false;
		}
		this.m_transportMsg.start(context, iTransNotify);
		this.m_fileTranManager.start(context, iTransNotify);
		if (Public_Tools.isWifiConnect()) {
			return true;
		}
		Public_Def public_Def = Public_Def.getInstance();
		public_Def.getClass();
		throw public_Def.new WifiConnectFailException();
	}

	@Override // com.netfeige.transport.ITransport
	public boolean SendMessage(ProPackage proPackage) {
		if (proPackage.Type != ProPackage.PackageType.UDP) {
			return false;
		}
		if (0 != (proPackage.nCommandID & 1)) {
			this.m_transportMsg.broadcastMessage(proPackage);
			return true;
		}
		this.m_transportMsg.sendProPackage(proPackage);
		return true;
	}

	public void broadcastMessage(ProPackage proPackage) {
		if (proPackage.Type == ProPackage.PackageType.UDP) {
			this.m_transportMsg.broadcastMessage(proPackage);
		}
	}

	@Override // com.netfeige.transport.ITransport
	public boolean sendFile(ProPackage proPackage, FileInformation fileInformation) {
		if ((this.m_fileTranManager == null) || ((proPackage == null) | (fileInformation == null))) {
			return false;
		}
		boolean zSendMessage = SendMessage(proPackage) & this.m_fileTranManager.addRequestingFile(proPackage.HostInfo.strMacAddr, fileInformation);
		SendMessage(offLineFile(fileInformation, proPackage.HostInfo));
		return zSendMessage;
	}

	@Override // com.netfeige.transport.ITransport
	public boolean sendFile(ProPackage proPackage, Vector<FileInformation> vector) {
		if ((this.m_fileTranManager == null) || ((proPackage == null) | (vector == null))) {
			return false;
		}
		boolean zSendMessage = SendMessage(proPackage);
		for (int i = 0; i < vector.size(); i++) {
			FileInformation fileInformation = vector.get(i);
			if (fileInformation != null) {
				this.m_fileTranManager.addRequestingFile(proPackage.HostInfo.strMacAddr, fileInformation);
				SendMessage(offLineFile(fileInformation, proPackage.HostInfo));
			}
		}
		return zSendMessage;
	}

	public ProPackage offLineFile(FileInformation fileInformation, HostInformation hostInformation) {
		return Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 209L, Public_Tools.getFileID(fileInformation.nPackageID, Public_Tools.getFileIndex(fileInformation.Id)) + Public_MsgID.CUTAPART);
	}

	@Override // com.netfeige.transport.ITransport
	public boolean recvFile(ProPackage proPackage, FileInformation fileInformation) throws IOException {
		return this.m_fileTranManager.recvFile(proPackage, fileInformation);
	}

	@Override // com.netfeige.transport.ITransport
	public void cancelFileTrans(String str, long j, long j2) {
		if (this.m_fileTranManager.removeRequestingFile(str, j, j2, 0L) == null) {
			this.m_fileTranManager.removeTransThread(str, j, j2);
		}
	}
}

