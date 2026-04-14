package com.netfeige.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.netfeige.R;
import com.netfeige.common.DBHelper;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.FileInformation;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.InviteDiscussInfo;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SoundPlayer;
import com.netfeige.display.data.DataSource;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.ui.ChatActivity;
import com.netfeige.display.ui.ChoicePrinterActivity;
import com.netfeige.display.ui.PrintActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.ScreenShot;
import com.netfeige.protocol.IFileTransNotify;
import com.netfeige.protocol.Protocol;
import com.netfeige.protocol.Protocol_Discuss;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class IpmsgService extends Service implements IFileTransNotify {
	private static final String m_strPrintNotify = "暂无打印机可用";
	public static ArrayList<HostInformation> printerList = new ArrayList<>();
	private IpmsgApplication myApp;
	public long msgId = 0;
	public DataSource m_DataSource = null;
	public ArrayList<Map<String, String>> m_resourceGroupList = new ArrayList<>();
	public ArrayList<ArrayList<Map<String, String>>> m_resourceChildList = new ArrayList<>();
	public ArrayList<HostInformation> userList = new ArrayList<>();
	public ArrayList<DiscussInfo> mListDiscuss = new ArrayList<>();
	public ArrayList<InviteDiscussInfo> mListInviteDiscuss = new ArrayList<>();
	public ArrayList<String> mListExitDiscuss = new ArrayList<>();
	public ArrayList<HostInformation> cacheUserList = new ArrayList<>();
	public Map<String, ArrayList<MsgRecord>> messages = new HashMap();
	public Map<String, ArrayList<MsgRecord>> screenShotMessages = new HashMap();
	public Map<String, ArrayList<MsgRecord>> fileMsgs = new HashMap();
	private LocalBinder localBinder = new LocalBinder();
	public ArrayList<EventHandler> ehList = new ArrayList<>();
	private SoundPlayer m_player = null;
	public ArrayList<EntryBroadcastEventHandler> entryBroadcastEHList = new ArrayList<>();
	public ArrayList<LanShareEventHandler> lanShareEHList = new ArrayList<>();
	public ArrayList<DiscussInviteEventHandler> mDiscussInviteEHList = new ArrayList<>();
	public ArrayList<DiscussInfoEventHandler> mDiscussInfoEHList = new ArrayList<>();
	public ArrayList<DiscussExitEventHandler> mDiscussExitEHList = new ArrayList<>();

	public interface DiscussExitEventHandler {
		void onDiscussExit();
	}

	public interface DiscussInfoEventHandler {
		void onDiscussInfo();
	}

	public interface DiscussInviteEventHandler {
		void onInvite();
	}

	public interface EntryBroadcastEventHandler {
		void onEntryBroadcastEventHandler(boolean z);
	}

	public interface EventHandler {
		void onAddMessage(String str, MsgRecord msgRecord);

		void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType);

		void onModifyFileList(String str);

		void onModifyFileList(String str, boolean z);

		void onModifyFileMessage(String str, MsgRecord msgRecord);

		void onModifyMessage(HostInformation hostInformation, MsgRecord msgRecord);
	}

	public interface LanShareEventHandler {
		void onFileShareListAns(HashMap<String, Object> map);

		void onSubFileShareListAns(HashMap<String, Object> map);
	}

	public boolean onEntryBroadcastEventHandler(boolean z) {
		for (int i = 0; i < this.entryBroadcastEHList.size(); i++) {
			this.entryBroadcastEHList.get(i).onEntryBroadcastEventHandler(z);
		}
		return true;
	}

	public boolean onFileShareListAns(HashMap<String, Object> map) {
		for (int i = 0; i < this.lanShareEHList.size(); i++) {
			this.lanShareEHList.get(i).onFileShareListAns(map);
		}
		return true;
	}

	public boolean onSubFileShareListAns(HashMap<String, Object> map) {
		for (int i = 0; i < this.lanShareEHList.size(); i++) {
			this.lanShareEHList.get(i).onSubFileShareListAns(map);
		}
		return true;
	}

	public void onInvite(HostInformation hostInformation, DiscussInfo discussInfo) {
		if (hostInformation.strMacAddr.equals(Public_Tools.getLocalMacAddress())) {
			return;
		}
		InviteDiscussInfo inviteDiscussInfo = new InviteDiscussInfo();
		inviteDiscussInfo.setHostInformation(hostInformation);
		inviteDiscussInfo.setDiscussInfo(discussInfo);
		this.mListInviteDiscuss.add(inviteDiscussInfo);
		if (this.myApp.g_strPromptAudio.equals(ContentTree.VIDEO_ID)) {
			this.m_player.play(this, R.raw.message, false);
		}
		for (int i = 0; i < this.mDiscussInviteEHList.size(); i++) {
			this.mDiscussInviteEHList.get(i).onInvite();
		}
	}

	public void onDiscussInfo() {
		for (int i = 0; i < this.mDiscussInfoEHList.size(); i++) {
			this.mDiscussInfoEHList.get(i).onDiscussInfo();
		}
	}

	public void onDiscussExit(String str, String str2) {
		DiscussInfo discussInfo;
		if (str2.equals(Public_Tools.getLocalMacAddress()) && (discussInfo = ((Protocol) this.m_DataSource.m_Protocol).mProtocol_Discuss.getDiscussInfo(str)) != null) {
			this.mListExitDiscuss.add("您被移出了讨论组，" + discussInfo.getStrName());
		}
		if (this.myApp.g_strPromptAudio.equals(ContentTree.VIDEO_ID)) {
			this.m_player.play(this, R.raw.message, false);
		}
		for (int i = 0; i < this.mDiscussExitEHList.size(); i++) {
			this.mDiscussExitEHList.get(i).onDiscussExit();
		}
	}

	@Override // android.app.Service
	public void onCreate() {
		this.myApp = (IpmsgApplication) getApplication();
		DataSource dataSource = new DataSource(this);
		this.m_DataSource = dataSource;
		dataSource.Init();
		HashMap map = new HashMap();
		map.put("name_text_grouplist_resource", getString(R.string.my_resource));
		this.m_resourceGroupList.add(map);
		HashMap map2 = new HashMap();
		map2.put("name_text_grouplist_resource", "鎴戠殑濂藉弸");
		this.m_resourceGroupList.add(map2);
		ArrayList<Map<String, String>> arrayList = new ArrayList<>();
		new HashMap();
		HashMap map3 = new HashMap();
		map3.put("name_text_childlist_resource", "鎴戠殑鍏变韩");
		arrayList.add(map3);
		HashMap map4 = new HashMap();
		map4.put("name_text_childlist_resource", "灞€鍩熺綉鍏变韩");
		arrayList.add(map4);
		this.m_resourceChildList.add(arrayList);
		this.m_resourceChildList.add(new ArrayList<>());
		this.m_player = new SoundPlayer();
		initDiscussList();
	}

	@Override // android.app.Service
	public IBinder onBind(Intent intent) {
		return this.localBinder;
	}

	public class LocalBinder extends Binder {
		public LocalBinder() {
		}

		public IpmsgService getService() {
			return IpmsgService.this;
		}
	}

	public void initDiscussList() {
		this.mListDiscuss.clear();
		this.mListDiscuss.addAll(DBHelper.getInstance(this).getDiscussInfoRecord("Exit = 0"));
	}

	public void addOrReplaceDiscuss(DiscussInfo discussInfo) {
		for (int i = 0; i < this.mListDiscuss.size(); i++) {
			if (this.mListDiscuss.get(i).getStrId().equals(discussInfo.getStrId())) {
				this.mListDiscuss.get(i).setStrName(discussInfo.getStrName());
				this.mListDiscuss.get(i).setStrsMember(discussInfo.getStrsMember());
				this.mListDiscuss.get(i).setLCreateTime(discussInfo.getLCreateTime());
				this.mListDiscuss.get(i).setLEndTime(discussInfo.getLEndTime());
				this.mListDiscuss.get(i).setBExit(discussInfo.isBExit());
				return;
			}
		}
		this.mListDiscuss.add(discussInfo);
	}

	public HostInformation getHostInfo2(String str) {
		for (int i = 0; i < printerList.size(); i++) {
			if (printerList.get(i).strMacAddr.equals(str)) {
				return printerList.get(i);
			}
		}
		return null;
	}

	public HostInformation getHostInfo(String str) {
		for (int i = 0; i < this.userList.size(); i++) {
			if (this.userList.get(i).strMacAddr != null && this.userList.get(i).strMacAddr.equals(str)) {
				return this.userList.get(i);
			}
		}
		return null;
	}

	public DiscussInfo getDiscussInfo(String str) {
		for (int i = 0; i < this.mListDiscuss.size(); i++) {
			if (this.mListDiscuss.get(i).getStrId() != null && this.mListDiscuss.get(i).getStrId().equals(str)) {
				return this.mListDiscuss.get(i);
			}
		}
		return null;
	}

	public HostInformation getHostInfoByIpPort(String str, int i) {
		for (int i2 = 0; i2 < this.userList.size(); i2++) {
			if (this.userList.get(i2).IpAddr.netAddr.getHostAddress().equals(str) && this.userList.get(i2).IpAddr.listenPort == i) {
				return this.userList.get(i2);
			}
		}
		return null;
	}

	public void addOrModifyHostInfo(HostInformation hostInformation) {
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		int i = 0;
		if (hostInfo == null) {
			this.userList.add(hostInformation);
			while (i < this.ehList.size()) {
				this.ehList.get(i).onAddOrModifyHostInfo(hostInformation, Global.UserHandleType.addUser);
				i++;
			}
			hostInfo = hostInformation;
		} else {
			hostInfo.groupName = hostInformation.groupName;
			hostInfo.IpAddr = hostInformation.IpAddr;
			hostInfo.pszHostName = hostInformation.pszHostName;
			hostInfo.pszUserName = hostInformation.pszUserName;
			hostInfo.strMacAddr = hostInformation.strMacAddr;
			hostInfo.headImage = hostInformation.headImage;
			while (i < this.ehList.size()) {
				this.ehList.get(i).onAddOrModifyHostInfo(hostInfo, Global.UserHandleType.modifyUser);
				i++;
			}
		}
		HostInformation hostInfo2 = getHostInfo2(hostInformation.strMacAddr);
		if (hostInfo2 == null) {
			if (hostInfo.strSharePrinter == null || hostInfo.strSharePrinter.length() <= 0) {
				return;
			}
			printerList.add(hostInformation);
			if (ChoicePrinterActivity.m_printerAdapter != null) {
				ChoicePrinterActivity.m_printerAdapter.notifyDataSetChanged();
				return;
			}
			return;
		}
		hostInfo2.groupName = hostInformation.groupName;
		hostInfo2.IpAddr = hostInformation.IpAddr;
		hostInfo2.pszHostName = hostInformation.pszHostName;
		hostInfo2.pszUserName = hostInformation.pszUserName;
		hostInfo2.strMacAddr = hostInformation.strMacAddr;
		hostInfo2.headImage = hostInformation.headImage;
		if (ChoicePrinterActivity.m_printerAdapter != null) {
			ChoicePrinterActivity.m_printerAdapter.notifyDataSetChanged();
		}
	}

	public void removeHostInfo(HostInformation hostInformation) {
		boolean z;
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		HostInformation hostInfo2 = getHostInfo2(hostInformation.strMacAddr);
		if (hostInfo != null) {
			ArrayList<MsgRecord> arrayList = this.fileMsgs.get(hostInfo.strMacAddr);
			if (arrayList != null) {
				z = false;
				for (int i = 0; i < arrayList.size(); i++) {
					if (arrayList.get(i).getFileId() != -1) {
						arrayList.get(i).setFileId(-1L);
						z = true;
					}
				}
			} else {
				z = false;
			}
			if (z) {
				for (int i2 = 0; i2 < this.ehList.size(); i2++) {
					this.ehList.get(i2).onAddMessage(hostInfo.strMacAddr, null);
				}
			}
			this.userList.remove(hostInfo);
			for (int i3 = 0; i3 < this.ehList.size(); i3++) {
				this.ehList.get(i3).onAddOrModifyHostInfo(hostInfo, Global.UserHandleType.removeUser);
			}
		}
		if (hostInfo2 != null) {
			printerList.remove(hostInfo2);
			try {
				if (ChoicePrinterActivity.m_printerAdapter != null) {
					if (Global.g_hostInformation != null && Global.g_hostInformation.strMacAddr.equals(hostInfo2.strMacAddr)) {
						if (printerList.size() > 0) {
							printerList.get(0).isChecked = true;
							Global.g_hostInformation = printerList.get(0);
							if (PrintActivity.s_textVChoicedPrinter != null) {
								PrintActivity.s_textVChoicedPrinter.setText(Global.g_hostInformation.pszUserName);
							}
						} else if (PrintActivity.s_textVChoicedPrinter != null) {
							PrintActivity.s_textVChoicedPrinter.setText(m_strPrintNotify);
							Global.g_hostInformation = null;
						}
					}
					ChoicePrinterActivity.m_printerAdapter.notifyDataSetChanged();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean processMessage(HostInformation hostInformation, String str, String str2, boolean z, boolean z2, long j) {
		if (str2 == null || str2.length() == 0) {
			return false;
		}
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		DiscussInfo discussInfo = null;
		if (hostInfo == null) {
			this.userList.add(hostInformation);
			for (int i = 0; i < this.ehList.size(); i++) {
				this.ehList.get(i).onAddOrModifyHostInfo(hostInformation, Global.UserHandleType.addUser);
			}
			hostInfo = hostInformation;
		}
		String str3 = hostInfo.strMacAddr;
		if (!str.isEmpty() && str.startsWith(Protocol_Discuss.smPreID)) {
			discussInfo = getDiscussInfo(str);
			if (discussInfo == null) {
				return false;
			}
			if (!z) {
				discussInfo.setBUnreadMsg(true);
			}
			str3 = str;
		} else if (this.myApp.currentActivity != null && !z && (!(this.myApp.currentActivity instanceof ChatActivity) || ((this.myApp.currentActivity instanceof ChatActivity) && !ChatActivity.strMacAddr.equals(hostInfo.strMacAddr)))) {
			hostInfo.unreadMsg = true;
		}
		MsgRecord msgRecord = new MsgRecord(z ? Public_Tools.getLocalMacAddress() : hostInformation.strMacAddr, Long.toString(this.msgId), str2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		this.msgId++;
		msgRecord.setSend(z);
		msgRecord.setnPackageID(j);
		parseMsgString(str2, msgRecord.screenShotList, z);
		if (this.myApp.g_bBackRuning && !z && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
			Public_Tools.showMsgNotification(this.myApp, hostInfo, discussInfo, msgRecord.bodyStr, R.drawable.msg_push);
		}
		ArrayList<MsgRecord> arrayList = this.messages.get(str3);
		if (arrayList == null) {
			ArrayList<MsgRecord> arrayList2 = new ArrayList<>();
			arrayList2.add(msgRecord);
			this.messages.put(str3, arrayList2);
		} else {
			arrayList.add(msgRecord);
		}
		if (msgRecord.screenShotList.size() > 0) {
			ArrayList<MsgRecord> arrayList3 = this.screenShotMessages.get(str3);
			if (arrayList3 == null) {
				ArrayList<MsgRecord> arrayList4 = new ArrayList<>();
				arrayList4.add(msgRecord);
				this.screenShotMessages.put(str3, arrayList4);
			} else {
				arrayList3.add(msgRecord);
			}
		}
		for (int i2 = 0; i2 < this.ehList.size(); i2++) {
			this.ehList.get(i2).onAddMessage(str3, msgRecord);
		}
		return true;
	}

	public void notifySendMsgFail(HostInformation hostInformation, long j) {
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		if (hostInfo == null) {
			this.userList.add(hostInformation);
			for (int i = 0; i < this.ehList.size(); i++) {
				this.ehList.get(i).onAddOrModifyHostInfo(hostInformation, Global.UserHandleType.addUser);
			}
			hostInfo = hostInformation;
		}
		ArrayList<MsgRecord> arrayList = this.messages.get(hostInfo.strMacAddr);
		if (arrayList == null) {
			arrayList = new ArrayList<>();
			this.messages.put(hostInfo.strMacAddr, arrayList);
		}
		for (int i2 = 0; i2 < arrayList.size(); i2++) {
			if (arrayList.get(i2).getnPackageID() == j) {
				MsgRecord msgRecord = new MsgRecord(hostInformation.strMacAddr, Long.toString(this.msgId), arrayList.get(i2).bodyStr, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				this.msgId++;
				msgRecord.setSend(true);
				msgRecord.setnPackageID(-1L);
				msgRecord.setFailed(true);
				arrayList.add(msgRecord);
				for (int i3 = 0; i3 < this.ehList.size(); i3++) {
					this.ehList.get(i3).onAddMessage(hostInfo.strMacAddr, msgRecord);
				}
				return;
			}
		}
	}

	public boolean processCancelSendFile(HostInformation hostInformation, long j, long j2) {
		ArrayList<MsgRecord> arrayList;
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		if (hostInfo == null || (arrayList = this.fileMsgs.get(hostInfo.strMacAddr)) == null) {
			return false;
		}
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).getFileId() == j2 && arrayList.get(i).getFileInfo().nPackageID == j) {
				MsgRecord msgRecord = arrayList.get(i);
				msgRecord.setFileId(-1L);
				msgRecord.setFileInfo(null);
				msgRecord.bodyStr = msgRecord.getFileName();
				String str = getString(R.string.other_side) + getString(R.string.terminate) + getString(R.string.file) + getString(R.string.transport);
				msgRecord.setDate(msgRecord.getDate() + "<br/><font size='+1' color='red'>" + str + "</font>");
				if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
					Public_Tools.showMsgNotification(this.myApp, hostInfo, null, str, R.drawable.msg_push);
				}
				for (int i2 = 0; i2 < this.ehList.size(); i2++) {
					this.ehList.get(i2).onModifyFileMessage(hostInfo.strMacAddr, msgRecord);
				}
				return true;
			}
		}
		return true;
	}

	public boolean processCancelRecvFile(HostInformation hostInformation, long j, long j2) {
		ArrayList<MsgRecord> arrayList;
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		if (hostInfo == null || (arrayList = this.fileMsgs.get(hostInfo.strMacAddr)) == null) {
			return false;
		}
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).getFileId() == j2 && arrayList.get(i).getFileInfo().nPackageID == j) {
				MsgRecord msgRecord = arrayList.get(i);
				msgRecord.setFileId(-1L);
				msgRecord.setFileInfo(null);
				msgRecord.bodyStr = msgRecord.getFileName();
				String str = getString(R.string.other_side) + getString(R.string.terminate) + getString(R.string.file) + getString(R.string.transport);
				msgRecord.setDate(msgRecord.getDate() + "<br/><font size='+1' color='red'>" + str + "</font>");
				if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
					Public_Tools.showMsgNotification(this.myApp, hostInfo, null, str, R.drawable.msg_push);
				}
				for (int i2 = 0; i2 < this.ehList.size(); i2++) {
					this.ehList.get(i2).onModifyFileMessage(hostInfo.strMacAddr, msgRecord);
				}
				return true;
			}
		}
		return true;
	}

	public boolean notifySendFileFail(HostInformation hostInformation, long j) {
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		if (hostInfo == null) {
			return false;
		}
		ArrayList<MsgRecord> arrayList = this.messages.get(hostInfo.strMacAddr);
		ArrayList<MsgRecord> arrayList2 = this.fileMsgs.get(hostInfo.strMacAddr);
		if (arrayList == null) {
			this.messages.put(hostInfo.strMacAddr, new ArrayList<>());
		}
		if (arrayList2 == null) {
			arrayList2 = new ArrayList<>();
			this.fileMsgs.put(hostInfo.strMacAddr, arrayList2);
		}
		for (int i = 0; i < arrayList2.size(); i++) {
			if (arrayList2.get(i).getFileInfo() != null && arrayList2.get(i).getFileInfo().nPackageID == j) {
				MsgRecord msgRecord = arrayList2.get(i);
				msgRecord.setFileId(-1L);
				msgRecord.setFileInfo(null);
				msgRecord.bodyStr = msgRecord.getFileName();
				String str = getString(R.string.io_error) + " " + getString(R.string.file) + getString(R.string.transport) + getString(R.string.failed);
				msgRecord.setDate(msgRecord.getDate() + "<br/><font size='+1' color='red'>" + str + "</font>");
				if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
					Public_Tools.showMsgNotification(this.myApp, hostInfo, null, str, R.drawable.msg_push);
				}
				for (int i2 = 0; i2 < this.ehList.size(); i2++) {
					this.ehList.get(i2).onModifyFileMessage(hostInfo.strMacAddr, msgRecord);
				}
			}
		}
		return true;
	}

	public boolean processFile(HostInformation hostInformation, Vector<FileInformation> vector, boolean z, boolean z2) {
		DiscussInfo discussInfo;
		boolean zRecvFile;
		ArrayList<MsgRecord> arrayList;
		boolean zRecvFile2;
		Vector<FileInformation> vector2 = vector;
		HostInformation hostInfo = getHostInfo(hostInformation.strMacAddr);
		if (hostInfo == null) {
			return false;
		}
		String str = hostInformation.strMacAddr;
		if (vector2.get(0).mStrDiscussID == null || !vector2.get(0).mStrDiscussID.startsWith(Protocol_Discuss.smPreID)) {
			discussInfo = null;
		} else {
			str = vector2.get(0).mStrDiscussID;
			discussInfo = getDiscussInfo(str);
			if (discussInfo == null) {
				return false;
			}
		}
		ArrayList<MsgRecord> arrayList2 = this.messages.get(str);
		ArrayList<MsgRecord> arrayList3 = this.fileMsgs.get(str);
		ArrayList<MsgRecord> arrayList4 = this.screenShotMessages.get(str);
		if (arrayList2 == null) {
			arrayList2 = new ArrayList<>();
			this.messages.put(str, arrayList2);
		}
		if (arrayList3 == null) {
			arrayList3 = new ArrayList<>();
			this.fileMsgs.put(str, arrayList3);
		}
		if (arrayList4 == null) {
			arrayList4 = new ArrayList<>();
			this.screenShotMessages.put(str, arrayList4);
		}
		int i = 0;
		while (i < vector.size()) {
			FileInformation fileInformation = vector2.get(i);
			if ((fileInformation.nFileAttr & 4) == 4) {
				try {
					zRecvFile = this.myApp.ipmsgService.m_DataSource.m_Protocol.recvFile(fileInformation, hostInformation, this.myApp.ipmsgService);
				} catch (IOException unused) {
					zRecvFile = false;
				}
				if (!zRecvFile) {
					for (int i2 = 0; i2 < arrayList4.size(); i2++) {
						MsgRecord msgRecord = arrayList4.get(i2);
						boolean z3 = false;
						for (int i3 = 0; i3 < msgRecord.screenShotList.size(); i3++) {
							if (msgRecord.screenShotList.get(i3).FileName.equals(fileInformation.FileName)) {
								msgRecord.screenShotList.get(i3).fileTranStatus = Public_Def.TransStatus.Trans_Error;
								z3 = true;
							}
						}
						if (z3) {
							for (int i4 = 0; i4 < this.ehList.size(); i4++) {
								this.ehList.get(i4).onModifyFileMessage(str, msgRecord);
							}
						}
					}
				}
			} else {
				MsgRecord msgRecord2 = new MsgRecord(hostInfo.strMacAddr, Long.toString(this.myApp.ipmsgService.msgId), fileInformation.FileName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				this.msgId++;
				msgRecord2.setSend(z);
				msgRecord2.setFileId(fileInformation.Id);
				msgRecord2.setFile(true);
				msgRecord2.setFileFullPath(fileInformation.Path);
				msgRecord2.setFileName(fileInformation.FileName);
				msgRecord2.setFileTranStatus(fileInformation.status);
				msgRecord2.setSize(fileInformation.size);
				msgRecord2.setFileInfo(fileInformation);
				msgRecord2.setnFileAttr(fileInformation.nFileAttr);
				arrayList2.add(msgRecord2);
				arrayList3.add(msgRecord2);
				if (this.myApp.g_bBackRuning && !z && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
					Public_Tools.showMsgNotification(this.myApp, hostInfo, discussInfo, msgRecord2.bodyStr, R.drawable.msg_push);
				}
				for (int i5 = 0; i5 < this.ehList.size(); i5++) {
					this.ehList.get(i5).onAddMessage(str, msgRecord2);
				}
				if ((this.myApp.g_strAutoRecvFile.equals(ContentTree.VIDEO_ID) && !z) || (fileInformation.nFileAttr & 16) == 16) {
					try {
						zRecvFile2 = this.myApp.ipmsgService.m_DataSource.m_Protocol.recvFile(fileInformation, hostInformation, this.myApp.ipmsgService);
					} catch (IOException unused2) {
						zRecvFile2 = false;
					}
					if (!zRecvFile2) {
						arrayList = arrayList2;
						msgRecord2.setFileId(-1L);
						msgRecord2.setFileInfo(null);
						msgRecord2.bodyStr = msgRecord2.getFileName();
						String str2 = getString(R.string.file) + getString(R.string.transport) + getString(R.string.failed);
						msgRecord2.setDate(msgRecord2.getDate() + "<br/><font size='+1' color='red'>" + str2 + "</font>");
						if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
							Public_Tools.showMsgNotification(this.myApp, hostInfo, discussInfo, str2, R.drawable.msg_push);
						}
						for (int i6 = 0; i6 < this.ehList.size(); i6++) {
							this.ehList.get(i6).onModifyFileMessage(str, msgRecord2);
						}
					}
				}
				i++;
				arrayList2 = arrayList;
				vector2 = vector;
			}
			arrayList = arrayList2;
			i++;
			arrayList2 = arrayList;
			vector2 = vector;
		}
		return true;
	}

	@Override // com.netfeige.protocol.IFileTransNotify
	public void transFile(FileInformation fileInformation, long j, String str) {
		ArrayList<Object> operateInstance = getOperateInstance(fileInformation);
		if (operateInstance != null) {
			String str2 = (String) operateInstance.get(0);
			HostInformation hostInfo = getHostInfo(str2);
			DiscussInfo discussInfo = getDiscussInfo(str2);
			MsgRecord msgRecord = (MsgRecord) operateInstance.get(1);
			if ((fileInformation.nFileAttr & 4) == 4) {
				if (fileInformation.status != Public_Def.TransStatus.Trans_Done) {
					return;
				}
				for (int i = 0; i < this.ehList.size(); i++) {
					if (!msgRecord.isSend()) {
						this.ehList.get(i).onModifyFileList(Public_Tools.m_strFeigeImages + File.separator + fileInformation.FileName, false);
					}
				}
				for (int i2 = 0; i2 < msgRecord.screenShotList.size(); i2++) {
					if (msgRecord.screenShotList.get(i2).FileName.equals(fileInformation.FileName)) {
						msgRecord.screenShotList.get(i2).fileTranStatus = fileInformation.status;
					}
				}
			} else {
				msgRecord.setFileTranStatus(fileInformation.status);
				msgRecord.setnProgress(Public_Tools.getProgress(j, fileInformation.size));
				msgRecord.setStrCurrSpeed(str);
				if (fileInformation.status == Public_Def.TransStatus.Trans_Done) {
					for (int i3 = 0; i3 < this.ehList.size(); i3++) {
						if (!msgRecord.isSend()) {
							this.ehList.get(i3).onModifyFileList(fileInformation.FileName, false);
						}
					}
					String string = getResources().getString(R.string.file);
					String str3 = (msgRecord.isSend() ? string + getResources().getString(R.string.send) : string + getResources().getString(R.string.receive)) + getResources().getString(R.string.success);
					msgRecord.setFileId(-1L);
					msgRecord.setFileInfo(null);
					msgRecord.setDate(msgRecord.getDate() + "<br/><font size='+1' color='red'>" + str3 + "</font>");
					if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
						Public_Tools.showMsgNotification(this.myApp, hostInfo, discussInfo, str3, R.drawable.msg_push);
					}
				}
			}
			for (int i4 = 0; i4 < this.ehList.size(); i4++) {
				this.ehList.get(i4).onModifyFileMessage(str2, msgRecord);
			}
		}
	}

	@Override // com.netfeige.protocol.IFileTransNotify
	public void transDir(FileInformation fileInformation, long j, FileInformation fileInformation2, long j2, String str) {
		ArrayList<Object> operateInstance;
		if ((fileInformation.nFileAttr & 4) == 4 || (operateInstance = getOperateInstance(fileInformation)) == null) {
			return;
		}
		String str2 = (String) operateInstance.get(0);
		HostInformation hostInfo = getHostInfo(str2);
		DiscussInfo discussInfo = getDiscussInfo(str2);
		MsgRecord msgRecord = (MsgRecord) operateInstance.get(1);
		msgRecord.setFileTranStatus(fileInformation.status);
		if (fileInformation2 != null) {
			msgRecord.setnProgress(Public_Tools.getProgress(j, fileInformation.size));
			msgRecord.bodyStr = fileInformation2.FileName;
			msgRecord.setSize(fileInformation.size);
		} else {
			msgRecord.setnProgress(Public_Tools.getProgress(j, fileInformation.size));
			msgRecord.bodyStr = fileInformation.FileName;
			msgRecord.setSize(j);
		}
		msgRecord.setStrCurrSpeed(str);
		if (fileInformation.status == Public_Def.TransStatus.Trans_Done) {
			for (int i = 0; i < this.ehList.size(); i++) {
				if (!msgRecord.isSend()) {
					this.ehList.get(i).onModifyFileList(fileInformation.FileName, true);
				}
			}
			String string = getResources().getString(R.string.file);
			String str3 = (msgRecord.isSend() ? string + getResources().getString(R.string.send) : string + getResources().getString(R.string.receive)) + getResources().getString(R.string.success);
			msgRecord.setFileId(-1L);
			msgRecord.setFileInfo(null);
			msgRecord.bodyStr = msgRecord.getFileName();
			msgRecord.setDate(msgRecord.getDate() + "<br/><font size='+1' color='red'>" + str3 + "</font>");
			if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
				Public_Tools.showMsgNotification(this.myApp, hostInfo, discussInfo, str3, R.drawable.msg_push);
			}
		}
		for (int i2 = 0; i2 < this.ehList.size(); i2++) {
			this.ehList.get(i2).onModifyFileMessage(str2, msgRecord);
		}
	}

	@Override // com.netfeige.protocol.IFileTransNotify
	public void reNamed(FileInformation fileInformation, FileInformation fileInformation2) {
		ArrayList<Object> operateInstance = getOperateInstance(fileInformation);
		if (operateInstance != null) {
			String str = (String) operateInstance.get(0);
			MsgRecord msgRecord = (MsgRecord) operateInstance.get(1);
			msgRecord.setFileTranStatus(fileInformation2.status);
			msgRecord.bodyStr = fileInformation2.FileName;
			msgRecord.setFileFullPath(fileInformation2.Path);
			msgRecord.setFileName(fileInformation2.FileName);
			msgRecord.setFileInfo(fileInformation2);
			Public_Tools.showToast(this, getResources().getString(R.string.file) + fileInformation.FileName + "名字重复;自动命名为\"" + fileInformation2.FileName + "\"", 0);
			for (int i = 0; i < this.ehList.size(); i++) {
				this.ehList.get(i).onModifyFileMessage(str, msgRecord);
			}
		}
	}

	@Override // com.netfeige.protocol.IFileTransNotify
	public void transException(FileInformation fileInformation, Exception exc) {
		ArrayList<Object> operateInstance = getOperateInstance(fileInformation);
		if (operateInstance != null) {
			String str = (String) operateInstance.get(0);
			HostInformation hostInfo = getHostInfo(str);
			DiscussInfo discussInfo = getDiscussInfo(str);
			MsgRecord msgRecord = (MsgRecord) operateInstance.get(1);
			if ((fileInformation.nFileAttr & 4) == 4) {
				for (int i = 0; i < msgRecord.screenShotList.size(); i++) {
					if (msgRecord.screenShotList.get(i).FileName.equals(fileInformation.FileName)) {
						msgRecord.screenShotList.get(i).fileTranStatus = Public_Def.TransStatus.Trans_Error;
					}
				}
			} else {
				if (exc.getClass().getName().toString().equals("java.io.FileNotFoundException")) {
					Public_Tools.showToast(this, getResources().getString(R.string.file_error), 1);
				} else if (exc.getClass().getName().toString().equals("java.io.IOException")) {
					Public_Tools.showToast(this, getResources().getString(R.string.io_error), 1);
				} else if (exc instanceof Public_Def.SDCardNoAvailaleSizeException) {
					Public_Tools.showToast(this, getResources().getString(R.string.sdcard_noVailaleSize), 1);
				}
				if (msgRecord.isSend()) {
					this.m_DataSource.m_Protocol.cancelSendFile(msgRecord.getFileInfo(), hostInfo);
				} else {
					this.m_DataSource.m_Protocol.cancelRecvFile(msgRecord.getFileInfo(), hostInfo);
				}
				msgRecord.setFileId(-1L);
				msgRecord.setFileInfo(null);
				msgRecord.bodyStr = msgRecord.getFileName();
				String str2 = getResources().getString(R.string.file) + getResources().getString(R.string.transport) + getResources().getString(R.string.failed);
				msgRecord.setDate(msgRecord.getDate() + "<br/><font size='+1' color='red'>" + str2 + "</font>");
				if (this.myApp.g_bBackRuning && this.myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
					Public_Tools.showMsgNotification(this.myApp, hostInfo, discussInfo, str2, R.drawable.msg_push);
				}
			}
			for (int i2 = 0; i2 < this.ehList.size(); i2++) {
				this.ehList.get(i2).onModifyFileMessage(str, msgRecord);
			}
		}
	}

	public ArrayList<Object> getOperateInstance(FileInformation fileInformation) {
		if ((fileInformation.nFileAttr & 4) == 4) {
			if (this.screenShotMessages.size() <= 0) {
				return null;
			}
			for (Map.Entry<String, ArrayList<MsgRecord>> entry : this.screenShotMessages.entrySet()) {
				String key = entry.getKey();
				ArrayList<MsgRecord> value = entry.getValue();
				for (int i = 0; i < value.size(); i++) {
					MsgRecord msgRecord = value.get(i);
					for (int i2 = 0; i2 < msgRecord.screenShotList.size(); i2++) {
						if (msgRecord.screenShotList.get(i2).FileName.equals(fileInformation.FileName)) {
							ArrayList<Object> arrayList = new ArrayList<>();
							arrayList.add(key);
							arrayList.add(msgRecord);
							return arrayList;
						}
					}
				}
			}
			return null;
		}
		if (this.fileMsgs.size() <= 0) {
			return null;
		}
		for (Map.Entry<String, ArrayList<MsgRecord>> entry2 : this.fileMsgs.entrySet()) {
			String key2 = entry2.getKey();
			ArrayList<MsgRecord> value2 = entry2.getValue();
			for (int i3 = 0; i3 < value2.size(); i3++) {
				MsgRecord msgRecord2 = value2.get(i3);
				if (msgRecord2.getFileId() == fileInformation.Id && msgRecord2.getFileInfo().nPackageID == fileInformation.nPackageID) {
					ArrayList<Object> arrayList2 = new ArrayList<>();
					arrayList2.add(key2);
					arrayList2.add(msgRecord2);
					return arrayList2;
				}
			}
		}
		return null;
	}

	private void parseMsgString(String str, List<ScreenShot> list, boolean z) {
		int length = str.length();
		int i = 0;
		while (i < length) {
			int iIndexOf = str.indexOf("[ekimg]");
			if (iIndexOf == -1) {
				return;
			}
			int iIndexOf2 = str.indexOf("[/ekimg]", iIndexOf + 1);
			if (iIndexOf > 0) {
				i += iIndexOf;
			}
			if (iIndexOf2 == -1) {
				return;
			}
			list.add(new ScreenShot(str.substring(iIndexOf + 7, iIndexOf2), z ? Public_Def.TransStatus.Trans_Done : Public_Def.TransStatus.Trans_Ready));
			int i2 = iIndexOf2 + 8;
			i += (i2 - iIndexOf) + 1;
			str = str.substring(i2);
		}
	}

	public void onReject(String str, HostInformation hostInformation) {
		processMessage(hostInformation, str, hostInformation.pszUserName + "拒绝了讨论组邀请", false, false, Public_Tools.getCurrentTimeMillis());
	}

	public void onDiscussAdd(String str, String str2) {
		HostInformation hostInfo = getHostInfo(str2);
		if (hostInfo != null) {
			processMessage(hostInfo, str, hostInfo.pszUserName + "鍔犲叆浜嗚璁虹粍", false, false, Public_Tools.getCurrentTimeMillis());
		}
	}
}

