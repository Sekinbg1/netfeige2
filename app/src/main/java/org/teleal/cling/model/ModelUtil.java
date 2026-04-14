package org.teleal.cling.model;

import com.netfeige.common.Public_MsgID;
import com.netfeige.dlna.ContentTree;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ModelUtil {
	public static final boolean ANDROID_EMULATOR;
	public static final boolean ANDROID_RUNTIME;

	static {
		boolean z = true;
		boolean z2 = false;
		boolean androidRuntime = false;
		try {
			androidRuntime = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build").getField("ID").get(null) != null;
		} catch (Exception unused) {
		}
		ANDROID_RUNTIME = androidRuntime;
		try {
			String str = (String) Thread.currentThread().getContextClassLoader().loadClass("android.os.Build").getField("PRODUCT").get(null);
			if (!"google_sdk".equals(str)) {
				if (!"sdk".equals(str)) {
					z = false;
				}
			}
			z2 = z;
		} catch (Exception unused) {
		}
		ANDROID_EMULATOR = z2;
	}

	public static boolean isStringConvertibleType(Set<Class> set, Class cls) {
		if (cls.isEnum()) {
			return true;
		}
		Iterator<Class> it = set.iterator();
		while (it.hasNext()) {
			if (it.next().isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidUDAName(String str) {
		return ANDROID_RUNTIME ? (str == null || str.length() == 0) ? false : true : (str == null || str.length() == 0 || str.toLowerCase().startsWith("xml") || !str.matches(Constants.REGEX_UDA_NAME)) ? false : true;
	}

	public static InetAddress getInetAddressByName(String str) {
		try {
			return InetAddress.getByName(str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toCommaSeparatedList(Object[] objArr) {
		return toCommaSeparatedList(objArr, true, false);
	}

	public static String toCommaSeparatedList(Object[] objArr, boolean z, boolean z2) {
		if (objArr == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object obj : objArr) {
			String strReplaceAll = obj.toString().replaceAll("\\\\", "\\\\\\\\");
			if (z) {
				strReplaceAll = strReplaceAll.replaceAll(",", "\\\\,");
			}
			if (z2) {
				strReplaceAll = strReplaceAll.replaceAll("\"", "\\\"");
			}
			sb.append(strReplaceAll);
			sb.append(",");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String[] fromCommaSeparatedList(String str) {
		return fromCommaSeparatedList(str, true);
	}

	public static String[] fromCommaSeparatedList(String str, boolean z) {
		if (str == null || str.length() == 0) {
			return null;
		}
		if (z) {
			str = str.replaceAll("\\\\,", "XXX1122334455XXX");
		}
		String[] strArrSplit = str.split(",");
		for (int i = 0; i < strArrSplit.length; i++) {
			strArrSplit[i] = strArrSplit[i].replaceAll("XXX1122334455XXX", ",");
			strArrSplit[i] = strArrSplit[i].replaceAll("\\\\\\\\", "\\\\");
		}
		return strArrSplit;
	}

	public static String toTimeString(long j) {
		long j2 = j / 3600;
		long j3 = j % 3600;
		long j4 = j3 / 60;
		long j5 = j3 % 60;
		StringBuilder sb = new StringBuilder();
		String str = ContentTree.ROOT_ID;
		sb.append(j2 < 10 ? ContentTree.ROOT_ID : "");
		sb.append(j2);
		sb.append(Public_MsgID.PRO_SPACE);
		sb.append(j4 < 10 ? ContentTree.ROOT_ID : "");
		sb.append(j4);
		sb.append(Public_MsgID.PRO_SPACE);
		if (j5 >= 10) {
			str = "";
		}
		sb.append(str);
		sb.append(j5);
		return sb.toString();
	}

	public static long fromTimeString(String str) {
		String[] strArrSplit = str.split(Public_MsgID.PRO_SPACE);
		return (Long.parseLong(strArrSplit[0]) * 3600) + (Long.parseLong(strArrSplit[1]) * 60) + Long.parseLong(strArrSplit[2]);
	}

	public static String commaToNewline(String str) {
		StringBuilder sb = new StringBuilder();
		for (String str2 : str.split(",")) {
			sb.append(str2);
			sb.append(",");
			sb.append("\n");
		}
		if (sb.length() > 2) {
			sb.deleteCharAt(sb.length() - 2);
		}
		return sb.toString();
	}

	public static String getLocalHostName(boolean z) {
		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			return (z || hostName.indexOf(".") == -1) ? hostName : hostName.substring(0, hostName.indexOf("."));
		} catch (Exception unused) {
			return "UNKNOWN HOST";
		}
	}

	public static byte[] getFirstNetworkInterfaceHardwareAddress() {
		try {
			for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (!networkInterface.isLoopback() && networkInterface.isUp() && networkInterface.getHardwareAddress() != null) {
					return networkInterface.getHardwareAddress();
				}
			}
			throw new RuntimeException("Could not discover first network interface hardware address");
		} catch (Exception unused) {
			throw new RuntimeException("Could not discover first network interface hardware address");
		}
	}
}
