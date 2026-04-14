package com.netfeige.protocol;

import android.content.Context;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.netfeige.R;
import com.netfeige.common.DBHelper;
import com.netfeige.common.DiscussExeStatus;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.FileInfo;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HostInformation;
import com.netfeige.common.IUpdateNotify;
import com.netfeige.common.LanSharedItem;
import com.netfeige.common.MsgInformation;
import com.netfeige.common.PlatformType;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.ShareFiles;
import com.netfeige.common.SharePassword;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.protocol.ProPackage;
import com.netfeige.service.IpmsgService;
import com.netfeige.transport.FileTransManager;
import com.netfeige.transport.ITransNotify;
import com.netfeige.transport.TransFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class Protocol implements IProtocol {
	private static IProtocol m_Protocol = null;
	private static final int m_nFileIDBit = 1;
	private UILopperFreighter m_UILopperFreighter = new UILopperFreighter();
	private Context m_Context = null;
	private CmdController m_MsgController = null;
	private IDataConfig m_DataConfig = null;
	private IUpdateNotify m_UpdateNotify = null;
	private ProPackage m_proPackage = null;
	private BroadcastRunnableImp m_broadcastRunnableImp = new BroadcastRunnableImp();
	public Protocol_Discuss mProtocol_Discuss = null;
	private final int UMID = 1000005;
	private String m_strContext = null;

	@Override // com.netfeige.protocol.IProtocol
	public void Destroy() {
	}

	public static IProtocol getInstance() {
		if (m_Protocol == null) {
			m_Protocol = new Protocol();
		}
		return m_Protocol;
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean Init(IProtocolNotify iProtocolNotify, Context context) throws Public_Def.WifiConnectFailException, IOException {
		if (context == null) {
			return false;
		}
		this.m_Context = context;
		this.m_UILopperFreighter.Init(iProtocolNotify);
		IDataConfig dataConfig = DataConfig.getInstance(this.m_Context);
		this.m_DataConfig = dataConfig;
		if (dataConfig == null) {
			return false;
		}
		this.m_MsgController = new CmdController(this.m_Context);
		this.mProtocol_Discuss = new Protocol_Discuss(this.m_Context, this.m_MsgController, this.m_UILopperFreighter);
		boolean zBindMessage = this.m_MsgController.BindMessage(1, new DecodeEntryService()) & this.m_MsgController.BindMessage(2, new DecodeExitService()) & this.m_MsgController.BindMessage(3, new DecodeAnsEntry()) & this.m_MsgController.BindMessage(32, new DecodeSendMsg()) & this.m_MsgController.BindMessage(Public_MsgID.IPMSG_TCP_HOLE_PUNCHING_CONSULT, new DecodeReserveLinkReq()) & this.m_MsgController.BindMessage(230, new DecodeCancelRecvFile()) & this.m_MsgController.BindMessage(232, new DecodeCancelSendFile()) & this.m_MsgController.BindMessage(R.styleable.AppCompatTheme_windowFixedWidthMajor, new DecodePrintAnswer()) & this.m_MsgController.BindMessage(R.styleable.AppCompatTheme_windowFixedWidthMinor, new DecodePrintRefused()) & this.m_MsgController.BindMessage(R.styleable.AppCompatTheme_windowMinWidthMajor, new DecodePrintTimeout()) & this.m_MsgController.BindMessage(R.styleable.AppCompatTheme_windowMinWidthMinor, new DecodePrintFinish()) & this.m_MsgController.BindMessage(PlatformType.WIN_8_32, new DecodeShareListQuery()) & this.m_MsgController.BindMessage(PlatformType.WIN_8_64, new DecodeFileShareAnswer()) & this.m_MsgController.BindMessage(147, new DecodeSubShareListQuery()) & this.m_MsgController.BindMessage(148, new DecodeSubFileShareAnswer()) & this.m_MsgController.BindMessage(146, new DecodeRootFileDLQuery()) & this.m_MsgController.BindMessage(149, new DecodeSubFileDLQuery()) & this.m_MsgController.BindMessage(240, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(241, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(242, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(243, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(244, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(245, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(246, this.mProtocol_Discuss) & this.m_MsgController.BindMessage(247, this.mProtocol_Discuss);
		this.m_MsgController.init();
		new ScanTimer().start();
		new DiscussReExeThread().start();
		return zBindMessage;
	}

	@Override // com.netfeige.protocol.IProtocol
	public synchronized void entryService(ArrayList<HostInformation> arrayList, boolean z) {
		String hexString;
		if ((m_Protocol == null) || (this.m_DataConfig == null)) {
			return;
		}
		if (z && isBroadcasting()) {
			this.m_broadcastRunnableImp.interrupt();
		} else if (isBroadcasting()) {
			return;
		}
		HostInformation localHostInfo = Public_Tools.getLocalHostInfo();
		if (localHostInfo == null) {
			return;
		}
		if (getScreenSize()) {
			hexString = Integer.toHexString(PlatformType.PAD_UNKNOWN);
		} else {
			hexString = Integer.toHexString(8192);
		}
		this.m_broadcastRunnableImp.setM_broadcastProPackage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, localHostInfo, 1025L, localHostInfo.pszUserName + Public_MsgID.CUTAPART + localHostInfo.groupName + Public_MsgID.CUTAPART + Public_Tools.getLocalMacAddress() + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + hexString + Public_MsgID.CUTAPART + localHostInfo.IpAddr.netAddr.getHostAddress() + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + localHostInfo.headImage + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART, arrayList));
		this.m_broadcastRunnableImp.start();
	}

	private class DecodeEntryService implements ITransNotify {
		private DecodeEntryService() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			Protocol.this.EncodeAnsEntry(proPackage.HostInfo);
			Protocol.this.m_UILopperFreighter.notifyEntryService(Protocol.this.addHostInfo(proPackage.HostInfo, proPackage.strAdditionalSection));
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean EncodeAnsEntry(HostInformation hostInformation) {
		HostInformation localHostInfo;
		String hexString;
		if (hostInformation == null || (localHostInfo = Public_Tools.getLocalHostInfo()) == null) {
			return false;
		}
		if (getScreenSize()) {
			hexString = Integer.toHexString(PlatformType.PAD_UNKNOWN);
		} else {
			hexString = Integer.toHexString(8192);
		}
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 3L, localHostInfo.pszUserName + Public_MsgID.CUTAPART + localHostInfo.groupName + Public_MsgID.CUTAPART + Public_Tools.getLocalMacAddress() + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + hexString + Public_MsgID.CUTAPART + localHostInfo.IpAddr.netAddr.getHostAddress() + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART + localHostInfo.headImage + Public_MsgID.CUTAPART + Public_MsgID.CUTAPART));
	}

	private class DecodeAnsEntry implements ITransNotify {
		private DecodeAnsEntry() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			Protocol.this.m_UILopperFreighter.notifyAnsEntry(Protocol.this.addHostInfo(proPackage.HostInfo, proPackage.strAdditionalSection));
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	/* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
	/* JADX WARN: Removed duplicated region for block: B:17:0x001b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
	public com.netfeige.common.HostInformation addHostInfo(com.netfeige.common.HostInformation r3, java.lang.String r4) {
        /*
            r2 = this;
            java.lang.String r0 = "\u0000"
            java.lang.String[] r4 = r4.split(r0)
            int r0 = r4.length
            switch(r0) {
                case 1: goto L3f;
                case 2: goto L3a;
                case 3: goto L35;
                case 4: goto L35;
                case 5: goto L35;
                case 6: goto L35;
                case 7: goto L30;
                case 8: goto L2b;
                case 9: goto L11;
                case 10: goto La;
                case 11: goto Lb;
                case 12: goto Lb;
                case 13: goto Lb;
                case 14: goto Lb;
                case 15: goto Lb;
                case 16: goto Lb;
                case 17: goto Lb;
                case 18: goto Lb;
                case 19: goto Lb;
                case 20: goto Lb;
                default: goto La;
            }
        La:
            goto L44
        Lb:
            r0 = 10
            r0 = r4[r0]
            r3.headImage = r0
        L11:
            r0 = 8
            r1 = r4[r0]
            int r1 = r1.length()
            if (r1 == 0) goto L2b
            com.netfeige.common.HostInformation$IP_ADDR r1 = r3.IpAddr     // Catch: java.net.UnknownHostException -> L26
            r0 = r4[r0]     // Catch: java.net.UnknownHostException -> L26
            java.net.InetAddress r0 = java.net.InetAddress.getByName(r0)     // Catch: java.net.UnknownHostException -> L26
            r1.realNetAddr = r0     // Catch: java.net.UnknownHostException -> L26
            goto L2b
        L26:
            com.netfeige.common.HostInformation$IP_ADDR r0 = r3.IpAddr
            r1 = 0
            r0.realNetAddr = r1
        L2b:
            r0 = 7
            r0 = r4[r0]
            r3.platformType = r0
        L30:
            r0 = 6
            r0 = r4[r0]
            r3.strSharePrinter = r0
        L35:
            r0 = 2
            r0 = r4[r0]
            r3.strMacAddr = r0
        L3a:
            r0 = 1
            r0 = r4[r0]
            r3.groupName = r0
        L3f:
            r0 = 0
            r4 = r4[r0]
            r3.pszUserName = r4
        L44:
            return r3
        */
		throw new UnsupportedOperationException("Method not decompiled: com.netfeige.protocol.Protocol.addHostInfo(com.netfeige.common.HostInformation, java.lang.String):com.netfeige.common.HostInformation");
	}

	@Override // com.netfeige.protocol.IProtocol
	public void exitService() {
		HostInformation localHostInfo = Public_Tools.getLocalHostInfo();
		if (localHostInfo == null || this.m_MsgController.m_Transport == null) {
			return;
		}
		this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, localHostInfo, 1026L, localHostInfo.pszUserName));
	}

	private class DecodeExitService implements ITransNotify {
		private DecodeExitService() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			Protocol.this.m_UILopperFreighter.notifyExitService(proPackage.HostInfo);
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public long sendMsg(String str, HostInformation hostInformation) {
		if (str == null || hostInformation == null) {
			return -1L;
		}
		ProPackage proPackageMakeProPackage = Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 288L, str);
		if (this.m_MsgController.m_Transport.SendMessage(proPackageMakeProPackage)) {
			return proPackageMakeProPackage.nPackageID;
		}
		return -1L;
	}

	private class DecodeSendMsg implements ITransNotify {
		private DecodeSendMsg() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (proPackage == null) {
				return;
			}
			try {
				if (0 != (proPackage.nCommandID & 256)) {
					Protocol.this.EncodeRecMsg(proPackage);
				}
				String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
				if ((proPackage.nCommandID & 512) == 512) {
					if (strArrSplit.length <= 1 || 0 == (proPackage.nCommandID & 512)) {
						return;
					}
					if (Public_Def.TransStatus.Trans_SendFailed == proPackage.Status) {
						Protocol.this.m_UILopperFreighter.notifySendFileFail(proPackage.HostInfo, proPackage.nPackageID);
						return;
					}
					for (int i = 1; i < strArrSplit.length; i++) {
						Protocol.this.DecodeFileMsg(proPackage.nCommandID, proPackage.nPackageID, proPackage.HostInfo, strArrSplit[i]);
					}
					return;
				}
				if (proPackage.Status == Public_Def.TransStatus.Trans_SendFailed) {
					Protocol.this.m_UILopperFreighter.notifySendMsgFail(proPackage.HostInfo, proPackage.nPackageID);
					return;
				}
				if (strArrSplit[0].indexOf("[rich]") >= 0) {
					strArrSplit[0] = strArrSplit[0].substring(0, strArrSplit[0].indexOf("[rich]"));
				}
				if (strArrSplit.length == 1) {
					Protocol.this.m_UILopperFreighter.notifySendMsg(proPackage.HostInfo, new MsgInformation(strArrSplit[0], "", proPackage.nPackageID, proPackage.Status));
				} else if (strArrSplit.length == 2) {
					Protocol.this.m_UILopperFreighter.notifySendMsg(proPackage.HostInfo, new MsgInformation(strArrSplit[0], strArrSplit[1], proPackage.nPackageID, proPackage.Status));
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean DecodeFileMsg(long j, long j2, HostInformation hostInformation, String str) {
		if (str == null || hostInformation == null) {
			return false;
		}
		this.m_UILopperFreighter.notifySendFile(hostInformation, makeFileInfofromAddSection(str, j2), false);
		return true;
	}

	private Vector<FileInformation> makeFileInfofromAddSection(String str, long j) {
		if (str == null) {
			return null;
		}
		Vector<FileInformation> vector = new Vector<>();
		for (String str2 : str.split(Public_MsgID.fileEnd)) {
			FileInformation fileInformation = new FileInformation();
			vector.add(fileInformation);
			String[] strArrSplit = str2.split(Public_MsgID.PRO_SPACE);
			fileInformation.nPackageID = j;
			fileInformation.Id = Long.parseLong(strArrSplit[0]);
			fileInformation.strOriginalFileName = strArrSplit[1];
			fileInformation.FileName = fileInformation.strOriginalFileName;
			fileInformation.size = Long.valueOf(strArrSplit[2], 16).longValue();
			fileInformation.nFileAttr = Integer.parseInt(strArrSplit[4]);
			if ((fileInformation.nFileAttr & 4) == 4) {
				fileInformation.Path += File.separator + Public_Tools.m_strFeigeImages;
			}
			if (strArrSplit.length >= 9) {
				fileInformation.mStrDiscussID = strArrSplit[8];
			}
		}
		return vector;
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean EncodeRecMsg(ProPackage proPackage) {
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, proPackage.HostInfo, 33L, Long.toString(proPackage.nPackageID)));
	}

	public FileInformation sendFile(String str, String str2, HostInformation hostInformation, IFileTransNotify iFileTransNotify, int i) {
		if ((hostInformation == null) || (str2 == null)) {
			return null;
		}
		ProPackage proPackage = new ProPackage();
		proPackage.Type = ProPackage.PackageType.UDP;
		proPackage.HostInfo = hostInformation;
		proPackage.nPackageID = Public_Tools.getCurrentTimeMillis();
		if (i == 0) {
			proPackage.nCommandID = 2097440L;
		} else {
			proPackage.nCommandID = 6291744L;
		}
		FileInformation fileInformationMakeFileInfo = makeFileInfo((Vector<String>) null, str2, proPackage.nPackageID, iFileTransNotify, hostInformation, str);
		proPackage.strAdditionalSection = Public_MsgID.CUTAPART + makeFileSection(fileInformationMakeFileInfo, i);
		Log.v("mylog", "send file: " + hostInformation.IpAddr.netAddr.getHostAddress() + " , " + makeFileSection(fileInformationMakeFileInfo, i));
		if (this.m_MsgController.m_Transport.sendFile(proPackage, fileInformationMakeFileInfo)) {
			return fileInformationMakeFileInfo;
		}
		return null;
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean sendReserveLinkReq(HostInformation hostInformation, int i, FileInformation fileInformation) {
		if (((hostInformation == null) | (i == 0)) || (fileInformation == null)) {
			return false;
		}
		ProPackage proPackage = new ProPackage();
		proPackage.Type = ProPackage.PackageType.UDP;
		proPackage.HostInfo = hostInformation;
		proPackage.nPackageID = Public_Tools.getCurrentTimeMillis();
		proPackage.nCommandID = -2147483254L;
		proPackage.strAdditionalSection = i + Public_MsgID.CUTAPART + fileInformation.Id + Public_MsgID.CUTAPART + 1 + Public_MsgID.CUTAPART + fileInformation.nPackageID;
		return this.m_MsgController.m_Transport.SendMessage(proPackage);
	}

	private class DecodeReserveLinkReq implements ITransNotify {
		private DecodeReserveLinkReq() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
			if (proPackage.Status == Public_Def.TransStatus.Trans_SendFailed) {
				TransFile.FileRecvThread fileRecvThread = (TransFile.FileRecvThread) FileTransManager.getInstance().getFileTransThread(Long.parseLong(strArrSplit[3]), Long.parseLong(strArrSplit[1]));
				if (fileRecvThread != null) {
					fileRecvThread.stop();
					return;
				}
				return;
			}
			FileTransManager.getInstance().ReserveConnectClient(proPackage, strArrSplit);
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public long sendFile(String str, Vector<String> vector, HostInformation hostInformation, IFileTransNotify iFileTransNotify, Vector<String> vector2, int i) {
		DiscussInfo discussInfo;
		HostInformation hostInfo;
		Vector vector3 = new Vector();
		int i2 = 0;
		if (hostInformation != null) {
			HostInformation hostInfo2 = ((IpmsgService) this.m_Context).getHostInfo(hostInformation.strMacAddr);
			if (hostInfo2 == null) {
				Context context = this.m_Context;
				Public_Tools.showToast(context, context.getString(R.string.offline_prompt), 0);
				return -1L;
			}
			vector3.add(hostInfo2);
		} else if (str != null && str.startsWith(Protocol_Discuss.smPreID) && (discussInfo = this.mProtocol_Discuss.getDiscussInfo(str)) != null) {
			String localMacAddress = Public_Tools.getLocalMacAddress();
			String[] strArrSplit = discussInfo.getStrsMember().split(Public_MsgID.PRO_SPACE_GROUP);
			for (int i3 = 0; i3 < strArrSplit.length; i3++) {
				if (!strArrSplit[i3].equals(localMacAddress) && (hostInfo = ((IpmsgService) this.m_Context).getHostInfo(strArrSplit[i3])) != null) {
					vector3.add(hostInfo);
				}
			}
		}
		Vector<String> vector4 = new Vector<>();
		if (vector != null) {
			for (int i4 = 0; i4 < vector.size(); i4++) {
				vector4.add(vector.get(i4));
				if (str != null && str.startsWith(Protocol_Discuss.smPreID)) {
					String strSubstring = vector.get(i4);
					int iLastIndexOf = strSubstring.lastIndexOf(File.separator);
					if (iLastIndexOf >= 0) {
						strSubstring = strSubstring.substring(iLastIndexOf + 1, strSubstring.length());
					}
					((IpmsgService) this.m_Context).processMessage(Public_Tools.getLocalHostInfo(), str, "您已发送文件\"" + strSubstring + "\"", true, false, System.currentTimeMillis());
				}
			}
		}
		ProPackage proPackage = new ProPackage();
		while (i2 < vector3.size()) {
			ProPackage proPackage2 = new ProPackage();
			proPackage2.Type = ProPackage.PackageType.UDP;
			proPackage2.HostInfo = (HostInformation) vector3.get(i2);
			proPackage2.nCommandID = 2097440L;
			SendFileRunnableImp sendFileRunnableImp = new SendFileRunnableImp();
			sendFileRunnableImp.setParameter(str, vector4, (HostInformation) vector3.get(i2), iFileTransNotify, vector2, proPackage2, i);
			sendFileRunnableImp.start();
			i2++;
			proPackage = proPackage2;
		}
		return proPackage.nPackageID;
	}

	private final FileInformation makeFileInfo(Vector<String> vector, String str, long j, IFileTransNotify iFileTransNotify, HostInformation hostInformation, String str2) {
		if (str == null) {
			return null;
		}
		FileInformation fileInformation = new FileInformation();
		File file = new File(str);
		if (hostInformation.version.compareTo("5.0") >= 0) {
			fileInformation.Id = (int) Public_Tools.getFileID(j, Public_Tools.getFileID());
		} else {
			fileInformation.Id = Public_Tools.getFileID();
		}
		fileInformation.nPackageID = j;
		fileInformation.Path = file.getParentFile().getPath();
		fileInformation.FileName = file.getName();
		fileInformation.size = Public_Tools.getFileSize(file);
		fileInformation.time = System.currentTimeMillis();
		fileInformation.transNotify = iFileTransNotify;
		fileInformation.nFileAttr |= file.isDirectory() ? 2 : 1;
		fileInformation.vecFilterType = vector;
		fileInformation.mStrDiscussID = str2;
		return fileInformation;
	}

	/* JADX INFO: Access modifiers changed from: private */
	public Vector<FileInformation> makeFileInfo(Vector<String> vector, Vector<String> vector2, long j, IFileTransNotify iFileTransNotify, HostInformation hostInformation, String str) {
		if (vector2 == null) {
			return null;
		}
		Vector<FileInformation> vector3 = new Vector<>();
		for (int i = 0; i < vector2.size(); i++) {
			FileInformation fileInformationMakeFileInfo = makeFileInfo(vector, vector2.get(i), j, iFileTransNotify, hostInformation, str);
			if (fileInformationMakeFileInfo != null) {
				vector3.add(fileInformationMakeFileInfo);
			}
		}
		return vector3;
	}

	private String makeFileSection(FileInformation fileInformation, int i) {
		if (fileInformation == null) {
			return null;
		}
		String str = ((((("" + String.valueOf(fileInformation.Id)) + Public_MsgID.PRO_SPACE + fileInformation.FileName) + Public_MsgID.PRO_SPACE + Long.toHexString(fileInformation.size).toUpperCase()) + Public_MsgID.PRO_SPACE + Long.toHexString(fileInformation.time).toUpperCase()) + Public_MsgID.PRO_SPACE + (i | fileInformation.nFileAttr)) + ":0:0:0";
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		sb.append(Public_MsgID.PRO_SPACE);
		sb.append(fileInformation.mStrDiscussID != null ? fileInformation.mStrDiscussID : "");
		return sb.toString() + Public_MsgID.fileEnd;
	}

	/* JADX INFO: Access modifiers changed from: private */
	public String makeMutiFileSection(Vector<FileInformation> vector, int i) {
		if (vector == null) {
			return null;
		}
		String str = "";
		for (int i2 = 0; i2 < vector.size(); i2++) {
			str = str + makeFileSection(vector.get(i2), i);
		}
		return str;
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean recvFile(FileInformation fileInformation, HostInformation hostInformation, IFileTransNotify iFileTransNotify) throws IOException {
		if (fileInformation == null || hostInformation == null || iFileTransNotify == null) {
			return false;
		}
		fileInformation.transNotify = iFileTransNotify;
		if (fileInformation.fileTransMode == Public_Def.FileTransMode.FILETRANS_CON_CANCEL) {
			File file = new File(fileInformation.Path, FileInformation.toTmpFileName(fileInformation.FileName));
			if (file.exists()) {
				file.delete();
			}
			fileInformation.fileTransMode = Public_Def.FileTransMode.FILETRANS_ORDER;
			fileInformation.startPos = 0L;
			DBHelper.getInstance(this.m_Context).deleteHistoryFilesRecord("sMac = '" + hostInformation.strMacAddr + "' and FileName = '" + fileInformation.strOriginalFileName + "' and TransStatus = 3 and Size = " + fileInformation.size);
		}
		if (Public_Def.FileTransMode.FILETRANS_ORDER == fileInformation.fileTransMode) {
			if (!autoRenameFile(fileInformation)) {
				return false;
			}
			if ((fileInformation.nFileAttr & 4) == 0 && (fileInformation.nFileAttr & 2) == 0) {
				if (!DBHelper.getInstance(this.m_Context).insertHistoryFilesRecord("insert into HistoryFiles (sMac,DiscussID,TransStatus,Time,Type,Size,FileName,FileFullPath) values('" + hostInformation.strMacAddr + "','',3," + new Date().getTime() + ",''," + fileInformation.size + ",'" + fileInformation.strOriginalFileName + "','" + fileInformation.Path + File.separator + fileInformation.FileName + "')")) {
					return false;
				}
			}
		}
		ProPackage proPackage = new ProPackage();
		proPackage.Type = ProPackage.PackageType.TCP;
		proPackage.HostInfo = hostInformation;
		proPackage.nPackageID = Public_Tools.getCurrentTimeMillis();
		proPackage.nCommandID = (fileInformation.nFileAttr & 2) != 0 ? 98 : 96;
		proPackage.strAdditionalSection = Long.toHexString(fileInformation.nPackageID) + Public_MsgID.PRO_SPACE + Long.toHexString(fileInformation.Id) + Public_MsgID.PRO_SPACE + Long.toHexString(fileInformation.startPos) + Public_MsgID.PRO_SPACE;
		return this.m_MsgController.m_Transport.recvFile(proPackage, fileInformation);
	}

	private boolean autoRenameFile(FileInformation fileInformation) {
		String str;
		new FileInformation();
		try {
			FileInformation fileInformation2 = (FileInformation) fileInformation.clone();
			int i = 1;
			while (true) {
				File file = new File(fileInformation.Path, fileInformation.FileName);
				if (!file.exists()) {
					break;
				}
				int iLastIndexOf = file.isDirectory() ? -1 : fileInformation2.FileName.lastIndexOf(".");
				StringBuilder sb = new StringBuilder();
				sb.append("_鍓湰");
				int i2 = i + 1;
				sb.append(Integer.toString(i));
				String string = sb.toString();
				if (-1 == iLastIndexOf) {
					str = fileInformation2.FileName + string;
				} else {
					str = fileInformation2.FileName.substring(0, iLastIndexOf) + string + fileInformation2.FileName.substring(iLastIndexOf, fileInformation2.FileName.length());
				}
				fileInformation.FileName = str;
				i = i2;
			}
			if (!fileInformation.FileName.equals(fileInformation2.FileName)) {
				fileInformation.status = Public_Def.TransStatus.Trans_Rename;
				fileInformation.transNotify.reNamed(fileInformation2, fileInformation);
			}
			return true;
		} catch (CloneNotSupportedException unused) {
			return false;
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean cancelRecvFile(FileInformation fileInformation, HostInformation hostInformation) {
		String str;
		if (fileInformation == null || hostInformation == null) {
			return false;
		}
		if (hostInformation.version.compareTo("5.0") >= 0) {
			str = Long.toHexString(fileInformation.nPackageID) + Public_MsgID.CUTAPART + Long.toHexString(fileInformation.Id);
		} else {
			str = Long.toString(fileInformation.nPackageID) + Public_Tools.getFileIndex(fileInformation.Id) + Public_MsgID.PRO_SPACE;
		}
		ProPackage proPackageMakeProPackage = Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 230L, str);
		this.m_MsgController.m_Transport.cancelFileTrans(hostInformation.strMacAddr, fileInformation.nPackageID, fileInformation.Id);
		return this.m_MsgController.m_Transport.SendMessage(proPackageMakeProPackage);
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean cancelSendFile(FileInformation fileInformation, HostInformation hostInformation) {
		String str;
		if (fileInformation == null || hostInformation == null) {
			return false;
		}
		if (hostInformation.version.compareTo("5.0") >= 0) {
			str = Long.toHexString(fileInformation.nPackageID) + Public_MsgID.CUTAPART + Long.toHexString(fileInformation.Id);
		} else {
			str = Long.toString(fileInformation.nPackageID) + Public_Tools.getFileIndex(fileInformation.Id) + Public_MsgID.PRO_SPACE;
		}
		ProPackage proPackageMakeProPackage = Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, 232L, str);
		this.m_MsgController.m_Transport.cancelFileTrans(hostInformation.strMacAddr, fileInformation.nPackageID, fileInformation.Id);
		return this.m_MsgController.m_Transport.SendMessage(proPackageMakeProPackage);
	}

	private class DecodeCancelRecvFile implements ITransNotify {
		private DecodeCancelRecvFile() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			long j;
			long j2;
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.PRO_SPACE);
			long j3 = -1;
			try {
				if (proPackage.HostInfo.version.compareTo("5.0") >= 0) {
					String[] strArrSplit2 = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
					j2 = Long.parseLong(strArrSplit2[0], 16);
					j = Long.parseLong(strArrSplit2[1], 16);
				} else {
					j2 = Long.parseLong(strArrSplit[0].substring(0, strArrSplit[0].length() - 1), 10);
					j = Long.parseLong(strArrSplit[0].substring(strArrSplit[0].length() - 1), 16);
				}
				j3 = j2;
			} catch (NumberFormatException unused) {
				j = -1;
			}
			try {
				Thread.sleep(250L);
			} catch (Exception unused2) {
			}
			long j4 = j3;
			long j5 = j;
			Protocol.this.m_MsgController.m_Transport.cancelFileTrans(proPackage.HostInfo.strMacAddr, j4, j5);
			Protocol.this.m_UILopperFreighter.notifyCancelRecvFile(proPackage.HostInfo, j4, j5);
		}
	}

	private class DecodeCancelSendFile implements ITransNotify {
		private DecodeCancelSendFile() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			long j;
			long j2;
			long jLongValue;
			long j3;
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.PRO_SPACE);
			int length = strArrSplit[0].length();
			try {
				if (proPackage.HostInfo.version.compareTo("5.0") >= 0) {
					String[] strArrSplit2 = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
					long j4 = Long.parseLong(strArrSplit2[0], 16);
					jLongValue = Long.parseLong(strArrSplit2[1], 16);
					j3 = j4;
				} else {
					int i = length - 1;
					long jLongValue2 = Long.valueOf(strArrSplit[0].substring(0, i)).longValue();
					jLongValue = Long.valueOf(strArrSplit[0].substring(i, length)).longValue();
					j3 = jLongValue2;
				}
				j2 = jLongValue;
				j = j3;
			} catch (NumberFormatException unused) {
				j = -1;
				j2 = -1;
			}
			Protocol.this.m_UILopperFreighter.notifyCancelSendFile(proPackage.HostInfo, j, j2);
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public void update(IUpdateNotify iUpdateNotify) {
		if (iUpdateNotify == null) {
			return;
		}
		this.m_UpdateNotify = iUpdateNotify;
		new Thread(new Runnable() { // from class: com.netfeige.protocol.Protocol.1
			@Override // java.lang.Runnable
			public synchronized void run() {
				ByteBuffer byteBufferAllocate = ByteBuffer.allocate(Public_Tools.getVersion().length() + 7);
				byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
				try {
					byteBufferAllocate.put((byte) 30);
					byteBufferAllocate.putInt(1);
					byteBufferAllocate.put((byte) 50);
					byteBufferAllocate.put((byte) Public_Tools.getVersion().length());
					byteBufferAllocate.put(Public_Tools.getVersion().getBytes("UTF-8"));
					byteBufferAllocate.flip();
				} catch (UnsupportedEncodingException unused) {
				}
				Log.i("mylog", "Update信息发送" + byteBufferAllocate.limit());
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress("sa.um800.net", 4665));
					OutputStream outputStream = socket.getOutputStream();
					InputStream inputStream = socket.getInputStream();
					outputStream.write(byteBufferAllocate.array());
					outputStream.flush();
					byte[] bArr = new byte[2];
					inputStream.read(bArr, 0, 2);
					ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr, 0, 2);
					byteBufferWrap.order(ByteOrder.LITTLE_ENDIAN);
					int i = byteBufferWrap.getShort();
					byte[] bArr2 = new byte[i];
					inputStream.read(bArr2, 0, i);
					Protocol.this.parse(bArr2, i);
				} catch (IOException e) {
					e.printStackTrace();
					if (Protocol.this.m_UpdateNotify != null) {
						Protocol.this.m_UpdateNotify.onGetUpdateResult(0, "", "", "");
					}
				}
			}
		}).start();
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean parse(byte[] bArr, int i) {
		String str;
		String str2;
		String str3;
		if (bArr[0] != 30) {
			return false;
		}
		Log.i("mylog", "Update淇℃伅 鎺ユ敹");
		try {
			ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr, 0, i);
			byteBufferWrap.order(ByteOrder.LITTLE_ENDIAN);
			byte b = byteBufferWrap.get();
			int i2 = byteBufferWrap.getInt();
			byte b2 = byteBufferWrap.get();
			Log.i("protocol", "result:" + ((int) b2) + " type:" + ((int) b) + " umid:" + i2);
			String str4 = "";
			if (b2 != 0) {
				int i3 = byteBufferWrap.get();
				if (i3 > 0) {
					byte[] bArr2 = new byte[i3];
					byteBufferWrap.get(bArr2);
					str3 = new String(bArr2, "UTF-8");
				} else {
					str3 = "";
				}
				int i4 = byteBufferWrap.get() & 255;
				if (i4 > 0) {
					byte[] bArr3 = new byte[i4];
					byteBufferWrap.get(bArr3);
					str2 = new String(bArr3, "UTF-8");
				} else {
					str2 = "";
				}
				int i5 = byteBufferWrap.get();
				if (i5 > 0) {
					byte[] bArr4 = new byte[i5];
					byteBufferWrap.get(bArr4);
					str = new String(bArr4, "UTF-8");
				} else {
					str = "";
				}
				str4 = str3;
			} else {
				str = "";
				str2 = str;
			}
			if (this.m_UpdateNotify == null) {
				return true;
			}
			this.m_UpdateNotify.onGetUpdateResult(b2, str4, str2, str);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public synchronized void sendFeedback(String str) {
		if (str == null) {
			return;
		}
		this.m_strContext = str;
		new Thread(new Runnable() { // from class: com.netfeige.protocol.Protocol.2
			@Override // java.lang.Runnable
			public synchronized void run() {
				try {
					byte[] bArrEncryptString = SimpleCrypto.encryptString(Protocol.this.m_strContext);
					ByteBuffer byteBufferAllocate = ByteBuffer.allocate(bArrEncryptString.length + 17);
					byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
					byteBufferAllocate.put((byte) 0); // Protocol version or type marker
					byteBufferAllocate.putInt(1000005);
					byteBufferAllocate.putInt(8001);
					byteBufferAllocate.putInt(1);
					byteBufferAllocate.putInt(bArrEncryptString.length);
					byteBufferAllocate.put(bArrEncryptString);
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress("sa.um800.net", 4665));
					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(byteBufferAllocate.array());
					outputStream.flush();
					outputStream.close();
					socket.close();
				} catch (Exception unused) {
				}
			}
		}).start();
	}

	private class DecodePrintAnswer implements ITransNotify {
		private DecodePrintAnswer() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			try {
				Protocol.this.sendFile("", proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART)[1].toString(), proPackage.HostInfo, (IpmsgService) Protocol.this.m_Context, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				Protocol.this.m_UILopperFreighter.notifyPrintAnswer(proPackage.HostInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class DecodePrintRefused implements ITransNotify {
		private DecodePrintRefused() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			Protocol.this.m_UILopperFreighter.notifyPrintRefused(proPackage.HostInfo);
		}
	}

	private class DecodePrintTimeout implements ITransNotify {
		private DecodePrintTimeout() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			Protocol.this.m_UILopperFreighter.notifyPrintTimeout(proPackage.HostInfo);
		}
	}

	private class DecodePrintFinish implements ITransNotify {
		private DecodePrintFinish() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			if ((proPackage.HostInfo == null) || (proPackage == null)) {
				return;
			}
			Protocol.this.m_UILopperFreighter.notifyPrintFinish(proPackage.HostInfo);
		}
	}

	private class DecodeShareListQuery implements ITransNotify {
		private DecodeShareListQuery() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			try {
				if (0 != (proPackage.nCommandID & 256)) {
					Protocol.this.EncodeRecMsg(proPackage);
				}
				boolean z = true;
				boolean z2 = proPackage == null;
				if (proPackage.HostInfo != null) {
					z = false;
				}
				if (z || z2) {
					return;
				}
				String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
				if (strArrSplit.length > 0) {
					Protocol.this.onFileShareQuery(proPackage.HostInfo, Long.parseLong(strArrSplit[0]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class DecodeFileShareAnswer implements ITransNotify {
		private DecodeFileShareAnswer() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			HashMap<String, Object> map = new HashMap<>();
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
			IpmsgApplication.g_mapUserSharePwd.put(proPackage.HostInfo.strMacAddr, strArrSplit.length >= 2 ? strArrSplit[1] : "");
			map.put("queryId", strArrSplit[0]);
			map.put("pwd", strArrSplit.length >= 2 ? strArrSplit[1] : "");
			ArrayList arrayList = new ArrayList();
			if (strArrSplit.length >= 3) {
				for (String str : strArrSplit[2].split(Public_MsgID.PRO_SPACE_GROUP)) {
					String[] strArrSplit2 = str.split(Public_MsgID.PRO_SPACE);
					LanSharedItem lanSharedItem = new LanSharedItem();
					lanSharedItem.setM_bEncrypt(strArrSplit[1].length() != 0);
					lanSharedItem.setM_iID(Integer.parseInt(strArrSplit2[0]));
					lanSharedItem.setM_strName(strArrSplit2[1]);
					lanSharedItem.setM_iType(Integer.parseInt(strArrSplit2[2]));
					lanSharedItem.setM_lSize(Long.parseLong(strArrSplit2[3]));
					lanSharedItem.setM_lTime(Long.parseLong(strArrSplit2[4]));
					lanSharedItem.setM_strFrom(proPackage.HostInfo.pszUserName);
					lanSharedItem.setM_strFromMac(proPackage.HostInfo.strMacAddr);
					arrayList.add(lanSharedItem);
				}
			}
			map.put("lanShareList", arrayList);
			Protocol.this.m_UILopperFreighter.notifyFileShareListAns(map);
		}
	}

	private class DecodeSubShareListQuery implements ITransNotify {
		private DecodeSubShareListQuery() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			HashMap map = new HashMap();
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
			map.put("queryId", strArrSplit[0]);
			map.put("fileId", strArrSplit.length >= 2 ? strArrSplit[1] : "");
			map.put("dirPath", strArrSplit.length >= 3 ? strArrSplit[2] : "");
			Protocol.this.OnSubFileShareQuery(proPackage.HostInfo, map);
		}
	}

	private class DecodeSubFileShareAnswer implements ITransNotify {
		private DecodeSubFileShareAnswer() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			HashMap<String, Object> map = new HashMap<>();
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
			map.put("queryId", strArrSplit[0]);
			map.put("fileId", strArrSplit.length >= 2 ? strArrSplit[1] : "");
			map.put("shareTime", strArrSplit.length >= 3 ? strArrSplit[2] : "");
			ArrayList arrayList = new ArrayList();
			if (strArrSplit.length >= 4) {
				for (String str : strArrSplit[3].split(Public_MsgID.PRO_SPACE_GROUP)) {
					String[] strArrSplit2 = str.split(Public_MsgID.PRO_SPACE);
					LanSharedItem lanSharedItem = new LanSharedItem();
					lanSharedItem.setM_bEncrypt(false);
					lanSharedItem.setM_iID(Integer.parseInt((String) map.get("fileId")));
					lanSharedItem.setM_strName(strArrSplit2[0]);
					lanSharedItem.setM_iType(Integer.parseInt(strArrSplit2[1]));
					lanSharedItem.setM_lSize(Long.parseLong(strArrSplit2[2]));
					lanSharedItem.setM_lTime(Long.parseLong((String) map.get("shareTime")));
					lanSharedItem.setM_strFrom(proPackage.HostInfo.pszUserName);
					lanSharedItem.setM_strFromMac(proPackage.HostInfo.strMacAddr);
					arrayList.add(lanSharedItem);
				}
			}
			map.put("lanShareList", arrayList);
			Protocol.this.m_UILopperFreighter.notifySubFileShareListAns(map);
		}
	}

	private class DecodeRootFileDLQuery implements ITransNotify {
		private DecodeRootFileDLQuery() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			HashMap map = new HashMap();
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
			map.put("dlcmd", strArrSplit[0]);
			ArrayList arrayList = new ArrayList();
			if (strArrSplit.length >= 2) {
				for (String str : strArrSplit[1].split(Public_MsgID.PRO_SPACE)) {
					arrayList.add(str);
				}
			}
			map.put("shareFileIdList", arrayList);
			Protocol.this.onRootFileDLQuery(proPackage.HostInfo, map);
		}
	}

	private class DecodeSubFileDLQuery implements ITransNotify {
		private DecodeSubFileDLQuery() {
		}

		@Override // com.netfeige.transport.ITransNotify
		public void Recv(ProPackage proPackage) {
			if (0 != (proPackage.nCommandID & 256)) {
				Protocol.this.EncodeRecMsg(proPackage);
			}
			if ((proPackage == null) || (proPackage.HostInfo == null)) {
				return;
			}
			HashMap map = new HashMap();
			String[] strArrSplit = proPackage.strAdditionalSection.split(Public_MsgID.CUTAPART);
			map.put("fileId", strArrSplit[0]);
			map.put("dirPath", strArrSplit.length >= 2 ? strArrSplit[1] : "");
			map.put("dlcmd", strArrSplit.length >= 3 ? strArrSplit[2] : "");
			ArrayList arrayList = new ArrayList();
			if (strArrSplit.length >= 4) {
				for (String str : strArrSplit[3].split(Public_MsgID.PRO_SPACE_GROUP)) {
					arrayList.add(str);
				}
			}
			map.put("shareFileSubDirList", arrayList);
			Protocol.this.onSubFileDLQuery(proPackage.HostInfo, map);
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public void printQuery(String str, HostInformation hostInformation) {
		if ((m_Protocol == null) || (this.m_DataConfig == null)) {
			return;
		}
		HostInformation localHostInfo = Public_Tools.getLocalHostInfo();
		if (hostInformation == null) {
			return;
		}
		this.m_proPackage = Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2143288973L, localHostInfo.pszUserName + Public_MsgID.CUTAPART + new File(str).getName() + Public_MsgID.CUTAPART + str + Public_MsgID.CUTAPART + FragmentTransaction.TRANSIT_FRAGMENT_OPEN + Public_MsgID.CUTAPART);
		this.m_MsgController.m_Transport.SendMessage(this.m_proPackage);
	}

	private boolean getScreenSize() {
		WindowManager windowManager = (WindowManager) this.m_Context.getSystemService("window");
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		double dSqrt = Math.sqrt(Math.pow(displayMetrics.widthPixels, 2.0d) + Math.pow(displayMetrics.heightPixels, 2.0d));
		double d = displayMetrics.density * 160.0f;
		Double.isNaN(d);
		return dSqrt / d > 5.8d;
	}

	private class SendFileRunnableImp implements Runnable {
		private String mStrDiscussID;
		private HostInformation m_destHostInfo;
		private int m_iFileExtaAttr;
		private ProPackage m_proPackage;
		private Thread m_thread;
		private IFileTransNotify m_transNotify;
		private Vector<String> m_vecFileType;
		private Vector<String> m_vecMutiFileInfo;

		private SendFileRunnableImp() {
			this.m_thread = null;
			this.m_proPackage = null;
			this.mStrDiscussID = "";
			this.m_vecMutiFileInfo = null;
			this.m_destHostInfo = null;
			this.m_transNotify = null;
			this.m_vecFileType = null;
			this.m_iFileExtaAttr = 0;
		}

		public void setParameter(String str, Vector<String> vector, HostInformation hostInformation, IFileTransNotify iFileTransNotify, Vector<String> vector2, ProPackage proPackage, int i) {
			this.mStrDiscussID = str;
			this.m_vecMutiFileInfo = vector;
			this.m_destHostInfo = hostInformation;
			this.m_transNotify = iFileTransNotify;
			this.m_vecFileType = vector2;
			this.m_proPackage = proPackage;
			this.m_iFileExtaAttr = i;
		}

		public void start() {
			Thread thread = new Thread(this);
			this.m_thread = thread;
			thread.start();
		}

		@Override // java.lang.Runnable
		public void run() {
			try {
				Vector<FileInformation> vectorMakeFileInfo = Protocol.this.makeFileInfo(this.m_vecFileType, this.m_vecMutiFileInfo, this.m_proPackage.nPackageID, this.m_transNotify, this.m_destHostInfo, this.mStrDiscussID);
				this.m_proPackage.strAdditionalSection = Public_MsgID.CUTAPART + Protocol.this.makeMutiFileSection(vectorMakeFileInfo, this.m_iFileExtaAttr);
				Protocol.this.m_MsgController.m_Transport.sendFile(this.m_proPackage, vectorMakeFileInfo);
				if ((this.mStrDiscussID == null || !this.mStrDiscussID.startsWith(Protocol_Discuss.smPreID)) && vectorMakeFileInfo != null && this.m_iFileExtaAttr == 0) {
					Protocol.this.m_UILopperFreighter.notifySendFile(this.m_destHostInfo, vectorMakeFileInfo, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class ScanTimer implements Runnable {
		private Thread m_thread;

		private ScanTimer() {
			this.m_thread = null;
		}

		public void start() {
			Thread thread = new Thread(this);
			this.m_thread = thread;
			thread.start();
		}

		@Override // java.lang.Runnable
		public void run() {
			while (true) {
				try {
					Thread.sleep(60000L);
					ArrayList<HostInformation> arrayList = new ArrayList<>(((IpmsgService) Protocol.this.m_Context).userList);
					if (!Protocol.this.isBroadcasting()) {
						Protocol.this.entryService(arrayList, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class DiscussReExeThread implements Runnable {
		private Thread m_thread;

		private DiscussReExeThread() {
			this.m_thread = null;
		}

		public void start() {
			Thread thread = new Thread(this);
			this.m_thread = thread;
			thread.start();
		}

		@Override // java.lang.Runnable
		public void run() {
			ProPackage proPackageMakeProPackage;
			while (true) {
				try {
					Thread.sleep(20000L);
					ArrayList<DiscussExeStatus> discussExeStatusRecord = DBHelper.getInstance(Protocol.this.m_Context).getDiscussExeStatusRecord("1 = 1");
					if (discussExeStatusRecord != null) {
						String strId = "";
						String strDestMac = "";
						String strRecvMac = strDestMac;
						for (int i = 0; i < discussExeStatusRecord.size(); i++) {
							if (!strId.equals(discussExeStatusRecord.get(i).getStrId()) || !strDestMac.equals(discussExeStatusRecord.get(i).getStrDestMac()) || !strRecvMac.equals(discussExeStatusRecord.get(i).getStrRecvMac())) {
								strId = discussExeStatusRecord.get(i).getStrId();
								strDestMac = discussExeStatusRecord.get(i).getStrDestMac();
								strRecvMac = discussExeStatusRecord.get(i).getStrRecvMac();
								HostInformation hostInfo = ((IpmsgService) Protocol.this.m_Context).getHostInfo(strRecvMac);
								if (hostInfo != null) {
									if (discussExeStatusRecord.get(i).isBIsJoin()) {
										proPackageMakeProPackage = Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 244L, strId + Public_MsgID.CUTAPART + strDestMac);
									} else {
										proPackageMakeProPackage = Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInfo, 245L, strId + Public_MsgID.CUTAPART + strDestMac);
									}
									Protocol.this.m_MsgController.m_Transport.SendMessage(proPackageMakeProPackage);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class BroadcastRunnableImp implements Runnable {
		private boolean m_bIsRunning;
		private ProPackage m_broadcastProPackage;
		private Thread m_thread;

		private BroadcastRunnableImp() {
			this.m_thread = null;
			this.m_broadcastProPackage = null;
			this.m_bIsRunning = false;
		}

		public ProPackage getM_broadcastProPackage() {
			return this.m_broadcastProPackage;
		}

		public void setM_broadcastProPackage(ProPackage proPackage) {
			this.m_broadcastProPackage = proPackage;
		}

		public boolean isM_bIsRunning() {
			return this.m_bIsRunning;
		}

		public void setM_bIsRunning(boolean z) {
			this.m_bIsRunning = z;
		}

		public void start() {
			Thread thread = new Thread(this);
			this.m_thread = thread;
			thread.start();
			setM_bIsRunning(true);
		}

		public void interrupt() {
			Thread thread = this.m_thread;
			if (thread != null) {
				thread.interrupt();
			}
		}

		/* JADX WARN: Multi-variable type inference failed */
		/* JADX WARN: Type inference failed for: r1v0, types: [boolean] */
		/* JADX WARN: Type inference failed for: r1v5, types: [com.netfeige.protocol.UILopperFreighter] */
		/* JADX WARN: Type inference failed for: r1v6 */
		@Override // java.lang.Runnable
		public void run() {
			boolean z = false;
			try {
				try {
					if (this.m_broadcastProPackage != null) {
						Protocol.this.m_MsgController.m_Transport.SendMessage(this.m_broadcastProPackage);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.m_broadcastProPackage = null;
				this.m_thread = null;
				setM_bIsRunning(z);
				Protocol.this.m_UILopperFreighter.notifyEntryBroadcastFinish(true);
			}
		}
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean isBroadcasting() {
		return this.m_broadcastRunnableImp.isM_bIsRunning();
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean onFileShareQuery(HostInformation hostInformation, long j) {
		ArrayList<ShareFiles> shareFilesRecord = DBHelper.getInstance(this.m_Context).getShareFilesRecord("MAClist = '' or MAClist like '%" + hostInformation.strMacAddr + "%'", null);
		ArrayList<SharePassword> sharePasswordRecord = DBHelper.getInstance(this.m_Context).getSharePasswordRecord("sMac = '" + Public_Tools.getLocalMacAddress() + "'");
		HashMap<String, Object> map = new HashMap<>();
		map.put("queryId", Long.valueOf(j));
		map.put("pwd", sharePasswordRecord.isEmpty() ? "" : sharePasswordRecord.get(0).getM_strPassword());
		map.put("myShareList", shareFilesRecord);
		return toAnswer(hostInformation, map);
	}

	private boolean toAnswer(HostInformation hostInformation, HashMap<String, Object> map) {
		String str = ((Long) map.get("queryId")) + Public_MsgID.CUTAPART + ((String) map.get("pwd")) + Public_MsgID.CUTAPART;
		ArrayList arrayList = (ArrayList) map.get("myShareList");
		for (int i = 0; i < arrayList.size(); i++) {
			String str2 = str + ((ShareFiles) arrayList.get(i)).getM_iID() + Public_MsgID.PRO_SPACE + ((ShareFiles) arrayList.get(i)).getM_strName() + Public_MsgID.PRO_SPACE + ((ShareFiles) arrayList.get(i)).getM_iType() + Public_MsgID.PRO_SPACE + ((ShareFiles) arrayList.get(i)).getM_lSize() + Public_MsgID.PRO_SPACE + ((ShareFiles) arrayList.get(i)).getM_lTime();
			str = i < arrayList.size() - 1 ? str2 + Public_MsgID.PRO_SPACE_GROUP : str2 + Public_MsgID.CUTAPART;
		}
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2147483503L, str));
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean OnSubFileShareQuery(HostInformation hostInformation, HashMap<String, Object> map) {
		ArrayList<ShareFiles> shareFilesRecord = DBHelper.getInstance(this.m_Context).getShareFilesRecord("ID = ?", new String[]{(String) map.get("fileId")});
		if (shareFilesRecord.size() <= 0) {
			return false;
		}
		HashMap<String, Object> map2 = new HashMap<>();
		map2.put("queryId", (String) map.get("queryId"));
		map2.put("fileId", (String) map.get("fileId"));
		map2.put("shareTime", Long.valueOf(shareFilesRecord.get(0).getM_lTime()));
		ArrayList arrayList = new ArrayList();
		String strReplace = (shareFilesRecord.get(0).getM_strPath() + map.get("dirPath")).replace("\\", File.separator);
		if (strReplace.endsWith("*.*")) {
			strReplace = strReplace.substring(0, strReplace.lastIndexOf("*.*"));
		}
		File[] fileArrListFiles = new File(strReplace).listFiles();
		if (fileArrListFiles == null || fileArrListFiles.length <= 0) {
			return false;
		}
		for (int i = 0; i < fileArrListFiles.length; i++) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setPath(fileArrListFiles[i].getAbsolutePath());
			fileInfo.setName(fileArrListFiles[i].getName());
			fileInfo.setFloder(fileArrListFiles[i].isDirectory());
			fileInfo.setLastTime(fileArrListFiles[i].lastModified());
			fileInfo.setSize(Public_Tools.getFileSize(fileArrListFiles[i]));
			arrayList.add(fileInfo);
		}
		map2.put("fileInfoList", arrayList);
		return toAnswerSubList(hostInformation, map2);
	}

	private boolean toAnswerSubList(HostInformation hostInformation, HashMap<String, Object> map) {
		String str = map.get("queryId") + Public_MsgID.CUTAPART + ((String) map.get("fileId")) + Public_MsgID.CUTAPART + ((Long) map.get("shareTime")) + Public_MsgID.CUTAPART;
		ArrayList arrayList = (ArrayList) map.get("fileInfoList");
		for (int i = 0; i < arrayList.size(); i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(str);
			sb.append(((FileInfo) arrayList.get(i)).getName());
			sb.append(Public_MsgID.PRO_SPACE);
			sb.append(((FileInfo) arrayList.get(i)).isFloder() ? 1 : 6);
			sb.append(Public_MsgID.PRO_SPACE);
			sb.append(((FileInfo) arrayList.get(i)).getSize());
			sb.append(Public_MsgID.PRO_SPACE);
			String string = sb.toString();
			str = i < arrayList.size() - 1 ? string + Public_MsgID.PRO_SPACE_GROUP : string + Public_MsgID.CUTAPART;
		}
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2147483500L, str));
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean onRootFileDLQuery(HostInformation hostInformation, HashMap<String, Object> map) {
		Vector<String> vector = new Vector<>();
		ArrayList arrayList = (ArrayList) map.get("shareFileIdList");
		for (int i = 0; i < arrayList.size(); i++) {
			ArrayList<ShareFiles> shareFilesRecord = DBHelper.getInstance(this.m_Context).getShareFilesRecord("ID = ?", new String[]{(String) arrayList.get(i)});
			if (shareFilesRecord.size() > 0) {
				vector.add(shareFilesRecord.get(0).getM_strPath());
			}
		}
		sendFile(null, vector, hostInformation, (IpmsgService) this.m_Context, null, Public_Def.DownloadCmd.valueOf(Integer.parseInt((String) map.get("dlcmd"))) == Public_Def.DownloadCmd.DOWNLOAD_OPEN ? 32 : 16);
		return true;
	}

	/* JADX INFO: Access modifiers changed from: private */
	public boolean onSubFileDLQuery(HostInformation hostInformation, HashMap<String, Object> map) {
		ArrayList<ShareFiles> shareFilesRecord = DBHelper.getInstance(this.m_Context).getShareFilesRecord("ID = ?", new String[]{(String) map.get("fileId")});
		if (shareFilesRecord.size() <= 0) {
			return false;
		}
		String strReplace = (shareFilesRecord.get(0).getM_strPath() + ((String) map.get("dirPath"))).replace("\\", File.separator);
		if (strReplace.endsWith("*.*")) {
			strReplace = strReplace.substring(0, strReplace.lastIndexOf("*.*"));
		}
		ArrayList arrayList = (ArrayList) map.get("shareFileSubDirList");
		Vector<String> vector = new Vector<>();
		for (int i = 0; i < arrayList.size(); i++) {
			vector.add(strReplace + ((String) arrayList.get(i)));
		}
		sendFile(null, vector, hostInformation, (IpmsgService) this.m_Context, null, Public_Def.DownloadCmd.valueOf(Integer.parseInt((String) map.get("dlcmd"))) == Public_Def.DownloadCmd.DOWNLOAD_OPEN ? 32 : 16);
		return true;
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean sendFileListQuery(long j) {
		boolean z = true;
		for (int i = 0; i < ((IpmsgService) this.m_Context).userList.size(); i++) {
			if (!((IpmsgService) this.m_Context).userList.get(i).strMacAddr.equals(Public_Tools.getLocalMacAddress()) && !sendFileListQuery(((IpmsgService) this.m_Context).userList.get(i), j)) {
				z = false;
			}
		}
		return z;
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean sendFileListQuery(HostInformation hostInformation, long j) {
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2147483504L, j + Public_MsgID.CUTAPART));
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean sendSubFileListQuery(HostInformation hostInformation, long j, int i, String str) {
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2147483501L, j + Public_MsgID.CUTAPART + i + Public_MsgID.CUTAPART + str.replace(File.separator, "\\") + Public_MsgID.CUTAPART));
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean sendRootFileDLQuery(HostInformation hostInformation, Public_Def.DownloadCmd downloadCmd, ArrayList<LanSharedItem> arrayList) {
		String str = downloadCmd.value() + Public_MsgID.CUTAPART;
		for (int i = 0; i < arrayList.size(); i++) {
			String str2 = str + arrayList.get(i).getM_iID();
			str = i < arrayList.size() - 1 ? str2 + Public_MsgID.PRO_SPACE : str2 + Public_MsgID.CUTAPART;
		}
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2147483502L, str));
	}

	@Override // com.netfeige.protocol.IProtocol
	public boolean sendSubFileDLQuery(HostInformation hostInformation, Public_Def.DownloadCmd downloadCmd, ArrayList<LanSharedItem> arrayList, String str) {
		if (arrayList.isEmpty()) {
			return false;
		}
		String str2 = arrayList.get(0).getM_iID() + Public_MsgID.CUTAPART + str.replace(File.separator, "\\") + Public_MsgID.CUTAPART + downloadCmd.value() + Public_MsgID.CUTAPART;
		for (int i = 0; i < arrayList.size(); i++) {
			String str3 = str2 + arrayList.get(i).getM_strName();
			str2 = i < arrayList.size() - 1 ? str3 + Public_MsgID.PRO_SPACE_GROUP : str3 + Public_MsgID.CUTAPART;
		}
		return this.m_MsgController.m_Transport.SendMessage(Public_Tools.MakeProPackage(ProPackage.PackageType.UDP, hostInformation, -2147483499L, str2));
	}
}

