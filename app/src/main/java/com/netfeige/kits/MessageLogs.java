package com.netfeige.kits;

import android.content.Context;
import com.netfeige.common.DBHelper;
import com.netfeige.kits.IMessageLogs;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MessageLogs implements IMessageLogs {
	private static Context m_context;
	private static IMessageLogs m_messageLogs;
	private DBHelper m_dbHelper;

	private MessageLogs(Context context) {
		this.m_dbHelper = null;
		m_context = context;
		this.m_dbHelper = DBHelper.getInstance(context);
	}

	public static IMessageLogs getInstance(Context context) {
		if (context == null) {
			return null;
		}
		if (m_messageLogs == null) {
			m_messageLogs = new MessageLogs(context);
		}
		return m_messageLogs;
	}

	@Override // com.netfeige.kits.IMessageLogs
	public List<IMessageLogs.MESSAGELOGITEM> readFirstMessageLog(String str, int i) {
		return this.m_dbHelper.getFirstMessageLog(str, i);
	}

	@Override // com.netfeige.kits.IMessageLogs
	public int readPageCount() {
		return this.m_dbHelper.getPageCount();
	}

	@Override // com.netfeige.kits.IMessageLogs
	public int readCurrentPage() {
		return this.m_dbHelper.getCurrentPage();
	}

	@Override // com.netfeige.kits.IMessageLogs
	public List<IMessageLogs.MESSAGELOGITEM> readForwardMessageLog() {
		return this.m_dbHelper.getForwardMessageLog();
	}

	@Override // com.netfeige.kits.IMessageLogs
	public List<IMessageLogs.MESSAGELOGITEM> readBackMessageLog() {
		try {
			return this.m_dbHelper.getBackMessageLog();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override // com.netfeige.kits.IMessageLogs
	public boolean writeMessageLog(IMessageLogs.MESSAGELOGITEM messagelogitem) {
		int i;
		String str = messagelogitem.uIpAddr;
		int i2 = messagelogitem.uTime;
		IMessageLogs.MESSAGELOGTYPE messagelogtype = messagelogitem.LogType;
		if (messagelogtype == IMessageLogs.MESSAGELOGTYPE.TYPE_TEXT) {
			i = 0;
		} else {
			i = messagelogtype == IMessageLogs.MESSAGELOGTYPE.TYPE_FILE ? 1 : -1;
		}
		return this.m_dbHelper.insert(str, i2, i, messagelogitem.pszContent);
	}
}
