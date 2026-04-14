package com.netfeige.common;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Process;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;
import com.geniusgithub.mediarender.center.MediaRenderProxy;
import com.netfeige.R;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.ui.ChoiceListView;
import com.netfeige.display.ui.FileListView;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.dlna.HttpServer;
import com.netfeige.enums.FileAccessAuth;
import com.netfeige.filemanager.Catalogue;
import com.netfeige.filemanager.FileComparator;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.protocol.ProPackage;
import com.netfeige.service.IpmsgService;
import com.netfeige.service.MusicService;
import com.netfeige.util.SDCardUtil;
import com.netfeige.wt.WifiAdmin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import org.teleal.cling.model.Constants;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class Public_Tools {
	public static final int WIFI_AP_STATE_DISABLED = 1;
	public static final int WIFI_AP_STATE_DISABLED_Android4 = 11;
	public static final int WIFI_AP_STATE_DISABLING = 0;
	public static final int WIFI_AP_STATE_ENABLED = 3;
	public static final int WIFI_AP_STATE_ENABLED_Android4 = 13;
	public static final int WIFI_AP_STATE_ENABLING = 2;
	public static final int WIFI_AP_STATE_FAILED = 4;
	private static Context m_Context = null;
	private static int m_nFileID = 0;
	private static final String m_strFeige = "/FeigeDownload";
	public static final String m_strFeigeImages = "Feige_Images";
	private static final String m_strNoFeigeDownload = "-1";
	private static final String m_strShareName = "鍒嗕韩";
	private static final String m_strUpdate = "Android鐗堥楦戒紶涔︾畝浠?txt";
	static int nRandom;

	public static long getLowBitCmd(long j) {
		return j & 255;
	}

	public static int[] intToArrayIP(int i) {
		return new int[]{i & 255, (65280 & i) >>> 8, (16711680 & i) >>> 16, (i & ViewCompat.MEASURED_STATE_MASK) >>> 24};
	}

	public static void init(Context context) {
		m_Context = context;
	}

	public static class IpInfo {
		public String strIp;
		public String strNetName;

		public IpInfo(String str, String str2) {
			this.strNetName = str;
			this.strIp = str2;
		}
	}

	public static Vector<IpInfo> getAllLocalHostIP() {
		Vector<IpInfo> vector = new Vector<>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterfaceNextElement = networkInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = networkInterfaceNextElement.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddressNextElement = inetAddresses.nextElement();
					if (!inetAddressNextElement.isLoopbackAddress() && isIPv4Address(inetAddressNextElement.getHostAddress())) {
						vector.add(new IpInfo(networkInterfaceNextElement.getDisplayName(), inetAddressNextElement.getHostAddress()));
					}
				}
			}
		} catch (SocketException e) {
			Log.i("Public_Tools", e.getMessage());
		}
		return vector;
	}

	private static boolean isIPv4Address(String address) {
		if (address == null || address.isEmpty()) {
			return false;
		}
		String[] parts = address.split("\\.");
		if (parts.length != 4) {
			return false;
		}
		for (String part : parts) {
			try {
				int num = Integer.parseInt(part);
				if (num < 0 || num > 255) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	private static IpInfo getIPbyNetName(String str) {
		Vector<IpInfo> allLocalHostIP = getAllLocalHostIP();
		if (allLocalHostIP.isEmpty()) {
			return null;
		}
		for (int i = 0; i < allLocalHostIP.size(); i++) {
			if (allLocalHostIP.get(i).strNetName.contains(str)) {
				return allLocalHostIP.get(i);
			}
		}
		return null;
	}

	public static String getDefaultLocalHostIP() {
		if (getWifiApState()) {
			IpInfo iPbyNetName = getIPbyNetName(Public_MsgID.hotNetDeviceName);
			if (iPbyNetName == null) {
				iPbyNetName = getIPbyNetName(Public_MsgID.hotNetDeviceName_huawei);
			}
			if (iPbyNetName == null) {
				iPbyNetName = getIPbyNetName(Public_MsgID.hotNetDeviceName_coolpad);
			}
			return iPbyNetName == null ? "127.0.0.1" : iPbyNetName.strIp;
		}
		if (isWifiConnect()) {
			return getIpString(((WifiManager) m_Context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
		}
		IpInfo iPbyNetName2 = getIPbyNetName(Public_MsgID.net3GDeviceName);
		return iPbyNetName2 == null ? "127.0.0.1" : iPbyNetName2.strIp;
	}

	public static String getCurrNetName() {
		if (getWifiApState()) {
			IpInfo iPbyNetName = getIPbyNetName(Public_MsgID.hotNetDeviceName);
			if (iPbyNetName == null) {
				return null;
			}
			return "鐑偣 (" + iPbyNetName.strNetName + ")";
		}
		if (isWifiConnect()) {
			WifiInfo connectionInfo = ((WifiManager) m_Context.getSystemService("wifi")).getConnectionInfo();
			if (connectionInfo == null) {
				return null;
			}
			return "WIFI (" + connectionInfo.getSSID() + ")";
		}
		IpInfo iPbyNetName2 = getIPbyNetName(Public_MsgID.net3GDeviceName);
		if (iPbyNetName2 == null) {
			return null;
		}
		return "3G (" + iPbyNetName2.strNetName + ")";
	}

	public static String getLocalMacAddress() {
		String localDeviceId = getLocalDeviceId();
		System.out.println("Mac:" + localDeviceId);
		return localDeviceId;
	}

	public static String getLocalDeviceId() {
		String strRead = DataConfig.getInstance(m_Context).Read(38);
		if (strRead != null && !strRead.isEmpty()) {
			return strRead;
		}
		String upperCase = UUID.randomUUID().toString().toUpperCase();
		DataConfig.getInstance(m_Context).Write(38, upperCase);
		return upperCase;
	}

	public static String getLocalHostName() {
		String str = Build.BRAND;
		String str2 = Build.MODEL;
		if (-1 != str2.toUpperCase().indexOf(str.toUpperCase())) {
			return str2;
		}
		return str + "_" + str2;
	}

	public static String getNetMask() {
		return getIpString(((WifiManager) m_Context.getSystemService("wifi")).getDhcpInfo().netmask);
	}

	public static String getNetGateway() {
		return getIpString(((WifiManager) m_Context.getSystemService("wifi")).getDhcpInfo().gateway);
	}

	public static String getIpString(int i) {
		int[] iArrIntToArrayIP = intToArrayIP(i);
		return "" + iArrIntToArrayIP[0] + "." + iArrIntToArrayIP[1] + "." + iArrIntToArrayIP[2] + "." + iArrIntToArrayIP[3];
	}

	public static HostInformation getLocalHostInfo() {
		if (m_Context == null) {
			return null;
		}
		HostInformation hostInformation = new HostInformation();
		hostInformation.pszHostName = "Android";
		hostInformation.pszHostUserName = "Android";
		hostInformation.groupName = DataConfig.getInstance(m_Context).Read(1);
		hostInformation.pszUserName = DataConfig.getInstance(m_Context).Read(0);
		try {
			hostInformation.IpAddr.netAddr = InetAddress.getByName(getDefaultLocalHostIP());
		} catch (Exception unused) {
			hostInformation.IpAddr.netAddr = null;
		}
		hostInformation.IpAddr.listenPort = 2425;
		hostInformation.strMacAddr = getLocalMacAddress();
		hostInformation.headImage = DataConfig.getInstance(m_Context).Read(36);
		return hostInformation;
	}

	public static Vector<String> getAllLocalIpAddress() {
		Vector<String> vector = new Vector<>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddressNextElement = inetAddresses.nextElement();
					if (!inetAddressNextElement.isLoopbackAddress()) {
						vector.add(inetAddressNextElement.getHostAddress().toString());
					}
				}
			}
		} catch (Exception unused) {
		}
		return vector;
	}

	public static boolean getWifiApState() {
		try {
			WifiManager wifiManager = (WifiManager) m_Context.getSystemService("wifi");
			int iIntValue = ((Integer) wifiManager.getClass().getMethod("getWifiApState", new Class[0]).invoke(wifiManager, new Object[0])).intValue();
			return 3 == iIntValue || 13 == iIntValue;
		} catch (Exception unused) {
			return false;
		}
	}

	public static ProPackage MakeProPackage(ProPackage.PackageType packageType, HostInformation hostInformation, long j, String str) {
		return new ProPackage(packageType, hostInformation, j, str);
	}

	public static ProPackage MakeProPackage(ProPackage.PackageType packageType, HostInformation hostInformation, long j, String str, ArrayList<HostInformation> arrayList) {
		return new ProPackage(packageType, hostInformation, j, str, arrayList);
	}

	public static void showToast(Context context, String str, int i) {
		if (context == null || str == null) {
			return;
		}
		Toast toastMakeText = Toast.makeText(context, str, i);
		toastMakeText.setGravity(17, 0, -30);
		toastMakeText.show();
	}

	public static long getCurrentTimeMillis() {
		if (99 < nRandom) {
			nRandom = 0;
		}
		long jCurrentTimeMillis = System.currentTimeMillis() / 1000;
		int i = nRandom;
		nRandom = i + 1;
		return jCurrentTimeMillis + ((long) i);
	}

	public static synchronized int getFileID() {
		int i;
		if (m_nFileID == 10) {
			m_nFileID = 0;
		}
		i = m_nFileID;
		m_nFileID = i + 1;
		return i;
	}

	public static synchronized long getFileID(long j, int i) {
		return ((Math.abs((int) j) / 10) * 10) + i;
	}

	public static synchronized int getFileIndex(long j) {
		return (int) (j % 10);
	}

	public static long getFileSize(String str) {
		return getFileSize(new File(str));
	}

	public static long getFileSize(File file) {
		long fileSize = 0;
		if (file == null) {
			return 0L;
		}
		if (!file.isDirectory()) {
			return file.length();
		}
		File[] fileArrListFiles = file.listFiles();
		if (fileArrListFiles == null) {
			return 0L;
		}
		for (File file2 : fileArrListFiles) {
			fileSize += getFileSize(file2);
		}
		return fileSize;
	}

	public static void showNotification(IpmsgApplication ipmsgApplication, String str, String str2, int i) {
		Notification notification = new Notification(i, str, System.currentTimeMillis());
		notification.flags = 16;
		Intent intent = new Intent(ipmsgApplication.currentActivity, ipmsgApplication.currentActivity.getClass());
		intent.setFlags(603979776);
		PendingIntent activity = PendingIntent.getActivity(ipmsgApplication.currentActivity, 0, intent, 134217728);
		if (Build.VERSION.SDK_INT < 16) {
			notification.flags = 16;
			try {
				notification.getClass().getDeclaredMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class).invoke(notification, ipmsgApplication.currentActivity, str, str2, activity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(ipmsgApplication.currentActivity);
			builder.setContentIntent(activity);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle(str);
			builder.setContentText(str2);
			builder.setWhen(System.currentTimeMillis());
			builder.setAutoCancel(true);
			builder.setTicker(str);
			notification = builder.build();
		}
		((NotificationManager) ipmsgApplication.getSystemService("notification")).notify(IpmsgApplication.MSG_NOTIFICATION_ID, notification);
	}

	public static void showMsgNotification(IpmsgApplication ipmsgApplication, HostInformation hostInformation, DiscussInfo discussInfo, String str, int i) {
		String str2;
		String str3;
		if (discussInfo != null) {
			str3 = discussInfo.getStrName() + ": " + str;
			str2 = "收到来自讨论组" + discussInfo.getStrName() + " 的消息";
		} else {
			if (hostInformation == null) {
				return;
			}
			String str4 = hostInformation.pszUserName + ": " + str;
			str2 = hostInformation.pszUserName + "给您发来飞信消息,";
			str3 = str4;
		}
		Notification notification = new Notification(i, str3, System.currentTimeMillis());
		Intent intent = new Intent(ipmsgApplication.currentActivity, (Class<?>) IpmsgActivity.class);
		intent.setFlags(603979776);
		PendingIntent activity = PendingIntent.getActivity(ipmsgApplication.currentActivity, 0, intent, 134217728);
		if (Build.VERSION.SDK_INT < 16) {
			notification.flags = 16;
			try {
				notification.getClass().getDeclaredMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class).invoke(notification, ipmsgApplication.currentActivity, str2, str, activity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(ipmsgApplication.currentActivity);
			builder.setContentIntent(activity);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle(str2);
			builder.setContentText(str);
			builder.setWhen(System.currentTimeMillis());
			builder.setAutoCancel(true);
			builder.setTicker(str3);
			notification = builder.build();
		}
		((NotificationManager) ipmsgApplication.getSystemService("notification")).notify(IpmsgApplication.MSG_NOTIFICATION_ID, notification);
	}

	public static int getProgress(long j, long j2) {
		if (0 == j2) {
			return 100;
		}
		double d = j;
		double d2 = j2;
		Double.isNaN(d);
		Double.isNaN(d2);
		int i = (int) ((d / d2) * 100.0d);
		if (i > 100) {
			return 100;
		}
		return i;
	}

	public static void showMainWindow(IpmsgApplication ipmsgApplication) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.setFlags(268435456);
		ipmsgApplication.startActivity(intent);
	}

	public static void exitApp(IpmsgApplication ipmsgApplication) {
		showToast(ipmsgApplication, "程序退出", 1);
		if (ipmsgApplication.g_bRemotePlayerStauts) {
			MediaRenderProxy.getInstance().stopEngine();
		}
		Global.g_bWorking = false;
		IpmsgActivity.g_windowManager = null;
		releaseResource();
		if (getWifiApState()) {
			WifiAdmin.getInstance(m_Context).createWiFiAP(WifiAdmin.getInstance(m_Context).createWifiInfo(ipmsgApplication.g_strAPPref + getLocalHostName(), "Feige6688", 1, "ap"), false);
		}
		cancelTran(ipmsgApplication);
		ipmsgApplication.ipmsgService.m_DataSource.m_Protocol.exitService();
		ipmsgApplication.dismissTempNotification();
		Intent intent = new Intent(ipmsgApplication, (Class<?>) IpmsgService.class);
		ipmsgApplication.unbindService(ipmsgApplication._connection);
		ipmsgApplication.stopService(intent);
		if (ipmsgApplication.getAndroidUpnpService() != null) {
			ipmsgApplication.getAndroidUpnpService().getRegistry().removeListener(ipmsgApplication.getBrowseRegistryListener());
		}
		ipmsgApplication.unbindService(ipmsgApplication.serviceConnection);
// Umeng removed: 		// Umeng removed: MobclickAgent.onKillProcess(ipmsgApplication);
		((ActivityManager) ipmsgApplication.getSystemService("activity")).restartPackage(ipmsgApplication.getPackageName());
		Process.killProcess(Process.myPid());
		System.exit(10);
	}

	public static void cancelTran(IpmsgApplication ipmsgApplication) {
		if (ipmsgApplication.ipmsgService.fileMsgs.size() > 0) {
			for (Map.Entry<String, ArrayList<MsgRecord>> entry : ipmsgApplication.ipmsgService.fileMsgs.entrySet()) {
				String key = entry.getKey();
				HostInformation hostInformation = null;
				ArrayList<MsgRecord> value = entry.getValue();
				for (int i = 0; i < value.size(); i++) {
					MsgRecord msgRecord = value.get(i);
					if (msgRecord.getFileId() != -1) {
						if (hostInformation == null) {
							for (int i2 = 0; i2 < ipmsgApplication.ipmsgService.userList.size(); i2++) {
								if (ipmsgApplication.ipmsgService.userList.get(i2).strMacAddr.equals(key)) {
									hostInformation = ipmsgApplication.ipmsgService.userList.get(i2);
								}
							}
						}
						if (hostInformation == null) {
							break;
						} else if (msgRecord.isSend()) {
							ipmsgApplication.ipmsgService.m_DataSource.m_Protocol.cancelSendFile(msgRecord.getFileInfo(), hostInformation);
						} else {
							ipmsgApplication.ipmsgService.m_DataSource.m_Protocol.cancelRecvFile(msgRecord.getFileInfo(), hostInformation);
						}
					}
				}
			}
		}
	}

	public static void releaseResource() {
		Global.g_imageListCache.clear();
		Global.g_audioListCache.clear();
		Global.g_hostInfo = null;
		Global.g_hostInformation = null;
		Global.g_filePath.clear();
		Global.g_pastePaths.clear();
		Global.g_chiocePaths.clear();
		Catalogue.categoryStack.clear();
		Catalogue.inFileActivityStack.clear();
		ImageAdapter.s_imageList.clear();
		IpmsgApplication.g_arrMusicList.clear();
		IpmsgApplication.g_arrPlayingList.clear();
		IpmsgApplication.g_arrVideoList.clear();
		IpmsgApplication.g_playingVideoList.clear();
		FileManager.m_imageFolderList.clear();
		FileManager.m_audioFolderList.clear();
		FileManager.m_videoFolderList.clear();
		FileManager.m_documentFolderList.clear();
		FileManager.m_apkFolderList.clear();
	}

	public static String getShortSize(long j) {
		String string;
		String str;
		if (j >= 1073741824) {
			string = Float.toString(j / 1.0737418E9f);
			str = "G";
		} else if (j >= 1048576) {
			string = Float.toString(j / 1048576.0f);
			str = "M";
		} else {
			string = Float.toString(j / 1024.0f);
			str = "K";
		}
		int iLastIndexOf = string.lastIndexOf(".");
		if (iLastIndexOf != -1) {
			if (string.length() - iLastIndexOf > 3) {
				return string.substring(0, iLastIndexOf + 3) + str;
			}
			return string + str;
		}
		return string + str;
	}

	public static byte[] arrayToByteIP(int[] iArr) {
		return new byte[]{(byte) (iArr[0] & 255), (byte) (iArr[1] & 255), (byte) (iArr[2] & 255), (byte) (iArr[3] & 255)};
	}

	public static String getDefaultDownloadPath() throws Throwable {
		boolean z;
		IDataConfig dataConfig = DataConfig.getInstance(m_Context);
		String mountPoint = null;
		try {
			ArrayList<SDCardInfo> sDCardInfo = SDCardUtil.getSDCardInfo(m_Context);
			String strRead = dataConfig.Read(35);
			int i = 0;
			if (!strRead.equals(m_strNoFeigeDownload)) {
				if (!strRead.equals(m_Context.getFilesDir().getPath()) && (!Environment.getExternalStorageState().equals("mounted") || !strRead.equals(Environment.getExternalStorageDirectory().toString()))) {
					if (!sDCardInfo.isEmpty()) {
						for (int i2 = 0; i2 < sDCardInfo.size(); i2++) {
							if (!strRead.equals(sDCardInfo.get(i2).getMountPoint()) || authority(new File(strRead)) != FileAccessAuth.RW_OK) {
							}
						}
					}
					z = true;
				} else {
					z = false;
				}
			} else {
				z = true;
			}
			if (z) {
				if (Environment.getExternalStorageState().equals("mounted")) {
					mountPoint = Environment.getExternalStorageDirectory().toString();
				} else if (!sDCardInfo.isEmpty()) {
					while (true) {
						if (i >= sDCardInfo.size()) {
							break;
						}
						if (authority(new File(sDCardInfo.get(i).getMountPoint())) == FileAccessAuth.RW_OK) {
							mountPoint = sDCardInfo.get(i).getMountPoint();
							break;
						}
						i++;
					}
				}
				if (mountPoint == null) {
					mountPoint = m_Context.getFilesDir().getPath();
				}
				dataConfig.Write(35, mountPoint);
			} else {
				mountPoint = strRead;
			}
			mountPoint = mountPoint + m_strFeige;
			writeData(mountPoint);
			return mountPoint;
		} catch (Exception unused) {
			dataConfig.Write(35, m_strNoFeigeDownload);
			return mountPoint;
		}
	}

	private static void writeData(String str) throws Throwable {
		File file = new File(str);
		if (!file.exists()) {
			file.mkdir();
			writeHelp(file, getRawResource(R.raw.update), m_strUpdate);
		}
		File file2 = new File(str + File.separator + m_strFeigeImages);
		if (file2.exists()) {
			return;
		}
		file2.mkdir();
	}

	private static void writeHelp(File file, byte[] bArr, String str) throws Throwable {
		FileOutputStream fileOutputStream = null;
		try {
			try {
				try {
					File file2 = new File(file, str);
					file2.createNewFile();
					FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
					try {
						fileOutputStream2.write(bArr);
						fileOutputStream2.close();
					} catch (Exception e) {
						fileOutputStream2.close();
						e.printStackTrace();
					} catch (Throwable th) {
						fileOutputStream2.close();
						throw th;
					}
				} catch (Throwable th2) {
				}
			} catch (Exception e3) {
				e3.printStackTrace();
				inputStreamOpenRawResource.close();
				return bArr2;
			}
		} catch (IOException e4) {
			e4.printStackTrace();
		}
	}

	private static byte[] getRawResource(int i) throws Throwable {
		Throwable th;
		InputStream inputStreamOpenRawResource;
		byte[] bArr;
		InputStream inputStream = null;
		byte[] bArr2 = null;
		inputStream = null;
		try {
			try {
				inputStreamOpenRawResource = m_Context.getResources().openRawResource(i);
				try {
					bArr2 = new byte[inputStreamOpenRawResource.available()];
					inputStreamOpenRawResource.read(bArr2);
					inputStreamOpenRawResource.close();
					try {
						inputStreamOpenRawResource.close();
						return bArr2;
					} catch (IOException e) {
						e.printStackTrace();
						return bArr2;
					}
				} catch (Exception e2) {
					e2.printStackTrace();
					try {
						inputStreamOpenRawResource.close();
					} catch (IOException e3) {
						e3.printStackTrace();
					}
					return bArr2;
				} catch (Throwable th2) {
					inputStreamOpenRawResource.close();
					throw th2;
				}
			} catch (Throwable th3) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e4) {
						e4.printStackTrace();
					}
				}
				throw th3;
			}
			return bArr2;
		} catch (Exception e5) {
			e5.printStackTrace();
			return null;
		}
	}

	public static String getNumber(String str) {
		if (str == null) {
			return ContentTree.ROOT_ID;
		}
		String str2 = "";
		for (int i = 0; i < str.length(); i++) {
			char cCharAt = str.charAt(i);
			if (cCharAt <= '9' && cCharAt >= '0') {
				str2 = str2 + cCharAt;
			}
		}
		return str2;
	}

	public static boolean isWifiConnect() {
		return ((ConnectivityManager) m_Context.getSystemService("connectivity")).getNetworkInfo(1).isConnected() || getWifiApState();
	}

	public static void entryNetSetting() {
		m_Context.startActivity(new Intent("android.settings.WIFI_SETTINGS").addFlags(268435456));
	}

	public static long getAvailableSize() {
		try {
			return new File(getDefaultDownloadPath()).getUsableSpace();
		} catch (Exception unused) {
			return 0L;
		}
	}

	public static String getVersion() {
		try {
			return m_Context.getPackageManager().getPackageInfo(m_Context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static boolean compareVersion(String str) {
		if (str == null) {
			return false;
		}
		return str.equals(Constants.PRODUCT_TOKEN_VERSION) || str.equals("2.0") || str.equals("2.1.0") || str.equals("2.2.0227") || str.equals("2.2.1") || str.equals("2.3") || str.equals("2.3.1") || str.equals("2.3.2") || str.equals("2.3.5") || str.equals("2.4.0") || str.equals("3.0.1") || str.equals("3.0.2");
	}

	public static String getCurrrentConnectionName(Context context) {
		NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
		return (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) ? "" : activeNetworkInfo.getTypeName();
	}

	public static String lastModifiedTime(long j) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.setTimeInMillis(j);
		return simpleDateFormat.format(calendar.getTime());
	}

	public static String getSDCardPath() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	public static void backForward(ArrayList<FileInfo> arrayList) {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(m_strNoFeigeDownload);
		fileInfo.setName("上一级");
		fileInfo.setPath("上一级");
		fileInfo.setFloder(true);
		arrayList.add(fileInfo);
	}

	public static void fileCompare(ArrayList<FileInfo> arrayList) {
		String strRead = DataConfig.getInstance(m_Context).Read(30);
		if (strRead.equals(ContentTree.ROOT_ID)) {
			Collections.sort(arrayList, FileComparator.g_nameAscComparator);
			return;
		}
		if (strRead.equals(ContentTree.VIDEO_ID)) {
			Collections.sort(arrayList, FileComparator.g_nameDesComparator);
		} else if (strRead.equals(ContentTree.AUDIO_ID)) {
			Collections.sort(arrayList, FileComparator.g_lastTimeAscComparator);
		} else {
			Collections.sort(arrayList, FileComparator.g_lastTimeDesComparator);
		}
	}

	public static void musicCompare(ArrayList<Music> arrayList) {
		String strRead = DataConfig.getInstance(m_Context).Read(30);
		if (strRead.equals(ContentTree.ROOT_ID)) {
			Collections.sort(arrayList, FileComparator.g_nameAscComparatorForMusic);
			return;
		}
		if (strRead.equals(ContentTree.VIDEO_ID)) {
			Collections.sort(arrayList, FileComparator.g_nameDesComparatorForMusic);
		} else if (strRead.equals(ContentTree.AUDIO_ID)) {
			Collections.sort(arrayList, FileComparator.g_lastTimeAscComparatorForMusic);
		} else {
			Collections.sort(arrayList, FileComparator.g_lastTimeDesComparatorForMusic);
		}
	}

	public static void imageCompare(ArrayList<ImagePreview> arrayList) {
		String strRead = DataConfig.getInstance(m_Context).Read(30);
		if (strRead.equals(ContentTree.ROOT_ID)) {
			Collections.sort(arrayList, FileComparator.g_nameAscComparatorForImage);
			return;
		}
		if (strRead.equals(ContentTree.VIDEO_ID)) {
			Collections.sort(arrayList, FileComparator.g_nameDesComparatorForImage);
		} else if (strRead.equals(ContentTree.AUDIO_ID)) {
			Collections.sort(arrayList, FileComparator.g_lastTimeAscComparatorForImage);
		} else {
			Collections.sort(arrayList, FileComparator.g_lastTimeDesComparatorForImage);
		}
	}

	public static Bitmap decodeBitmap(String str, int i, int i2) {
		Bitmap bitmapCreateScaledBitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(str, options);
			int iCeil = (int) Math.ceil(options.outWidth / i);
			int iCeil2 = (int) Math.ceil(options.outHeight / i2);
			if (iCeil > 1 && iCeil2 > 1) {
				if (iCeil > iCeil2) {
					options.inSampleSize = iCeil;
				} else {
					options.inSampleSize = iCeil2;
				}
			}
			options.inJustDecodeBounds = false;
			Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str, options);
			if (bitmapDecodeFile == null) {
				return null;
			}
			bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapDecodeFile, i, i2, true);
			bitmapDecodeFile.recycle();
			return bitmapCreateScaledBitmap;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return bitmapCreateScaledBitmap;
		} catch (Exception e2) {
			e2.printStackTrace();
			return bitmapCreateScaledBitmap;
		}
	}

	public static Bitmap decodeBitmap2(String str, int i, int i2) {
		Bitmap bitmapDecodeFile = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmapDecodeFile = BitmapFactory.decodeFile(str, options);
			int i3 = options.outWidth;
			int i4 = options.outHeight;
			Display defaultDisplay = IpmsgActivity.g_windowManager.getDefaultDisplay();
			int width = (defaultDisplay.getWidth() * 2) / 3;
			int height = (defaultDisplay.getHeight() * 2) / 3;
			if (i > 0) {
				width = (i * 2) / 3;
			}
			if (i2 > 0) {
				height = (i2 * 2) / 3;
			}
			options.inSampleSize = 1;
			if ((i > 0 || i2 > 0) && (i <= 0 || i2 <= 0)) {
				if (i > 0) {
					if (i3 > width) {
						options.inSampleSize = i3 / width;
					}
				} else if (i2 > 0 && i4 > height) {
					options.inSampleSize = i4 / height;
				}
			} else if (i3 > i4) {
				if (i3 > width) {
					options.inSampleSize = i3 / width;
				}
			} else if (i4 > height) {
				options.inSampleSize = i4 / height;
			}
			options.inJustDecodeBounds = false;
			try {
				return BitmapFactory.decodeFile(str, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return bitmapDecodeFile;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			return bitmapDecodeFile;
		}
	}

	public static boolean isImageFile(String str) {
		try {
			String lowerCase = str.toLowerCase();
			if (!lowerCase.endsWith(".jpeg") && !lowerCase.endsWith(".jpg") && !lowerCase.endsWith(".png") && !lowerCase.endsWith(".bmp")) {
				if (!lowerCase.endsWith(".gif")) {
					return false;
				}
			}
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public static boolean isAudioFile(String str) {
		try {
			String lowerCase = str.toLowerCase();
			if (!lowerCase.endsWith(".mp3")) {
				if (!lowerCase.endsWith(".wma")) {
					return false;
				}
			}
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public static boolean isVideoFile(String str) {
		try {
			String lowerCase = str.toLowerCase();
			if (!lowerCase.endsWith(".mp4") && !lowerCase.endsWith(".rmvb") && !lowerCase.endsWith(".3gp") && !lowerCase.endsWith(".avi")) {
				if (!lowerCase.endsWith(".wmv")) {
					return false;
				}
			}
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public static boolean isDocumentFile(String str) {
		try {
			String lowerCase = str.toLowerCase();
			if (!lowerCase.endsWith(".txt") && !lowerCase.endsWith(".doc") && !lowerCase.endsWith(".docx") && !lowerCase.endsWith(".xls") && !lowerCase.endsWith(".xlsx") && !lowerCase.endsWith(".ppt") && !lowerCase.endsWith(".pptx") && !lowerCase.endsWith(".pdf") && !lowerCase.endsWith(".pps")) {
				if (!lowerCase.endsWith(".rtf")) {
					return false;
				}
			}
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public static boolean isApkFile(String str) {
		try {
			return str.toLowerCase().endsWith(".apk");
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public static boolean isTxtFile(String str) {
		try {
			String lowerCase = str.toLowerCase();
			if (!lowerCase.endsWith(".txt")) {
				if (!lowerCase.endsWith(".TXT")) {
					return false;
				}
			}
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public static FileAccessAuth authority(File file) {
		FileAccessAuth fileAccessAuth = FileAccessAuth.NONE;
		boolean zCanRead = file.canRead();
		boolean zCanWrite = file.canWrite();
		if (!file.exists()) {
			return fileAccessAuth;
		}
		if (zCanRead && zCanWrite) {
			return FileAccessAuth.RW_OK;
		}
		if (zCanRead) {
			return FileAccessAuth.R_OK;
		}
		return zCanWrite ? FileAccessAuth.W_OK : fileAccessAuth;
	}

	public static int fileCategory(String str) {
		String lowerCase = str.toLowerCase();
		if (lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".bmp") || lowerCase.endsWith(".gif")) {
			return 0;
		}
		if (lowerCase.endsWith(".mp3") || lowerCase.endsWith(".wma")) {
			return 1;
		}
		if (lowerCase.endsWith(".mp4") || lowerCase.endsWith(".rmvb") || lowerCase.endsWith(".3gp") || lowerCase.endsWith(".avi") || lowerCase.endsWith(".wmv")) {
			return 2;
		}
		if (lowerCase.endsWith(".txt") || lowerCase.endsWith(".doc") || lowerCase.endsWith(".docx") || lowerCase.endsWith(".xls") || lowerCase.endsWith(".xlsx") || lowerCase.endsWith(".ppt") || lowerCase.endsWith(".pptx") || lowerCase.endsWith(".pdf") || lowerCase.endsWith(".pps")) {
			return 3;
		}
		return lowerCase.endsWith(".apk") ? 4 : -1;
	}

	public static String subductionPath(String str) {
		String[] strArrSplit;
		int length;
		if (str != null && str != "") {
			try {
				int iLastIndexOf = str.lastIndexOf(ServiceReference.DELIMITER);
				if (iLastIndexOf != -1) {
					if (str.length() != iLastIndexOf + 1) {
						String[] strArrSplit2 = str.split(ServiceReference.DELIMITER);
						int length2 = strArrSplit2.length;
						if (length2 >= 3) {
							str = strArrSplit2[length2 - 2] + ServiceReference.DELIMITER + strArrSplit2[length2 - 1];
						}
					} else if (iLastIndexOf > 0 && (length = (strArrSplit = str.substring(0, iLastIndexOf).split(ServiceReference.DELIMITER)).length) >= 3) {
						str = strArrSplit[length - 2] + ServiceReference.DELIMITER + strArrSplit[length - 1];
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return str;
	}

	public static String cutName(String str) {
		int i;
		if (str == null || str == "") {
			return str;
		}
		try {
			int iLastIndexOf = str.lastIndexOf(ServiceReference.DELIMITER);
			return (iLastIndexOf == -1 || str.length() == (i = iLastIndexOf + 1) || iLastIndexOf <= 0) ? str : str.substring(0, i);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return str;
		} catch (Exception e2) {
			e2.printStackTrace();
			return str;
		}
	}

	public static int getPositionOnPath(ArrayList<FileInfo> arrayList, String str) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).getPath().endsWith(str)) {
				return i;
			}
		}
		return -1;
	}

	public static int getMusicPositionOnPath(ArrayList<Music> arrayList, String str) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).getPath().endsWith(str)) {
				return i;
			}
		}
		return -1;
	}

	public static int getImagePositionOnPath(ArrayList<ImagePreview> arrayList, String str) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).getM_strImagePath().endsWith(str)) {
				return i;
			}
		}
		return -1;
	}

	public static int getPositionOnName(ArrayList<FileInfo> arrayList, String str) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (str.equals(arrayList.get(i).getName())) {
				return i;
			}
		}
		return -1;
	}

	public static int dip2px(Context context, float f) {
		return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
	}

	public static int px2dip(Context context, float f) {
		return (int) ((f / context.getResources().getDisplayMetrics().density) + 0.5f);
	}

	public static boolean isContain(String[] strArr, String str) {
		for (String str2 : strArr) {
			if (str.equals(str2)) {
				return true;
			}
		}
		return false;
	}

	public static void removeMusic(String str) {
		if (MusicService.s_nCurrentPlayingPath.equals(str)) {
			MusicService.s_mediaPlayer.stop();
			MusicService.s_mediaPlayer.reset();
			MusicService.s_mediaPlayer.release();
			MusicService.s_mediaPlayer = null;
		}
		int musicPositionOnPath = getMusicPositionOnPath(IpmsgApplication.g_arrPlayingList, str);
		if (musicPositionOnPath != -1) {
			IpmsgApplication.g_arrPlayingList.remove(musicPositionOnPath);
		}
		int musicPositionOnPath2 = getMusicPositionOnPath(IpmsgApplication.g_arrMusicList, str);
		if (musicPositionOnPath2 != -1) {
			IpmsgApplication.g_arrMusicList.remove(musicPositionOnPath2);
		}
	}

	public static void removeImage(String str) {
		int imagePositionOnPath = getImagePositionOnPath(ImageAdapter.s_imageList, str);
		if (imagePositionOnPath != -1) {
			Bitmap m_imageBitmap = ImageAdapter.s_imageList.get(imagePositionOnPath).getM_imageBitmap();
			if (m_imageBitmap != null && !m_imageBitmap.isRecycled()) {
				m_imageBitmap.recycle();
			}
			ImageAdapter.s_imageList.remove(imagePositionOnPath);
		}
	}

	/* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:21:0x0064 -> B:42:0x007f). Please report as a decompilation issue!!! */
	public static Bitmap getOptimizedBitmap(String str) {
		FileInputStream fileInputStream;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inDither = false;
		options.inPurgeable = true;
		options.inTempStorage = new byte[PlatformType.IOS_4];
		Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str, options);
		int iCeil = (int) Math.ceil(options.outWidth / 600);
		int iCeil2 = (int) Math.ceil(options.outHeight / 800);
		if (iCeil > 1 && iCeil2 > 1) {
			options.inSampleSize = 2;
		} else {
			options.inSampleSize = 1;
		}
		options.inJustDecodeBounds = false;
		if (bitmapDecodeFile != null) {
			bitmapDecodeFile.recycle();
		}
		File file = new File(str);
		Bitmap bitmapDecodeFileDescriptor = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fileInputStream = null;
		}
		if (fileInputStream != null) {
			try {
				try {
					try {
						bitmapDecodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD(), null, options);
						if (fileInputStream != null) {
							fileInputStream.close();
						}
					} catch (Throwable th) {
						if (fileInputStream != null) {
							try {
								fileInputStream.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
						throw th;
					}
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			} catch (IOException e4) {
				e4.printStackTrace();
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				return bitmapDecodeFileDescriptor;
			}
		}
		return bitmapDecodeFileDescriptor;
	}

	public void distoryBitmap(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		bitmap.recycle();
	}

	public static long getDuration(String str) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		long duration = 0;
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(str);
			mediaPlayer.prepare();
			duration = mediaPlayer.getDuration();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		} catch (IllegalStateException e3) {
			e3.printStackTrace();
		}
		mediaPlayer.release();
		return duration;
	}

	public static String toTime(int i) {
		int i2 = i / 1000;
		return String.format("%02d:%02d", Integer.valueOf((i2 / 60) % 60), Integer.valueOf(i2 % 60));
	}

	public static int getCharNumbers(String str) {
		int i = 0;
		for (int i2 = 0; i2 < str.length(); i2++) {
			if (str.charAt(i2) == '/') {
				i++;
			}
		}
		return i;
	}

	public static int isInDeletedDirectory(String str, String str2) {
		try {
			return str.indexOf(str2);
		} catch (Exception unused) {
			return -1;
		}
	}

	public static String getExtensionName(String str) {
		int iLastIndexOf;
		return (str == null || str.length() <= 0 || (iLastIndexOf = str.lastIndexOf(46)) <= -1 || iLastIndexOf >= str.length() + (-1)) ? str : str.substring(iLastIndexOf + 1);
	}

	public static String getFileName(String str) {
		return new File(str).getName();
	}

	public static String getFolderPath(String str) {
		int iLastIndexOf = str.lastIndexOf(ServiceReference.DELIMITER);
		return iLastIndexOf != -1 ? str.substring(0, iLastIndexOf) : "";
	}

	public static String getFolderName(String str) {
		int i;
		int iLastIndexOf = str.lastIndexOf(ServiceReference.DELIMITER);
		int length = str.length();
		return (iLastIndexOf == -1 || length <= (i = iLastIndexOf + 1)) ? "" : str.substring(i, length);
	}

	public static void path2FileInfoForMian(String str, FileListView fileListView) {
		FileInfo fileInfoPath2FileInfo = path2FileInfo(str);
		try {
			fileListView.getFileAdapter().getFileList().add(fileInfoPath2FileInfo);
			if (isImageFile(fileInfoPath2FileInfo.getName())) {
				ImageAdapter.s_imageList.add(new ImagePreview(0, fileInfoPath2FileInfo.getName(), fileInfoPath2FileInfo.getPath(), null, fileInfoPath2FileInfo.getLastTime()));
			} else if (isAudioFile(fileInfoPath2FileInfo.getName())) {
				IpmsgApplication.g_arrMusicList.add(new Music(0, fileInfoPath2FileInfo.getName(), fileInfoPath2FileInfo.getPath(), -1L, fileInfoPath2FileInfo.getLastTime()));
			} else if (isVideoFile(fileInfoPath2FileInfo.getName())) {
				IpmsgApplication.g_arrVideoList.add(new Music(0, fileInfoPath2FileInfo.getName(), fileInfoPath2FileInfo.getPath(), -1L, fileInfoPath2FileInfo.getLastTime()));
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public static void path2FileInfoForChoice(String str, ChoiceListView choiceListView) {
		FileInfo fileInfoPath2FileInfo = path2FileInfo(str);
		try {
			choiceListView.getFileAdapter().getFileList().add(fileInfoPath2FileInfo);
			if (isImageFile(fileInfoPath2FileInfo.getName())) {
				ImageAdapter.s_imageList.add(new ImagePreview(0, fileInfoPath2FileInfo.getName(), fileInfoPath2FileInfo.getPath(), null, fileInfoPath2FileInfo.getLastTime()));
			} else if (isAudioFile(fileInfoPath2FileInfo.getName())) {
				IpmsgApplication.g_arrMusicList.add(new Music(0, fileInfoPath2FileInfo.getName(), fileInfoPath2FileInfo.getPath(), -1L, fileInfoPath2FileInfo.getLastTime()));
			} else if (isVideoFile(fileInfoPath2FileInfo.getName())) {
				IpmsgApplication.g_arrVideoList.add(new Music(0, fileInfoPath2FileInfo.getName(), fileInfoPath2FileInfo.getPath(), -1L, fileInfoPath2FileInfo.getLastTime()));
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public static FileInfo path2FileInfo(String str) {
		FileInfo fileInfo = new FileInfo();
		File file = new File(str);
		String name = file.getName();
		boolean zIsFile = file.isFile();
		fileInfo.setPath(str);
		fileInfo.setName(name);
		fileInfo.setFloder(!zIsFile);
		fileInfo.setLastTime(file.lastModified());
		if (zIsFile) {
			fileInfo.setSize(file.length());
		} else {
			fileInfo.setSize(0L);
		}
		return fileInfo;
	}

	public static void writeSqlite(DBHelper dBHelper) {
		for (int i = 0; i < FileManager.m_imageFolderList.size(); i++) {
			String str = FileManager.m_imageFolderList.get(i);
			if (!isEmptyFolder(str)) {
				dBHelper.insertFolderPath("image", str);
			}
		}
		for (int i2 = 0; i2 < FileManager.m_audioFolderList.size(); i2++) {
			String str2 = FileManager.m_audioFolderList.get(i2);
			if (!isEmptyFolder(str2)) {
				dBHelper.insertFolderPath("audio", str2);
			}
		}
		for (int i3 = 0; i3 < FileManager.m_videoFolderList.size(); i3++) {
			String str3 = FileManager.m_videoFolderList.get(i3);
			if (!isEmptyFolder(str3)) {
				dBHelper.insertFolderPath("video", str3);
			}
		}
		for (int i4 = 0; i4 < FileManager.m_documentFolderList.size(); i4++) {
			String str4 = FileManager.m_documentFolderList.get(i4);
			if (!isEmptyFolder(str4)) {
				dBHelper.insertFolderPath("document", str4);
			}
		}
		for (int i5 = 0; i5 < FileManager.m_apkFolderList.size(); i5++) {
			String str5 = FileManager.m_apkFolderList.get(i5);
			if (!isEmptyFolder(str5)) {
				dBHelper.insertFolderPath("apk", str5);
			}
		}
	}

	public static boolean isEmptyFolder(String str) {
		File file = new File(str);
		return !file.exists() || file.listFiles().length == 0;
	}

	public static void isContain(String str) {
		if (FileManager.m_imageFolderList.contains(str)) {
			FileManager.m_imageFolderList.remove(str);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_audioFolderList.contains(str)) {
			FileManager.m_audioFolderList.remove(str);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_videoFolderList.contains(str)) {
			FileManager.m_videoFolderList.remove(str);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_documentFolderList.contains(str)) {
			FileManager.m_documentFolderList.remove(str);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_apkFolderList.contains(str)) {
			FileManager.m_apkFolderList.remove(str);
			Global.g_bIsUpdateSQLite = true;
		}
	}

	public static void updateFolderList(String str, String str2) {
		if (FileManager.m_imageFolderList.contains(str)) {
			FileManager.m_imageFolderList.remove(str);
			FileManager.m_imageFolderList.add(str2);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_audioFolderList.contains(str)) {
			FileManager.m_audioFolderList.remove(str);
			FileManager.m_audioFolderList.add(str2);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_videoFolderList.contains(str)) {
			FileManager.m_videoFolderList.remove(str);
			FileManager.m_videoFolderList.add(str2);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_documentFolderList.contains(str)) {
			FileManager.m_documentFolderList.remove(str);
			FileManager.m_documentFolderList.add(str2);
			Global.g_bIsUpdateSQLite = true;
		}
		if (FileManager.m_apkFolderList.contains(str)) {
			FileManager.m_apkFolderList.remove(str);
			FileManager.m_apkFolderList.add(str2);
			Global.g_bIsUpdateSQLite = true;
		}
	}

	public static int[] categoryInFolder(String str) {
		File[] fileArrListFiles = new File(str).listFiles();
		int length = fileArrListFiles.length;
		int[] iArr = new int[length];
		for (int i = 0; i < length; i++) {
			iArr[i] = fileCategory(fileArrListFiles[i].getName());
		}
		return iArr;
	}

	public static void limited(ArrayList<FileInfo> arrayList, int i) {
		try {
			Global.g_bLimited = false;
			if ((Global.g_whatFolder == Category.image || Global.g_whatFolder == Category.audio || Global.g_whatFolder == Category.video || Global.g_whatFolder == Category.document || Global.g_whatFolder == Category.apk) && !new File(arrayList.get(i).getPath()).isFile()) {
				Global.g_bLimited = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public static void reSort() {
		IpmsgActivity.s_fileListView.getFileAdapter().getFileList().remove(0);
		backForward(IpmsgActivity.s_fileListView.getFileAdapter().getFileList());
		fileCompare(IpmsgActivity.s_fileListView.getFileAdapter().getFileList());
		IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
	}

	public static void share(Context context, String str) {
		try {
			context.startActivity(getShareIntent(str));
		} catch (Exception unused) {
			Toast.makeText(context, R.string.notshare, 1).show();
		}
	}

	private static Intent getShareIntent(String str) {
		File file;
		Intent intent = new Intent("android.intent.action.SEND");
		if (str == null) {
			intent.setType(HttpServer.MIME_PLAINTEXT);
			file = null;
		} else {
			file = new File(str);
			setFileType(str, intent);
		}
		List<ResolveInfo> listQueryIntentActivities = IpmsgActivity.g_packageManager.queryIntentActivities(intent, 0);
		if (listQueryIntentActivities.isEmpty()) {
			return null;
		}
		ArrayList arrayList = new ArrayList();
		for (ResolveInfo resolveInfo : listQueryIntentActivities) {
			Intent intent2 = new Intent("android.intent.action.SEND");
			if (str == null) {
				intent2.setType(HttpServer.MIME_PLAINTEXT);
			} else {
				setFileType(str, intent2);
			}
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			if (!activityInfo.packageName.contains("netfeige") && !activityInfo.name.contains("netfeige")) {
				if (str == null) {
					intent2.putExtra("android.intent.extra.SUBJECT", m_strShareName);
					intent2.putExtra("android.intent.extra.TEXT", "椋為附浼犱功锛屾渶涓撲笟鐨勫眬鍩熺綉閫氳杞欢锛屾敮鎸丳C銆佹墜鏈恒€丳ad璺ㄥ钩鍙版枃浠朵紶杈撱€佹墦鍗板拰绀句氦骞冲彴鍒嗕韩  http://www.feige360.com/smartdl/index.asp");
				} else {
					intent2.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
				}
				intent2.setFlags(268435456);
				intent2.setPackage(activityInfo.packageName);
				intent2.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
				arrayList.add(intent2);
			}
		}
		Intent intentCreateChooser = Intent.createChooser((Intent) arrayList.remove(0), m_strShareName);
		if (intentCreateChooser != null) {
			intentCreateChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
		}
		return intentCreateChooser;
	}

	private static void setFileType(String str, Intent intent) {
		if (isImageFile(str)) {
			intent.setType("image/*");
			return;
		}
		if (isAudioFile(str)) {
			intent.setType("audio/*");
			return;
		}
		if (isDocumentFile(str)) {
			intent.setType("application/*");
			return;
		}
		if (isVideoFile(str)) {
			intent.setType("video/*");
		} else if (isApkFile(str)) {
			intent.setType("application/*");
		} else {
			intent.setType("*/*");
		}
	}

	public static InetAddress getLocalIpAddress() {
		try {
			int ipAddress = ((WifiManager) m_Context.getSystemService("wifi")).getConnectionInfo().getIpAddress();
			return InetAddress.getByName(String.format("%d.%d.%d.%d", Integer.valueOf(ipAddress & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255)));
		} catch (UnknownHostException unused) {
			return null;
		}
	}

	public static boolean isTopActivy(String str) {
		String className;
		List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) m_Context.getSystemService("activity")).getRunningTasks(1);
		if (runningTasks != null) {
			className = runningTasks.get(0).topActivity.getClassName();
			Log.e("cmdname", "cmdname:" + className);
		} else {
			className = null;
		}
		if (className == null) {
			return false;
		}
		return className.equals(str);
	}
}

