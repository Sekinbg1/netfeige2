package com.netfeige.kits;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.service.IpmsgService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
	private static final String BOUNDARY = "---------------------------7db1c523809b2";
	private static CrashHandler m_CrashHandler;
	private Context m_Context;
	private SimpleDateFormat m_timeFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	private boolean m_bflag = false;

	private CrashHandler() {
	}

	public static synchronized CrashHandler getInstance() {
		if (m_CrashHandler != null) {
			return m_CrashHandler;
		}
		CrashHandler crashHandler = new CrashHandler();
		m_CrashHandler = crashHandler;
		return crashHandler;
	}

	public void init(Context context) {
		this.m_Context = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override // java.lang.Thread.UncaughtExceptionHandler
	public void uncaughtException(Thread thread, Throwable th) {
		if (this.m_bflag) {
			String str = this.m_timeFormat.format(Long.valueOf(System.currentTimeMillis())) + "\n";
			String str2 = "\nVersion Info: \n" + getVersionInfo();
			String str3 = "\nPhone Info: \n" + getMobileInfo();
			String str4 = "\nCrash Info: \n" + getErrorInfo(th);
			String str5 = "crash_" + Public_Tools.getLocalHostName() + "_" + System.currentTimeMillis() + ".txt";
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(new File(Public_Tools.getDefaultDownloadPath(), str5));
				fileOutputStream.write((str + str2 + str3 + str4).getBytes());
				fileOutputStream.flush();
				fileOutputStream.close();
				uploadHttpURLConnection("", "", Public_Tools.getDefaultDownloadPath() + ServiceReference.DELIMITER + str5);
			} catch (Throwable e) {
				e.printStackTrace();
				Intent intent = new Intent(this.m_Context, (Class<?>) IpmsgService.class);
				Context context = this.m_Context;
				((IpmsgApplication) context).unbindService(((IpmsgApplication) context)._connection);
				((IpmsgApplication) this.m_Context).stopService(intent);
				IpmsgActivity.updateSqlite();
// Umeng removed:                 // Umeng removed: MobclickAgent.onKillProcess(this.m_Context);
				((ActivityManager) ((IpmsgApplication) this.m_Context).getSystemService("activity")).restartPackage(((IpmsgApplication) this.m_Context).getPackageName());
				Process.killProcess(Process.myPid());
				System.exit(10);
				return;
			}
		}
		Intent intent2 = new Intent(this.m_Context, (Class<?>) IpmsgService.class);
		Context context2 = this.m_Context;
		((IpmsgApplication) context2).unbindService(((IpmsgApplication) context2)._connection);
		((IpmsgApplication) this.m_Context).stopService(intent2);
		Intent intent3 = new Intent("cn.com.feige");
		intent3.putExtra("stop", 1);
		this.m_Context.sendBroadcast(intent3);
		IpmsgActivity.updateSqlite();
// Umeng removed:         // Umeng removed: MobclickAgent.onKillProcess(this.m_Context);
		((ActivityManager) ((IpmsgApplication) this.m_Context).getSystemService("activity")).restartPackage(((IpmsgApplication) this.m_Context).getPackageName());
		Process.killProcess(Process.myPid());
		System.exit(10);
	}

	private String getErrorInfo(Throwable th) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		th.printStackTrace(printWriter);
		th.printStackTrace();
		printWriter.close();
		return stringWriter.toString();
	}

	private String getMobileInfo() {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			for (Field field : Build.class.getDeclaredFields()) {
				field.setAccessible(true);
				stringBuffer.append(field.getName() + "=" + field.get(null).toString());
				stringBuffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}

	private String getVersionInfo() {
		return Public_Tools.getVersion() + "\n";
	}

	public boolean uploadHttpURLConnection(String str, String str2, String str3) throws Exception {
		File file = new File(str3);
		StringBuilder sb = new StringBuilder();
		sb.append("-----------------------------7db1c523809b2\r\n");
		sb.append("Content-Disposition: form-data; name=\"username\"\r\n");
		sb.append("\r\n");
		sb.append(str + "\r\n");
		sb.append("-----------------------------7db1c523809b2\r\n");
		sb.append("Content-Disposition: form-data; name=\"password\"\r\n");
		sb.append("\r\n");
		sb.append(str2 + "\r\n");
		sb.append("-----------------------------7db1c523809b2\r\n");
		sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + str3 + "\"\r\n");
		sb.append("Content-Type: image/pjpeg\r\n");
		sb.append("\r\n");
		byte[] bytes = sb.toString().getBytes("UTF-8");
		byte[] bytes2 = "\r\n-----------------------------7db1c523809b2--\r\n".getBytes("UTF-8");
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://192.168.0.111/Setup/crash_log").openConnection();
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------7db1c523809b2");
		httpURLConnection.setRequestProperty("Content-Length", String.valueOf(((long) bytes.length) + file.length() + ((long) bytes2.length)));
		httpURLConnection.setRequestProperty("HOST", "192.168.0.111");
		httpURLConnection.setDoOutput(true);
		OutputStream outputStream = httpURLConnection.getOutputStream();
		FileInputStream fileInputStream = new FileInputStream(file);
		outputStream.write(bytes);
		byte[] bArr = new byte[1024];
		while (true) {
			int i = fileInputStream.read(bArr);
			if (i == -1) {
				break;
			}
			outputStream.write(bArr, 0, i);
		}
		outputStream.write(bytes2);
		fileInputStream.close();
		outputStream.close();
		return httpURLConnection.getResponseCode() == 200;
	}
}
