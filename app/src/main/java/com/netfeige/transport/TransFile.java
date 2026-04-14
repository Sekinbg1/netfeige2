package com.netfeige.transport;

import android.content.Context;
import android.util.Log;
import com.netfeige.common.DBHelper;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HistoryFiles;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Def.SDCardNoAvailaleSizeException;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.protocol.ProPackage;
import com.netfeige.service.IpmsgService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.NumberFormat;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class TransFile implements Runnable {
	private Context m_context;
	private FileTransManager m_fileTranManager;
	private ServerSocket m_tcpServerSocket = null;
	private Thread m_threadTcpServerSocket = null;
	private ITransNotify m_transport_Recv = null;

	public TransFile(FileTransManager fileTransManager) {
		this.m_fileTranManager = null;
		this.m_fileTranManager = fileTransManager;
	}

	public void start(Context context, ITransNotify iTransNotify) throws IOException {
		this.m_transport_Recv = iTransNotify;
		this.m_context = context;
		this.m_tcpServerSocket = new ServerSocket(2425);
		Thread thread = new Thread(this);
		this.m_threadTcpServerSocket = thread;
		thread.start();
	}

	@Override // java.lang.Runnable
	public void run() {
		TransPackage transPackageMakeDataPack;
		ProPackage proPackageDataPackToProPackage;
		while (true) {
			try {
				Socket socketAccept = this.m_tcpServerSocket.accept();
				byte[] bArr = new byte[2048];
				int i = socketAccept.getInputStream().read(bArr);
				if (-1 != i) {
					String str = new String(bArr, 0, i, "GBK");
					if (this.m_transport_Recv != null && (transPackageMakeDataPack = TransPackage.makeDataPack(str, socketAccept.getInetAddress(), socketAccept.getPort())) != null && (proPackageDataPackToProPackage = transPackageMakeDataPack.DataPackToProPackage(ProPackage.PackageType.TCP)) != null) {
						Log.v("mylog", "recv tcp: " + proPackageDataPackToProPackage.toString());
						if ((proPackageDataPackToProPackage.nCommandID & 96) == 96 || (proPackageDataPackToProPackage.nCommandID & 98) == 98) {
							String[] strArrSplit = proPackageDataPackToProPackage.strAdditionalSection.split(Public_MsgID.PRO_SPACE);
							try {
								this.m_fileTranManager.sendFile(socketAccept, Long.parseLong(strArrSplit[0], 16), Long.parseLong(strArrSplit[1], 16), strArrSplit.length >= 3 ? Long.parseLong(strArrSplit[2], 16) : 0L, proPackageDataPackToProPackage.HostInfo);
							} catch (NumberFormatException unused) {
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class ReserveConnectThread implements Runnable {
		protected Thread m_Thread;
		protected long m_lFileId;
		protected long m_lPackageID;
		protected int m_nPort;
		protected InetAddress m_netAddr;
		private final int m_reserveLinkMaxTimeOut = 1000;
		protected boolean m_bConnected = false;

		ReserveConnectThread(long j, long j2, InetAddress inetAddress, int i) {
			this.m_Thread = null;
			this.m_lFileId = j;
			this.m_lPackageID = j2;
			this.m_netAddr = inetAddress;
			this.m_nPort = i;
			this.m_Thread = new Thread(this);
		}

		@Override // java.lang.Runnable
		public void run() {
			TransPackage transPackageMakeDataPack;
			ProPackage proPackageDataPackToProPackage;
			TransFile.this.m_fileTranManager.addReserveLinkThread(this);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(this.m_netAddr, this.m_nPort);
			Socket socket = new Socket();
			int i = 0;
			while (i <= 6) {
				int i2 = i + 1;
				try {
					try {
						socket.connect(inetSocketAddress, 1000);
						byte[] bArr = new byte[2048];
						int i3 = socket.getInputStream().read(bArr);
						if (-1 != i3 && (transPackageMakeDataPack = TransPackage.makeDataPack(new String(bArr, 0, i3, "GBK"), socket.getInetAddress(), socket.getPort())) != null && (proPackageDataPackToProPackage = transPackageMakeDataPack.DataPackToProPackage(ProPackage.PackageType.TCP)) != null) {
							Log.v("mylog", "reserve link recv tcp: " + proPackageDataPackToProPackage.toString());
							if ((proPackageDataPackToProPackage.nCommandID & 96) == 96 || (proPackageDataPackToProPackage.nCommandID & 98) == 98) {
								String[] strArrSplit = proPackageDataPackToProPackage.strAdditionalSection.split(Public_MsgID.PRO_SPACE);
								try {
									TransFile.this.m_fileTranManager.sendFile(socket, Long.parseLong(strArrSplit[0], 16), Long.parseLong(strArrSplit[1], 16), strArrSplit.length >= 3 ? Long.parseLong(strArrSplit[2], 16) : 0L, proPackageDataPackToProPackage.HostInfo);
									this.m_bConnected = true;
									break;
								} catch (NumberFormatException unused) {
									continue;
								}
							}
						}
					} catch (IOException unused2) {
						Thread.sleep(500L);
					}
					i = i2;
				} catch (Exception unused3) {
				}
			}
			if (this.m_bConnected) {
				return;
			}
			FileTransManager.getInstance().removeReserveLinkThread(this);
		}
	}

	public class FileRecvThread extends IFileTransThread {
		private File m_file;
		private int m_nPort;
		private ProPackage m_proPackage;
		private final int m_recvMaxTimeOut;
		private SocketAddress m_remoteSocket;
		private InputStream m_socketInFileStream;
		private OutputStream m_socketOutStream;
		private String m_strCurrDirePath;
		private ServerSocket m_tcpReverseServerSocket;

		FileRecvThread(FileInformation fileInformation, ProPackage proPackage) throws IOException {
			super(null, fileInformation, proPackage.HostInfo);
			this.m_recvMaxTimeOut = 8000;
			this.m_file = null;
			this.m_socketInFileStream = null;
			this.m_socketOutStream = null;
			this.m_strCurrDirePath = "";
			this.m_remoteSocket = null;
			this.m_proPackage = null;
			this.m_tcpReverseServerSocket = null;
			this.m_proPackage = proPackage;
			this.m_bIsRecv = true;
		}

		private void initConnect() throws Exception {
			if (isReverseLink()) {
				createReverseTcpSocket();
			} else {
				this.m_sock = new Socket();
				this.m_remoteSocket = new InetSocketAddress(this.m_proPackage.HostInfo.IpAddr.netAddr, 2425);
				this.m_sock.connect(this.m_remoteSocket, 8000);
			}
			this.m_socketOutStream = this.m_sock.getOutputStream();
			this.m_socketOutStream.write(new TransPackage(this.m_proPackage).toString().getBytes("GBK"));
			this.m_socketOutStream.flush();
			this.m_socketInFileStream = this.m_sock.getInputStream();
		}

		private boolean isReverseLink() {
			return (this.m_proPackage.HostInfo.IpAddr.realNetAddr == null || this.m_proPackage.HostInfo.IpAddr.netAddr.getHostAddress().equals(this.m_proPackage.HostInfo.IpAddr.realNetAddr.getHostAddress())) ? false : true;
		}

		private void recvFile(FileInformation fileInformation) throws Exception {
			FileOutputStream fileOutputStream;
			long j = fileInformation.size;
			long j2 = 262144;
			int i = (int) (262144 > j ? fileInformation.size : 262144L);
			String tmpFileName = fileInformation.FileName;
			if (fileInformation.size > Public_Tools.getAvailableSize()) {
				Public_Def public_Def = Public_Def.getInstance();
				public_Def.getClass();
				throw public_Def.new SDCardNoAvailaleSizeException();
			}
			if ((this.m_nMainFileInfo.nFileAttr & 1) != 0) {
				tmpFileName = FileInformation.toTmpFileName(fileInformation.FileName);
			}
			File file = new File(fileInformation.Path, tmpFileName);
			this.m_file = file;
			long j3 = 0;
			file.setLastModified(0 == fileInformation.time ? System.currentTimeMillis() : fileInformation.time);
			if ((this.m_nMainFileInfo.nFileAttr & 1) != 0 && this.m_nMainFileInfo.startPos > 0) {
				j3 = this.m_nMainFileInfo.startPos;
				this.m_nFinishTotle = this.m_nMainFileInfo.startPos;
				if (PlaybackStateCompat.ACTION_SET_REPEAT_MODE > fileInformation.size - this.m_nMainFileInfo.startPos) {
					j2 = fileInformation.size - this.m_nMainFileInfo.startPos;
				}
				i = (int) j2;
				fileOutputStream = new FileOutputStream(this.m_file, true);
			} else {
				fileOutputStream = new FileOutputStream(this.m_file);
			}
			while (j3 < fileInformation.size && !this.m_bStopTrans) {
				try {
					InputStream inputStream = this.m_socketInFileStream;
					byte[] bArr = this.m_FileBuffer;
					long j4 = i;
					if (fileInformation.size - j3 < j4) {
						j4 = fileInformation.size - j3;
					}
					int i2 = inputStream.read(bArr, 0, (int) j4);
					fileOutputStream.write(this.m_FileBuffer, 0, i2);
					long j5 = i2;
					j3 += j5;
					this.m_nFinishTotle += j5;
					fileOutputStream.flush();
					if (checkFileTransTimeStamp()) {
						notifyFile(fileInformation, j3, Public_Def.TransStatus.Trans_Recving);
					}
				} catch (Exception e) {
					fileOutputStream.close();
					throw e;
				}
			}
			fileOutputStream.close();
		}

		private void recvDir(FileInformation fileInformation) throws Exception {
			this.m_strCurrDirePath = fileInformation.Path;
			byte[] bArr = new byte[4];
			byte[] bArr2 = new byte[1024];
			FileInformation fileInformation2 = new FileInformation();
			int i = 0;
			boolean z = true;
			while (!this.m_bStopTrans && -1 != this.m_socketInFileStream.read(bArr, 0, 4)) {
				Log.v("mylog", "recv dir cmd len : " + new String(bArr, 0, 4, "GBK"));
				try {
					i = Integer.parseInt(new String(bArr, 0, 4, "GBK"), 16);
				} catch (Exception unused) {
					Log.v("mylog", "recv dir cmd len : " + new String(bArr, 0, 4, "GBK"));
				}
				int i2 = i - 4;
				this.m_socketInFileStream.read(bArr2, 0, i2);
				String str = new String(bArr2, 0, i2, "GBK");
				Log.v("mylog", "recv dir cmd : " + str);
				String[] strArrSplit = str.split(Public_MsgID.PRO_SPACE);
				if (z) {
					this.m_nMainFileInfo.size = Long.parseLong(strArrSplit[2], 16);
					fileInformation2.FileName = this.m_nMainFileInfo.FileName;
					z = false;
				} else {
					fileInformation2.size = Long.parseLong(strArrSplit[2], 16);
					fileInformation2.FileName = strArrSplit[1];
				}
				int i3 = Integer.parseInt(strArrSplit[3], 16);
				fileInformation2.time = Long.parseLong(strArrSplit[4].split("=")[1], 16);
				processCmdID(i3, fileInformation2);
				if (this.m_nMainFileInfo.Path.equals(this.m_strCurrDirePath)) {
					Log.v("mylog", "recv dir is over ! ");
					return;
				}
				Thread.sleep(2L);
			}
		}

		private void handleSameFileName() {
			String str;
			FileInformation fileInformation = new FileInformation();
			int i = 1;
			try {
				fileInformation = (FileInformation) this.m_nMainFileInfo.clone();
			} catch (CloneNotSupportedException unused) {
				this.m_bStopTrans = true;
				notifyFile(Public_Def.TransStatus.Trans_Error);
			}
			while (true) {
				File file = new File(this.m_nMainFileInfo.Path, this.m_nMainFileInfo.FileName);
				if (!file.exists()) {
					break;
				}
				int iLastIndexOf = file.isDirectory() ? -1 : fileInformation.FileName.lastIndexOf(".");
				StringBuilder sb = new StringBuilder();
				sb.append("_副本");
				int i2 = i + 1;
				sb.append(Integer.toString(i));
				String string = sb.toString();
				FileInformation fileInformation2 = this.m_nMainFileInfo;
				if (-1 == iLastIndexOf) {
					str = fileInformation.FileName + string;
				} else {
					str = fileInformation.FileName.substring(0, iLastIndexOf) + string + fileInformation.FileName.substring(iLastIndexOf, fileInformation.FileName.length());
				}
				fileInformation2.FileName = str;
				i = i2;
			}
			if (this.m_nMainFileInfo.FileName.equals(fileInformation.FileName)) {
				return;
			}
			this.m_nMainFileInfo.status = Public_Def.TransStatus.Trans_Rename;
			TransFile.this.m_fileTranManager.post(new IFileTransThread.NotifyFileProgress(this, fileInformation));
		}

		private void processCmdID(int i, FileInformation fileInformation) throws Exception {
			if (i == 1) {
				fileInformation.Path = this.m_strCurrDirePath;
				recvFile(fileInformation);
				return;
			}
			if (i != 2) {
				if (i != 3) {
					return;
				}
				int iLastIndexOf = this.m_strCurrDirePath.lastIndexOf(ServiceReference.DELIMITER);
				this.m_strCurrDirePath = -1 == iLastIndexOf ? "" : this.m_strCurrDirePath.substring(0, iLastIndexOf);
				return;
			}
			this.m_strCurrDirePath += ServiceReference.DELIMITER + fileInformation.FileName;
			File file = new File(this.m_strCurrDirePath);
			if (file.exists()) {
				return;
			}
			file.setLastModified(fileInformation.time);
			file.mkdirs();
		}

		@Override // com.netfeige.transport.TransFile.IFileTransThread
		public void Impl() throws Exception {
			initConnect();
			if ((this.m_nMainFileInfo.nFileAttr & 2) != 0) {
				recvDir(this.m_nMainFileInfo);
			} else {
				recvFile(this.m_nMainFileInfo);
				if (!this.m_bStopTrans) {
					File file = new File(this.m_nMainFileInfo.Path, FileInformation.toTmpFileName(this.m_nMainFileInfo.FileName));
					File file2 = new File(this.m_nMainFileInfo.Path, this.m_nMainFileInfo.FileName);
					file.renameTo(file2);
					if (DBHelper.getInstance() != null) {
						DBHelper.getInstance().updateHistoryFilesRecord(new HistoryFiles(-1, this.m_RemoteHostInformation.strMacAddr, null, 3, -1L, -1, this.m_nMainFileInfo.size, null, file2.getPath()), new HistoryFiles(-1, null, null, 5, -1L, -1, -1L, null, null));
					}
				}
			}
			this.m_socketInFileStream.close();
			this.m_socketOutStream.close();
			ServerSocket serverSocket = this.m_tcpReverseServerSocket;
			if (serverSocket != null) {
				serverSocket.close();
				this.m_tcpReverseServerSocket = null;
			}
		}

		public boolean createReverseTcpSocket() throws Exception {
			ServerSocket serverSocket = new ServerSocket(0);
			this.m_tcpReverseServerSocket = serverSocket;
			this.m_nPort = serverSocket.getLocalPort();
			if (this.m_bStopTrans || SendProReq()) {
				return reserveConnectRun();
			}
			return false;
		}

		public boolean SendProReq() {
			return ((IpmsgService) TransFile.this.m_context).m_DataSource.m_Protocol.sendReserveLinkReq(this.m_RemoteHostInformation, this.m_nPort, this.m_nMainFileInfo);
		}

		public boolean reserveConnectRun() throws Exception {
			if (this.m_bStopTrans) {
				return false;
			}
			this.m_sock = this.m_tcpReverseServerSocket.accept();
			return true;
		}
	}

	public class FileSendThread extends IFileTransThread {
		private File m_file;
		private long m_nTmpRootDirLen;
		private OutputStream m_socketOutFileStream;
		private String m_strTimePoint;

		public FileSendThread(Socket socket, FileInformation fileInformation, HostInformation hostInformation) {
			super(socket, fileInformation, hostInformation);
			this.m_file = null;
			this.m_socketOutFileStream = null;
			this.m_strTimePoint = Long.toHexString(Public_Tools.getCurrentTimeMillis());
			this.m_nTmpRootDirLen = 0L;
			this.m_bIsRecv = false;
			if (this.m_nMainFileInfo == null) {
				stop();
				return;
			}
			if (0 == this.m_nMainFileInfo.size && (this.m_nMainFileInfo.nFileAttr & 1) != 0) {
				notifyFile(Public_Def.TransStatus.Trans_Done);
				stop();
				return;
			}
			try {
				this.m_file = new File(this.m_nMainFileInfo.Path, this.m_nMainFileInfo.FileName);
				this.m_socketOutFileStream = this.m_sock.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
				notifyFile(e);
			}
		}

		public FileSendThread(TransFile transFile, Socket socket, long j, long j2, long j3, HostInformation hostInformation) {
			this(socket, transFile.m_fileTranManager.removeRequestingFile(hostInformation.strMacAddr, j, j2, j3), hostInformation);
		}

		private void sendDirProtocal(String str, int i) throws Exception {
			long j = this.m_nTmpRootDirLen;
			if (0 != j) {
				this.m_nTmpRootDirLen = 0L;
			} else {
				j = 0;
			}
			sendProtocal(Public_MsgID.PRO_SPACE + str + Public_MsgID.PRO_SPACE + Long.toHexString(j) + Public_MsgID.PRO_SPACE + Long.toHexString(i) + Public_MsgID.PRO_SPACE + Long.toHexString(20L) + "=" + this.m_strTimePoint + Public_MsgID.PRO_SPACE + Long.toHexString(22L) + "=" + this.m_strTimePoint + Public_MsgID.PRO_SPACE);
		}

		private void sendFileProtocal(String str, long j) throws Exception {
			String str2;
			if (j <= 4.2949673E9f) {
				str2 = Public_MsgID.PRO_SPACE + str + ":0" + Long.toHexString(j) + Public_MsgID.PRO_SPACE + Long.toHexString(1L) + Public_MsgID.PRO_SPACE + Long.toHexString(20L) + "=" + this.m_strTimePoint + Public_MsgID.PRO_SPACE + Long.toHexString(22L) + "=" + this.m_strTimePoint + Public_MsgID.PRO_SPACE;
			} else {
				str2 = Public_MsgID.PRO_SPACE + str + Public_MsgID.PRO_SPACE + Long.toHexString(j) + Public_MsgID.PRO_SPACE + Long.toHexString(1L) + Public_MsgID.PRO_SPACE + Long.toHexString(20L) + "=" + this.m_strTimePoint + Public_MsgID.PRO_SPACE + Long.toHexString(22L) + "=" + this.m_strTimePoint + Public_MsgID.PRO_SPACE;
			}
			sendProtocal(str2);
		}

		private void sendProtocal(String str) throws Exception {
			this.m_socketOutFileStream.write((String.format("%04x", Integer.valueOf(str.getBytes("GBK").length + 4)) + str).getBytes("GBK"));
			this.m_socketOutFileStream.flush();
		}

		private void sendFile(File file) throws Exception {
			long j;
			if (this.m_file.isFile()) {
				j = this.m_nMainFileInfo.startPos;
				this.m_nFinishTotle = this.m_nMainFileInfo.startPos;
			} else {
				j = 0;
			}
			long length = file.length();
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.skip(this.m_nMainFileInfo.startPos);
			while (j < length && !this.m_bStopTrans) {
				int i = fileInputStream.read(this.m_FileBuffer, 0, 262144);
				this.m_socketOutFileStream.write(this.m_FileBuffer, 0, i);
				long j2 = i;
				j += j2;
				this.m_nFinishTotle += j2;
				this.m_socketOutFileStream.flush();
				if (checkFileTransTimeStamp()) {
					notifyFile(file, j, Public_Def.TransStatus.Trans_Sending);
				}
			}
			fileInputStream.close();
		}

		private void sendDir(File file) throws Exception {
			if (file == null) {
				return;
			}
			sendDirProtocal(file.getName(), 2);
			File[] fileArrListFiles = file.listFiles();
			for (int i = 0; i < fileArrListFiles.length; i++) {
				if (fileArrListFiles[i].isDirectory() && this.m_nMainFileInfo.vecFilterType == null) {
					sendDir(fileArrListFiles[i]);
				} else if (isFliter(fileArrListFiles[i])) {
					sendFileProtocal(fileArrListFiles[i].getName(), fileArrListFiles[i].length());
					sendFile(fileArrListFiles[i]);
				}
			}
			sendDirProtocal(".", 3);
		}

		private boolean isFliter(File file) {
			if (this.m_nMainFileInfo.vecFilterType == null) {
				return true;
			}
			String extensionName = Public_Tools.getExtensionName(file.getName());
			for (int i = 0; i < this.m_nMainFileInfo.vecFilterType.size(); i++) {
				if (this.m_nMainFileInfo.vecFilterType.get(i).equals(extensionName)) {
					return true;
				}
			}
			return false;
		}

		@Override // com.netfeige.transport.TransFile.IFileTransThread
		public void Impl() throws Exception {
			if (this.m_file.isDirectory()) {
				this.m_nTmpRootDirLen = this.m_nMainFileInfo.size;
				sendDir(this.m_file);
			} else {
				sendFile(this.m_file);
			}
			this.m_socketOutFileStream.close();
		}
	}

	public abstract class IFileTransThread implements Runnable {
		protected static final int m_nBufferLen = 262144;
		protected static final int m_nByteOfDirCmd = 1024;
		protected static final int m_nByteOfDirCmdLen = 4;
		protected static final int m_nCmdLen = 4;
		private static final int nFileNotfiyFrequency = 800;
		protected HostInformation m_RemoteHostInformation;
		protected Thread m_Thread;
		protected FileInformation m_nMainFileInfo;
		protected NumberFormat m_percentFinish;
		protected Socket m_sock;
		protected boolean m_bStopTrans = false;
		protected byte[] m_FileBuffer = new byte[262144];
		protected long m_nFinishTotle = 0;
		protected boolean m_bIsRecv = true;
		private long m_nTimeStamp = 0;
		private long m_nSpeedStamp = 0;
		private long nSubFinished = 0;

		protected abstract void Impl() throws Exception;

		protected class NotifyFileProgress implements Runnable {
			private static final int TRANS_DIR = 1;
			private static final int TRANS_EXCEPTION = 3;
			private static final int TRANS_FILE = 0;
			private static final int TRANS_RENAMED = 2;
			private Exception m_Exception;
			private int m_currFileNotifyType;
			private String m_currTransSpeed;
			private FileInformation m_originalFile;
			private FileInformation m_subFile;

			protected NotifyFileProgress() {
				this.m_currTransSpeed = null;
				this.m_subFile = null;
				this.m_originalFile = null;
				this.m_currFileNotifyType = 0;
				this.m_Exception = null;
				this.m_currTransSpeed = getCurrSpeed();
				this.m_currFileNotifyType = (IFileTransThread.this.m_nMainFileInfo.nFileAttr & 1) != 1 ? 1 : 0;
			}

			protected NotifyFileProgress(IFileTransThread iFileTransThread, FileInformation fileInformation, long j, Public_Def.TransStatus transStatus) {
				this();
				this.m_subFile = new FileInformation();
				this.m_subFile = fileInformation;
				fileInformation.status = transStatus;
				iFileTransThread.nSubFinished = j;
			}

			protected NotifyFileProgress(IFileTransThread iFileTransThread, FileInformation fileInformation) {
				this();
				this.m_originalFile = fileInformation;
				this.m_currFileNotifyType = 2;
			}

			protected NotifyFileProgress(IFileTransThread iFileTransThread, Exception exc) {
				this();
				this.m_Exception = exc;
				this.m_currFileNotifyType = 3;
			}

			private String getCurrSpeed() {
				long jCurrentTimeMillis = System.currentTimeMillis();
				if (0 == IFileTransThread.this.m_nTimeStamp || jCurrentTimeMillis <= IFileTransThread.this.m_nTimeStamp) {
					return "0KB/s";
				}
				Log.v("mylog", "m_nCurrFinished = " + IFileTransThread.this.m_nFinishTotle + "; m_nSpeedStamp = " + IFileTransThread.this.m_nSpeedStamp);
				long j = ((IFileTransThread.this.m_nFinishTotle - IFileTransThread.this.m_nSpeedStamp) / (jCurrentTimeMillis - IFileTransThread.this.m_nTimeStamp)) * 1000;
				StringBuilder sb = new StringBuilder();
				sb.append(Public_Tools.getShortSize(j));
				sb.append("B/S");
				String string = sb.toString();
				IFileTransThread.this.m_nTimeStamp = jCurrentTimeMillis;
				IFileTransThread iFileTransThread = IFileTransThread.this;
				iFileTransThread.m_nSpeedStamp = iFileTransThread.m_nFinishTotle;
				return string;
			}

			private void notifyInfo() {
				int i = this.m_currFileNotifyType;
				if (i == 0) {
					IFileTransThread.this.m_nMainFileInfo.transNotify.transFile(IFileTransThread.this.m_nMainFileInfo, IFileTransThread.this.m_nFinishTotle, this.m_currTransSpeed);
					return;
				}
				if (i == 1) {
					IFileTransThread.this.m_nMainFileInfo.transNotify.transDir(IFileTransThread.this.m_nMainFileInfo, IFileTransThread.this.m_nFinishTotle, this.m_subFile, IFileTransThread.this.nSubFinished, this.m_currTransSpeed);
				} else if (i == 2) {
					IFileTransThread.this.m_nMainFileInfo.transNotify.reNamed(this.m_originalFile, IFileTransThread.this.m_nMainFileInfo);
				} else {
					if (i != 3) {
						return;
					}
					IFileTransThread.this.m_nMainFileInfo.transNotify.transException(IFileTransThread.this.m_nMainFileInfo, this.m_Exception);
				}
			}

			@Override // java.lang.Runnable
			public void run() {
				if (IFileTransThread.this.m_nMainFileInfo.transNotify == null) {
					return;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("asyn m_currTransSpeed = ");
				sb.append(this.m_currTransSpeed);
				sb.append(" m_nMainFileInfo.FileName = ");
				sb.append(IFileTransThread.this.m_nMainFileInfo.FileName);
				sb.append(" m_nMainFileInfo.status = ");
				sb.append(IFileTransThread.this.m_nMainFileInfo.status);
				sb.append(" m_subFile = ");
				FileInformation fileInformation = this.m_subFile;
				sb.append(fileInformation == null ? null : fileInformation.FileName);
				Log.v("mylog", sb.toString());
				notifyInfo();
			}

			public void syncNotify() {
				if (IFileTransThread.this.m_nMainFileInfo.transNotify == null) {
					return;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("syncNotify m_currTransSpeed = ");
				sb.append(this.m_currTransSpeed);
				sb.append(" m_nMainFileInfo.FileName = ");
				sb.append(IFileTransThread.this.m_nMainFileInfo.FileName);
				sb.append(" m_nMainFileInfo.status = ");
				sb.append(IFileTransThread.this.m_nMainFileInfo.status);
				sb.append(" m_subFile = ");
				FileInformation fileInformation = this.m_subFile;
				sb.append(fileInformation == null ? null : fileInformation.FileName);
				Log.v("mylog", sb.toString());
				notifyInfo();
			}
		}

		protected void notifyFile(FileInformation fileInformation, long j, Public_Def.TransStatus transStatus) {
			FileInformation fileInformation2 = this.m_nMainFileInfo;
			if (fileInformation2 != null) {
				fileInformation2.status = this.m_bIsRecv ? Public_Def.TransStatus.Trans_Recving : Public_Def.TransStatus.Trans_Sending;
				if ((this.m_nMainFileInfo.nFileAttr & 2) != 0) {
					TransFile.this.m_fileTranManager.post(new NotifyFileProgress(this, fileInformation, j, transStatus));
				} else {
					notifyFile(transStatus);
				}
			}
		}

		protected void notifyFile(File file, long j, Public_Def.TransStatus transStatus) {
			if (file == null || this.m_nMainFileInfo == null) {
				return;
			}
			FileInformation fileInformation = new FileInformation();
			fileInformation.Path = file.getParentFile().getPath();
			fileInformation.FileName = file.getName();
			fileInformation.size = file.length();
			fileInformation.nFileAttr = (file.isDirectory() ? 2 : 1) | fileInformation.nFileAttr;
			this.m_nMainFileInfo.status = this.m_bIsRecv ? Public_Def.TransStatus.Trans_Recving : Public_Def.TransStatus.Trans_Sending;
			TransFile.this.m_fileTranManager.post(new NotifyFileProgress(this, fileInformation, j, transStatus));
		}

		protected boolean checkFileTransTimeStamp() {
			return System.currentTimeMillis() - this.m_nTimeStamp > 800;
		}

		protected void notifyFile(Public_Def.TransStatus transStatus) {
			if ((Public_Def.TransStatus.Trans_Recving == transStatus || Public_Def.TransStatus.Trans_Sending == transStatus) && this.m_nMainFileInfo == null) {
				return;
			}
			this.m_nMainFileInfo.status = transStatus;
			TransFile.this.m_fileTranManager.post(new NotifyFileProgress());
		}

		protected void notifyFile(Exception exc) {
			FileInformation fileInformation = this.m_nMainFileInfo;
			if (fileInformation == null) {
				return;
			}
			fileInformation.status = Public_Def.TransStatus.Trans_Error;
			TransFile.this.m_fileTranManager.post(new NotifyFileProgress(this, exc));
		}

		IFileTransThread(Socket socket, FileInformation fileInformation, HostInformation hostInformation) {
			this.m_sock = null;
			this.m_Thread = null;
			this.m_RemoteHostInformation = null;
			this.m_nMainFileInfo = null;
			this.m_percentFinish = null;
			this.m_nMainFileInfo = fileInformation;
			this.m_sock = socket;
			NumberFormat percentInstance = NumberFormat.getPercentInstance();
			this.m_percentFinish = percentInstance;
			percentInstance.setMinimumFractionDigits(0);
			this.m_RemoteHostInformation = hostInformation;
			this.m_Thread = new Thread(this);
		}

		public void start() {
			if (this.m_bStopTrans || this.m_nMainFileInfo == null) {
				return;
			}
			this.m_Thread.start();
			this.m_nTimeStamp = System.currentTimeMillis();
		}

		public void stop() {
			this.m_bStopTrans = true;
		}

		@Override // java.lang.Runnable
		public void run() {
			TransFile.this.m_fileTranManager.addTransThread(this);
			try {
				Impl();
				Log.v("mylog", "recv file is right over ! ");
				if (!this.m_bStopTrans) {
					notifyFile(Public_Def.TransStatus.Trans_Done);
				}
				this.m_sock.close();
			} catch (Exception e) {
				notifyFile(e);
			}
			TransFile.this.m_fileTranManager.removeTransThread(this);
			TransFile.this.m_fileTranManager.removeReserveLinkThread(this.m_nMainFileInfo.nPackageID, this.m_nMainFileInfo.Id);
		}
	}
}

