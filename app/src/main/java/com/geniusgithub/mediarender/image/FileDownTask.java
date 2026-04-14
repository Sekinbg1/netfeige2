package com.geniusgithub.mediarender.image;

import com.geniusgithub.mediarender.image.DownLoadHelper;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.FileHelper;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.common.Public_MsgID;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class FileDownTask implements Runnable {
	private static final int CONNECT_TIME_OUT = 5000;
	private static final int MAX_REQUEST_COUNT = 3;
	private static final CommonLog log = LogFactory.createLog();
	public DownLoadHelper.IDownLoadCallback callback;
	public String requestUrl;
	public String saveUri;
	public String requesetMethod = "GET";
	public int responsCode = 0;
	public boolean isDownloadSuccess = false;

	public FileDownTask(String str, String str2, DownLoadHelper.IDownLoadCallback iDownLoadCallback) {
		this.requestUrl = str;
		this.saveUri = str2;
		this.callback = iDownLoadCallback;
	}

	@Override // java.lang.Runnable
	public void run() {
		if (isParamValid()) {
			int i = 0;
			while (!request() && i <= 2) {
				i++;
				log.e("request fail,cur count = " + i);
			}
		} else {
			log.e("isParamValid = false!!!");
		}
		DownLoadHelper.IDownLoadCallback iDownLoadCallback = this.callback;
		if (iDownLoadCallback != null) {
			iDownLoadCallback.downLoadResult(this.isDownloadSuccess, this.saveUri);
		}
	}

	private boolean request() {
		try {
			this.requestUrl = urlEncoder(this.requestUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.requestUrl).openConnection();
			httpURLConnection.setRequestMethod(this.requesetMethod);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
			int responseCode = httpURLConnection.getResponseCode();
			this.responsCode = responseCode;
			if (responseCode != 200) {
				log.e("responsCode = " + this.responsCode + ", so Fail!!!");
				return false;
			}
			InputStream inputStream = httpURLConnection.getInputStream();
			try {
				this.isDownloadSuccess = FileHelper.writeFile(this.saveUri, inputStream);
			} catch (Throwable e) {
				e.printStackTrace();
				log.e("catch Throwable e = " + e.getMessage());
				this.isDownloadSuccess = false;
			}
			inputStream.close();
			return this.isDownloadSuccess;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.e("catch MalformedURLException e = " + e.getMessage());
			return false;
		} catch (IOException e2) {
			e2.printStackTrace();
			log.e("catch IOException e = " + e2.getMessage() + ", inputStream = " + ((Object) null));
			return false;
		}
	}

	private String urlEncoder(String str) throws UnsupportedEncodingException {
		if (str == null || str.equals("")) {
			return null;
		}
		String[] strArrSplit = str.split(Public_MsgID.PRO_SPACE);
		int length = strArrSplit.length;
		if (length == 3) {
			return strArrSplit[0] + Public_MsgID.PRO_SPACE + strArrSplit[1] + Public_MsgID.PRO_SPACE + splitBias(strArrSplit[2]);
		}
		if (length != 2) {
			return "";
		}
		return strArrSplit[0] + Public_MsgID.PRO_SPACE + splitBias(strArrSplit[1]);
	}

	private String splitBias(String str) throws UnsupportedEncodingException {
		String str2 = "";
		for (String str3 : str.split(ServiceReference.DELIMITER)) {
			str2 = str2 + URLEncoder.encode(str3, "UTF-8") + ServiceReference.DELIMITER;
		}
		return str2;
	}

	public boolean isParamValid() {
		return (this.requestUrl == null || this.saveUri == null) ? false : true;
	}
}
