package com.netfeige.protocol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import com.netfeige.common.DBHelper;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HistoryFiles;
import com.netfeige.common.HostInformation;
import com.netfeige.common.MsgInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_MsgID;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class UILopperFreighter extends Handler implements IProtocolNotify {
	private static final int SENDFILE_FAIL = 268435458;
	private static final int SENDMSG_FAIL = 268435457;
	private static final String m_bKeyIsFinish = "IsFinish";
	private static final String m_strDiscussInfo = "discussInfo";
	private static final String m_strFileID = "FileID";
	private static final String m_strFileInformation = "FileInformation";
	private static final String m_strKeyHostInfo = "HostInformation";
	private static final String m_strLanShareInfo = "lanShareInfo";
	private static final String m_strMsgInformation = "MsgInformation";
	private static final String m_strPackageID = "PackageID";
	private static final String m_strVecFileInfo_size = "vecFileInfo_size";
	private IProtocolNotify m_ProtocolNotify = null;

	public void Init(IProtocolNotify iProtocolNotify) {
		this.m_ProtocolNotify = iProtocolNotify;
	}

	@Override // android.os.Handler
	public void handleMessage(Message message) {
		super.handleMessage(message);
		Bundle data = message.getData();
		switch (message.what) {
			case Public_MsgID.IPMSG_PRINT_ANSWER /* -2147483532 */:
				this.m_ProtocolNotify.notifyPrintAnswer((HostInformation) data.getParcelable(m_strKeyHostInfo));
				break;
			case Public_MsgID.IPMSG_PRINT_REFUSED /* -2147483531 */:
				this.m_ProtocolNotify.notifyPrintRefused((HostInformation) data.getParcelable(m_strKeyHostInfo));
				break;
			case Public_MsgID.IPMSG_PRINT_TIMEOUT /* -2147483530 */:
				this.m_ProtocolNotify.notifyPrintTimeout((HostInformation) data.getParcelable(m_strKeyHostInfo));
				break;
			case Public_MsgID.IPMSG_PRINT_FINISH /* -2147483529 */:
				this.m_ProtocolNotify.notifyPrintFinish((HostInformation) data.getParcelable(m_strKeyHostInfo));
				break;
			case Public_MsgID.IPMSG_FILESHARE_RESPONSE /* -2147483503 */:
				this.m_ProtocolNotify.notifyFileShareListAns((HashMap) data.getParcelableArrayList(m_strLanShareInfo).get(0));
				break;
			case Public_MsgID.IPMSG_FILESHARE_SUBDIRLISTANSWER /* -2147483500 */:
				this.m_ProtocolNotify.notifySubFileShareListAns((HashMap) data.getParcelableArrayList(m_strLanShareInfo).get(0));
				break;
			case Public_MsgID.IPMSG_ENTRY_BROADCAST_FINISH /* -1879048191 */:
				this.m_ProtocolNotify.notifyEntryBroadcastFinish(data.getBoolean(m_bKeyIsFinish));
				break;
			case 1:
				this.m_ProtocolNotify.notifyEntryService((HostInformation) message.obj);
				break;
			case 2:
				this.m_ProtocolNotify.notifyExitService((HostInformation) message.obj);
				break;
			case 3:
				this.m_ProtocolNotify.notifyAnsEntry((HostInformation) message.obj);
				break;
			case 32:
				this.m_ProtocolNotify.notifySendMsg((HostInformation) data.getParcelable(m_strKeyHostInfo), (MsgInformation) data.getParcelable(m_strMsgInformation));
				break;
			case 230:
				this.m_ProtocolNotify.notifyCancelRecvFile((HostInformation) data.getParcelable(m_strKeyHostInfo), data.getLong(m_strPackageID), data.getLong(m_strFileID));
				break;
			case 232:
				this.m_ProtocolNotify.notifyCancelSendFile((HostInformation) data.getParcelable(m_strKeyHostInfo), data.getLong(m_strPackageID), data.getLong(m_strFileID));
				break;
			case 240:
				this.m_ProtocolNotify.onInvite((HostInformation) data.getParcelable(m_strKeyHostInfo), (DiscussInfo) data.getParcelable(m_strDiscussInfo));
				break;
			case 241:
				this.m_ProtocolNotify.onReject(data.getString("discussid"), (HostInformation) data.getParcelable(m_strKeyHostInfo));
				break;
			case 243:
				this.m_ProtocolNotify.onDiscussInfo();
				break;
			case 244:
				this.m_ProtocolNotify.onDiscussAdd(data.getString("discussid"), data.getString("mac"));
				break;
			case 245:
				this.m_ProtocolNotify.onDiscussExit(data.getString("discussid"), data.getString("mac"));
				break;
			case 2097184:
				Vector<FileInformation> vector = new Vector<>();
				HostInformation hostInformation = (HostInformation) data.getParcelable(m_strKeyHostInfo);
				int i = data.getInt(m_strVecFileInfo_size);
				boolean z = data.getBoolean("isSend");
				for (int i2 = 0; i2 < i; i2++) {
					vector.add((FileInformation) data.getParcelable(m_strFileInformation + i2));
				}
				this.m_ProtocolNotify.notifySendFile(hostInformation, vector, z);
				break;
			case SENDMSG_FAIL /* 268435457 */:
				this.m_ProtocolNotify.notifySendMsgFail((HostInformation) data.getParcelable(m_strKeyHostInfo), data.getLong(m_strPackageID));
				break;
			case SENDFILE_FAIL /* 268435458 */:
				this.m_ProtocolNotify.notifySendFileFail((HostInformation) data.getParcelable(m_strKeyHostInfo), data.getLong(m_strPackageID));
				break;
		}
		data.clear();
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyEntryService(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 1;
		messageObtainMessage.obj = hostInformation;
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyAnsEntry(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 3;
		messageObtainMessage.obj = hostInformation;
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyExitService(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 2;
		messageObtainMessage.obj = hostInformation;
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifySendMsg(HostInformation hostInformation, MsgInformation msgInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 32;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putParcelable(m_strMsgInformation, msgInformation);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifySendMsgFail(HostInformation hostInformation, long j) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = SENDMSG_FAIL;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putLong(m_strPackageID, j);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifySendFile(HostInformation hostInformation, Vector<FileInformation> vector, boolean z) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 2097184;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putInt(m_strVecFileInfo_size, vector.size());
		bundle.putBoolean("isSend", z);
		for (int i = 0; i < vector.size(); i++) {
			FileInformation fileInformation = vector.get(i);
			if (!z) {
				String str = "sMac = '" + hostInformation.strMacAddr + "' and FileName = '" + fileInformation.strOriginalFileName + "' and TransStatus = 3 and Size = " + fileInformation.size;
				if (DBHelper.getInstance() != null) {
					ArrayList<HistoryFiles> historyFilesRecord = DBHelper.getInstance().getHistoryFilesRecord(str);
					if (historyFilesRecord.size() > 0) {
						for (int i2 = 0; i2 < historyFilesRecord.size(); i2++) {
							try {
								File file = new File(FileInformation.toTmpFileName(historyFilesRecord.get(i2).getM_strFileFullPath()));
								if (file.exists()) {
									fileInformation.startPos = file.length();
									fileInformation.Path = file.getParent();
									int iLastIndexOf = historyFilesRecord.get(0).getM_strFileFullPath().lastIndexOf(File.separator);
									if (iLastIndexOf >= 0) {
										fileInformation.FileName = historyFilesRecord.get(0).getM_strFileFullPath().substring(iLastIndexOf + 1, historyFilesRecord.get(0).getM_strFileFullPath().length());
									}
									fileInformation.fileTransMode = Public_Def.FileTransMode.FILETRANS_CONTINUE;
									break;
								} else {
									DBHelper.getInstance().deleteHistoryFilesRecord("ID = " + historyFilesRecord.get(i2).getM_iID());
								}
							} catch (Exception unused) {
							}
						}
					}
				}
			}
			bundle.putParcelable(m_strFileInformation + i, fileInformation);
		}
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifySendFileFail(HostInformation hostInformation, long j) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = SENDFILE_FAIL;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putLong(m_strPackageID, j);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyCancelRecvFile(HostInformation hostInformation, long j, long j2) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 230;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putLong(m_strPackageID, j);
		bundle.putLong(m_strFileID, j2);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyCancelSendFile(HostInformation hostInformation, long j, long j2) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 232;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putLong(m_strPackageID, j);
		bundle.putLong(m_strFileID, j2);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyPrintAnswer(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_PRINT_ANSWER;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyPrintRefused(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_PRINT_REFUSED;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyPrintTimeout(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_PRINT_TIMEOUT;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyPrintFinish(HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_PRINT_FINISH;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyEntryBroadcastFinish(boolean z) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_ENTRY_BROADCAST_FINISH;
		Bundle bundle = new Bundle();
		bundle.putBoolean(m_bKeyIsFinish, z);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifyFileShareListAns(HashMap<String, Object> map) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_FILESHARE_RESPONSE;
		Bundle bundle = new Bundle();
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
		arrayList.add(map);
		bundle.putSerializable(m_strLanShareInfo, arrayList);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void notifySubFileShareListAns(HashMap<String, Object> map) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = Public_MsgID.IPMSG_FILESHARE_SUBDIRLISTANSWER;
		Bundle bundle = new Bundle();
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
		arrayList.add(map);
		bundle.putSerializable(m_strLanShareInfo, arrayList);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void onInvite(HostInformation hostInformation, DiscussInfo discussInfo) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 240;
		Bundle bundle = new Bundle();
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		bundle.putParcelable(m_strDiscussInfo, discussInfo);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void onReject(String str, HostInformation hostInformation) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 241;
		Bundle bundle = new Bundle();
		bundle.putString("discussid", str);
		bundle.putParcelable(m_strKeyHostInfo, hostInformation);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void onDiscussInfo() {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 243;
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void onDiscussAdd(String str, String str2) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 244;
		Bundle bundle = new Bundle();
		bundle.putString("discussid", str);
		bundle.putString("mac", str2);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}

	@Override // com.netfeige.protocol.IProtocolNotify
	public void onDiscussExit(String str, String str2) {
		Message messageObtainMessage = obtainMessage();
		messageObtainMessage.what = 245;
		Bundle bundle = new Bundle();
		bundle.putString("discussid", str);
		bundle.putString("mac", str2);
		messageObtainMessage.setData(bundle);
		sendMessage(messageObtainMessage);
	}
}
