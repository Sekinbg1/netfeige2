package com.netfeige.transport;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.protocol.ProPackage;
import com.netfeige.service.IpmsgService;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class TransMsg implements Runnable {
	private DatagramSocket m_udpSock = null;
	private Thread m_udpListenerThread = null;
	private boolean m_isRunUdpListener = true;
	private Context m_context = null;
	private ITransNotify m_protocolNotify = null;
	private PackageLoseChecker m_packageLoseChecker = new PackageLoseChecker();
	private PackageRepeatChecker m_packageRepeatChecker = new PackageRepeatChecker();

	public boolean start(Context context, ITransNotify iTransNotify) throws SocketException {
		if (iTransNotify == null) {
			return true;
		}
		this.m_context = context;
		this.m_protocolNotify = iTransNotify;
		if (!bind()) {
			return false;
		}
		Thread thread = new Thread(this);
		this.m_udpListenerThread = thread;
		thread.start();
		this.m_packageLoseChecker.start();
		return true;
	}

	public boolean bind() throws SocketException {
		this.m_udpSock = new DatagramSocket(2425);
		return true;
	}

	public void broadcastMessage(ProPackage proPackage) {
		if (proPackage == null) {
			return;
		}
		TransPackage transPackage = new TransPackage(proPackage);
		if (this.m_udpSock == null || this.m_context == null) {
			return;
		}
		try {
			byte[] bytes = transPackage.toString().getBytes("GBK");
			System.out.println("broadcast data:" + transPackage.toString());
			DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
			datagramPacket.setPort(2425);
			datagramPacket.setAddress(InetAddress.getByName(Public_Tools.getDefaultLocalHostIP()));
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			this.m_udpSock.send(datagramPacket);
			if (proPackage.userList != null && !proPackage.userList.isEmpty()) {
				for (int i = 0; i < proPackage.userList.size(); i++) {
					datagramPacket.setAddress(proPackage.userList.get(i).IpAddr.netAddr);
					datagramPacket.setPort(proPackage.userList.get(i).IpAddr.listenPort);
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					this.m_udpSock.send(datagramPacket);
				}
			}
			datagramPacket.setPort(2425);
			int[] iArrIntToArrayIP = {0, 0, 0, 0};
			int[] iArrIntToArrayIP2 = {0, 0, 0, 0};
			if (Public_Tools.getWifiApState()) {
				String defaultLocalHostIP = Public_Tools.getDefaultLocalHostIP();
				if (defaultLocalHostIP == null) {
					return;
				}
				String[] strArrSplit = defaultLocalHostIP.split("\\.");
				iArrIntToArrayIP[0] = Integer.parseInt(strArrSplit[0]);
				iArrIntToArrayIP[1] = Integer.parseInt(strArrSplit[1]);
				iArrIntToArrayIP[2] = Integer.parseInt(strArrSplit[2]);
				iArrIntToArrayIP[3] = 1;
				iArrIntToArrayIP2[0] = iArrIntToArrayIP[0];
				iArrIntToArrayIP2[1] = iArrIntToArrayIP[1];
				iArrIntToArrayIP2[2] = iArrIntToArrayIP[2];
				iArrIntToArrayIP2[3] = 254;
			} else if (Public_Tools.isWifiConnect()) {
				DhcpInfo dhcpInfo = ((WifiManager) this.m_context.getSystemService("wifi")).getDhcpInfo();
				int i2 = dhcpInfo.netmask & dhcpInfo.ipAddress;
				IpmsgApplication ipmsgApplication = (IpmsgApplication) ((IpmsgService) this.m_context).getApplication();
				for (int i3 = 0; i3 < ipmsgApplication.g_listNetSectors.size(); i3++) {
					String str = ipmsgApplication.g_listNetSectors.get(i3);
					if (!Public_Tools.getIpString(dhcpInfo.ipAddress).startsWith(str)) {
						String[] strArrSplit2 = str.split("\\.");
						iArrIntToArrayIP[0] = Integer.parseInt(strArrSplit2[0]);
						iArrIntToArrayIP[1] = Integer.parseInt(strArrSplit2[1]);
						iArrIntToArrayIP[2] = Integer.parseInt(strArrSplit2[2]);
						iArrIntToArrayIP[3] = 1;
						iArrIntToArrayIP2[0] = iArrIntToArrayIP[0];
						iArrIntToArrayIP2[1] = iArrIntToArrayIP[1];
						iArrIntToArrayIP2[2] = iArrIntToArrayIP[2];
						iArrIntToArrayIP2[3] = 255;
						while (iArrIntToArrayIP[2] <= iArrIntToArrayIP2[2]) {
							while (iArrIntToArrayIP[3] < iArrIntToArrayIP2[3]) {
								datagramPacket.setAddress(InetAddress.getByAddress(Public_Tools.arrayToByteIP(iArrIntToArrayIP)));
								if (Thread.currentThread().isInterrupted()) {
									return;
								}
								this.m_udpSock.send(datagramPacket);
								iArrIntToArrayIP[3] = iArrIntToArrayIP[3] + 1;
							}
							iArrIntToArrayIP[3] = 0;
							iArrIntToArrayIP[2] = iArrIntToArrayIP[2] + 1;
						}
					}
				}
				int i4 = (dhcpInfo.netmask ^ (-1)) | i2;
				iArrIntToArrayIP = Public_Tools.intToArrayIP(i2);
				iArrIntToArrayIP2 = Public_Tools.intToArrayIP(i4);
			}
			while (iArrIntToArrayIP[2] <= iArrIntToArrayIP2[2]) {
				while (iArrIntToArrayIP[3] < iArrIntToArrayIP2[3]) {
					datagramPacket.setAddress(InetAddress.getByAddress(Public_Tools.arrayToByteIP(iArrIntToArrayIP)));
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					this.m_udpSock.send(datagramPacket);
					iArrIntToArrayIP[3] = iArrIntToArrayIP[3] + 1;
				}
				iArrIntToArrayIP[3] = 0;
				iArrIntToArrayIP[2] = iArrIntToArrayIP[2] + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendProPackage(ProPackage proPackage) {
		if (proPackage == null) {
			return;
		}
		try {
			TransPackage transPackage = new TransPackage(proPackage);
			if (0 != (proPackage.nCommandID & 256)) {
				this.m_packageLoseChecker.addCheckPackage(transPackage);
			}
			Log.e("data send", transPackage.toString());
			sendData(transPackage.toString().getBytes("GBK"), transPackage.Addr.getHostAddress(), transPackage.netPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	/* JADX WARN: Type inference failed for: r0v1, types: [com.netfeige.transport.TransMsg$1] */
	public synchronized void sendData(final byte[] bArr, final String str, final int i) {
		if (this.m_udpSock == null) {
			return;
		}
		new Thread() { // from class: com.netfeige.transport.TransMsg.1
			@Override // java.lang.Thread, java.lang.Runnable
			public void run() {
				try {
					DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length);
					datagramPacket.setAddress(InetAddress.getByName(str));
					datagramPacket.setPort(i);
					TransMsg.this.m_udpSock.send(datagramPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override // java.lang.Runnable
	public void run() {
		TransPackage transPackageMakeDataPack;
		ProPackage proPackageDataPackToProPackage;
		byte[] bArr = new byte[65507];
		while (this.m_isRunUdpListener) {
			DatagramPacket datagramPacket = new DatagramPacket(bArr, 65507);
			try {
				this.m_udpSock.receive(datagramPacket);
				if (this.m_protocolNotify != null && (transPackageMakeDataPack = TransPackage.makeDataPack(new String(datagramPacket.getData(), 0, datagramPacket.getLength(), "GBK"), datagramPacket.getAddress(), datagramPacket.getPort())) != null && (proPackageDataPackToProPackage = transPackageMakeDataPack.DataPackToProPackage(ProPackage.PackageType.UDP)) != null) {
					if (Public_Tools.getLowBitCmd(transPackageMakeDataPack.m_nCommandID) == 33) {
						try {
							this.m_packageLoseChecker.removeCheckPackage(Long.parseLong(Public_Tools.getNumber(transPackageMakeDataPack.m_additionalSection)));
						} catch (NumberFormatException unused) {
						}
					} else if (!proPackageDataPackToProPackage.HostInfo.version.equals(Public_MsgID.PRO_COMPATABLE_AZHI) && !this.m_packageRepeatChecker.isRepeatPackage(proPackageDataPackToProPackage.nPackageID)) {
						this.m_protocolNotify.Recv(proPackageDataPackToProPackage);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.m_isRunUdpListener = false;
		this.m_udpListenerThread = null;
	}

	private class PackageLoseChecker implements Runnable {
		private final int m_maxTimeOut = 8000;
		private Thread m_Thread = null;
		private Object m_CheckLock = new Object();
		private Vector<TransPackage> m_vecCheckPackages = new Vector<>();

		public PackageLoseChecker() {
		}

		public synchronized void addCheckPackage(TransPackage transPackage) {
			if (transPackage == null) {
				return;
			}
			if (getCheckPackage(transPackage.m_nPacketID) == null) {
				this.m_vecCheckPackages.add(transPackage);
			}
		}

		private synchronized TransPackage getCheckPackage(long j) {
			for (int i = 0; i < this.m_vecCheckPackages.size(); i++) {
				TransPackage transPackage = this.m_vecCheckPackages.get(i);
				if (transPackage.m_nPacketID == j) {
					return transPackage;
				}
			}
			return null;
		}

		public synchronized void removeCheckPackage(long j) {
			for (int i = 0; i < this.m_vecCheckPackages.size(); i++) {
				if (this.m_vecCheckPackages.get(i).m_nPacketID == j) {
					this.m_vecCheckPackages.remove(i);
				}
			}
		}

		public void start() {
			if (this.m_Thread == null) {
				Thread thread = new Thread(this);
				this.m_Thread = thread;
				thread.start();
			}
		}

		@Override // java.lang.Runnable
		public void run() {
			ProPackage proPackageDataPackToProPackage;
			while (true) {
				try {
					synchronized (this.m_CheckLock) {
						for (int i = 0; i < this.m_vecCheckPackages.size(); i++) {
							TransPackage transPackage = this.m_vecCheckPackages.get(i);
							long jCurrentTimeMillis = System.currentTimeMillis();
							if (jCurrentTimeMillis - transPackage.m_sendtimeStamp > 2666) {
								TransMsg.this.sendData(transPackage.toString().getBytes("GBK"), transPackage.Addr.getHostAddress(), transPackage.netPort);
							}
							if (jCurrentTimeMillis - transPackage.m_sendtimeStamp > 8000 && TransMsg.this.m_protocolNotify != null && (proPackageDataPackToProPackage = transPackage.DataPackToProPackage(ProPackage.PackageType.UDP)) != null) {
								proPackageDataPackToProPackage.Status = Public_Def.TransStatus.Trans_SendFailed;
								if (proPackageDataPackToProPackage.nCommandID == -2147483277) {
									proPackageDataPackToProPackage.nCommandID = -2147483530L;
								}
								try {
									TransMsg.this.m_protocolNotify.Recv(proPackageDataPackToProPackage);
								} catch (Exception unused) {
								}
								if ((proPackageDataPackToProPackage.nCommandID & 32) != 0 && (proPackageDataPackToProPackage.nCommandID & 256) != 0 && (proPackageDataPackToProPackage.nCommandID & 512) != 0) {
									FileTransManager.getInstance().removeRequestingFile(proPackageDataPackToProPackage.HostInfo.strMacAddr, proPackageDataPackToProPackage.nPackageID);
								}
								this.m_vecCheckPackages.remove(transPackage);
							}
						}
					}
					Thread.sleep(1500L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class PackageRepeatChecker {
		private final int m_MaxPackageHistory;
		private Vector<Long> m_packageList;

		private PackageRepeatChecker() {
			this.m_MaxPackageHistory = 10;
			this.m_packageList = new Vector<>();
		}

		private void addPackageRepeatCheck(long j) {
			if (10 == this.m_packageList.size()) {
				this.m_packageList.remove(0);
			}
			Vector<Long> vector = this.m_packageList;
			vector.add(vector.size(), Long.valueOf(j));
		}

		public boolean isRepeatPackage(long j) {
			if (-1 != this.m_packageList.indexOf(Long.valueOf(j))) {
				return true;
			}
			addPackageRepeatCheck(j);
			return false;
		}
	}
}

