package com.netfeige.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.netfeige.kits.IMessageLogs;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper m_dbHelper = null;
    private static final int m_nDataBaseVersion = 4;
    private static final String m_strDataBaseName = "IPMsg.db";
    private int m_nCurrentPage;
    private int m_nNumberEachPage;
    private final String m_strCreateDisExeStatusTable;
    private final String m_strCreateDiscussInfoTable;
    private final String m_strCreateHistoryFilesTable;
    private final String m_strCreateShareFilesTable;
    private final String m_strCreateSharePasswordTable;
    private final String m_strCreateTable;
    private final String m_strCreateTable2;
    private final String m_strDataBaseTable;
    private final String m_strDataBaseTable2;
    private final String m_strDiscussExeStatus;
    private final String m_strDiscussInfo;
    private final String m_strHistoryFiles;
    private String m_strIpAdress;
    private final String m_strShareFiles;
    private final String m_strSharePassword;

    public String getM_strDiscussExeStatus() {
        return "DiscussExeStatus";
    }

    public String getM_strDiscussInfo() {
        return "DiscussInfo";
    }

    private DBHelper(Context context) {
        super(context, m_strDataBaseName, (SQLiteDatabase.CursorFactory) null, 4);
        this.m_strDataBaseTable = "MessageLog";
        this.m_strDataBaseTable2 = "FolderPath";
        this.m_strHistoryFiles = "HistoryFiles";
        this.m_strShareFiles = "ShareFiles";
        this.m_strSharePassword = "SharePassword";
        this.m_strDiscussInfo = "DiscussInfo";
        this.m_strDiscussExeStatus = "DiscussExeStatus";
        this.m_strCreateTable = "create table if not exists MessageLog(UIpAddr text not null ,UTime integer,LogType integer,PszContent text)";
        this.m_strCreateTable2 = "create table if not exists FolderPath(Category text not null,Path text)";
        this.m_strCreateHistoryFilesTable = "create table if not exists HistoryFiles(ID INTEGER PRIMARY KEY AUTOINCREMENT,sMac TEXT,DiscussID TEXT,TransStatus INTEGER,Time INTEGER,Type INTEGER,Size INTEGER,FileName TEXT,FileFullPath TEXT)";
        this.m_strCreateShareFilesTable = "create table if not exists ShareFiles(ID INTEGER PRIMARY KEY AUTOINCREMENT,Time INTEGER,Type INTEGER,Size INTEGER,Name TEXT,Path TEXT UNIQUE,MAClist TEXT)";
        this.m_strCreateSharePasswordTable = "create table if not exists SharePassword(ID INTEGER PRIMARY KEY AUTOINCREMENT,sMac TEXT UNIQUE,Password TEXT)";
        this.m_strCreateDiscussInfoTable = "create table if not exists DiscussInfo(ID INTEGER PRIMARY KEY AUTOINCREMENT,DiscussID TEXT UNIQUE,Name TEXT,Author TEXT,MemList TEXT,CreateTime INTEGER,EndTime INTEGER,Exit INTEGER)";
        this.m_strCreateDisExeStatusTable = "create table if not exists DiscussExeStatus(ID INTEGER PRIMARY KEY AUTOINCREMENT,DiscussID TEXT,DestMac TEXT,RecvMac TEXT,ExeTime INTEGER,IsJoin INTEGER,IsNotified INTEGER)";
        this.m_nNumberEachPage = 0;
        this.m_nCurrentPage = 1;
        this.m_strIpAdress = null;
    }

    public static DBHelper getInstance(Context context) {
        if (m_dbHelper == null) {
            m_dbHelper = new DBHelper(context);
        }
        return m_dbHelper;
    }

    public static DBHelper getInstance() {
        return m_dbHelper;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table if not exists MessageLog(UIpAddr text not null ,UTime integer,LogType integer,PszContent text)");
        sQLiteDatabase.execSQL("create table if not exists FolderPath(Category text not null,Path text)");
        sQLiteDatabase.execSQL("create table if not exists HistoryFiles(ID INTEGER PRIMARY KEY AUTOINCREMENT,sMac TEXT,DiscussID TEXT,TransStatus INTEGER,Time INTEGER,Type INTEGER,Size INTEGER,FileName TEXT,FileFullPath TEXT)");
        sQLiteDatabase.execSQL("create table if not exists ShareFiles(ID INTEGER PRIMARY KEY AUTOINCREMENT,Time INTEGER,Type INTEGER,Size INTEGER,Name TEXT,Path TEXT UNIQUE,MAClist TEXT)");
        sQLiteDatabase.execSQL("create table if not exists SharePassword(ID INTEGER PRIMARY KEY AUTOINCREMENT,sMac TEXT UNIQUE,Password TEXT)");
        sQLiteDatabase.execSQL("create table if not exists DiscussInfo(ID INTEGER PRIMARY KEY AUTOINCREMENT,DiscussID TEXT UNIQUE,Name TEXT,Author TEXT,MemList TEXT,CreateTime INTEGER,EndTime INTEGER,Exit INTEGER)");
        sQLiteDatabase.execSQL("create table if not exists DiscussExeStatus(ID INTEGER PRIMARY KEY AUTOINCREMENT,DiscussID TEXT,DestMac TEXT,RecvMac TEXT,ExeTime INTEGER,IsJoin INTEGER,IsNotified INTEGER)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table if exists MessageLog");
        sQLiteDatabase.execSQL("drop table if exists FolderPath");
        sQLiteDatabase.execSQL("drop table if exists HistoryFiles");
        sQLiteDatabase.execSQL("drop table if exists ShareFiles");
        sQLiteDatabase.execSQL("drop table if exists SharePassword");
        sQLiteDatabase.execSQL("drop table if exists DiscussInfo");
        sQLiteDatabase.execSQL("drop table if exists DiscussExeStatus");
        onCreate(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table if exists MessageLog");
        sQLiteDatabase.execSQL("drop table if exists FolderPath");
        sQLiteDatabase.execSQL("drop table if exists HistoryFiles");
        sQLiteDatabase.execSQL("drop table if exists ShareFiles");
        sQLiteDatabase.execSQL("drop table if exists SharePassword");
        sQLiteDatabase.execSQL("drop table if exists DiscussInfo");
        sQLiteDatabase.execSQL("drop table if exists DiscussExeStatus");
        onCreate(sQLiteDatabase);
    }

    public List<IMessageLogs.MESSAGELOGITEM> getAllMsgLogItem() {
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("MessageLog", null, null, null, null, null, null);
        List<IMessageLogs.MESSAGELOGITEM> msgLogItemsByCursor = getMsgLogItemsByCursor(cursorQuery);
        cursorQuery.close();
        return msgLogItemsByCursor;
    }

    public ArrayList<FolderInfo> getAllFolderInfo() {
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("FolderPath", null, null, null, null, null, null);
        ArrayList<FolderInfo> folderInfoByCursor = getFolderInfoByCursor(cursorQuery);
        cursorQuery.close();
        return folderInfoByCursor;
    }

    public List<IMessageLogs.MESSAGELOGITEM> getFirstMessageLog(String str, int i) {
        synchronized (m_strDataBaseName) {
        }
        this.m_nNumberEachPage = i;
        this.m_strIpAdress = str;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList arrayList = new ArrayList();
        Cursor cursorRawQuery = readableDatabase.rawQuery("select * from MessageLog where UIpAddr=? order by UTime desc", new String[]{String.valueOf(str)});
        int i2 = 0;
        while (cursorRawQuery.moveToNext()) {
            i2++;
            if (i2 <= i) {
                String string = cursorRawQuery.getString(0);
                int i3 = cursorRawQuery.getInt(1);
                int i4 = cursorRawQuery.getInt(2);
                IMessageLogs.MESSAGELOGTYPE messagelogtype = null;
                if (i4 == 0) {
                    messagelogtype = IMessageLogs.MESSAGELOGTYPE.TYPE_TEXT;
                } else if (1 == i4) {
                    messagelogtype = IMessageLogs.MESSAGELOGTYPE.TYPE_FILE;
                }
                arrayList.add(new IMessageLogs.MESSAGELOGITEM(string, i3, messagelogtype, cursorRawQuery.getString(3)));
            }
        }
        cursorRawQuery.close();
        return arrayList;
    }

    public List<IMessageLogs.MESSAGELOGITEM> getForwardMessageLog() {
        IMessageLogs.MESSAGELOGTYPE messagelogtype;
        synchronized (m_strDataBaseName) {
        }
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        if ((this.m_nCurrentPage > 1) & (this.m_nNumberEachPage > 0)) {
            Cursor cursorRawQuery = readableDatabase.rawQuery("select * from MessageLog where UIpAddr='" + this.m_strIpAdress + "' order by UTime desc Limit " + this.m_nNumberEachPage + " offset " + ((this.m_nCurrentPage - 1) * this.m_nNumberEachPage), null);
            while (cursorRawQuery.moveToNext()) {
                String string = cursorRawQuery.getString(0);
                int i = cursorRawQuery.getInt(1);
                int i2 = cursorRawQuery.getInt(2);
                if (i2 == 0) {
                    messagelogtype = IMessageLogs.MESSAGELOGTYPE.TYPE_TEXT;
                } else {
                    messagelogtype = 1 == i2 ? IMessageLogs.MESSAGELOGTYPE.TYPE_FILE : null;
                }
                arrayList.add(new IMessageLogs.MESSAGELOGITEM(string, i, messagelogtype, cursorRawQuery.getString(3)));
            }
            cursorRawQuery.close();
        }
        this.m_nCurrentPage--;
        return arrayList;
    }

    public List<IMessageLogs.MESSAGELOGITEM> getBackMessageLog() throws Throwable {
        IMessageLogs.MESSAGELOGTYPE messagelogtype;
        ArrayList arrayList = new ArrayList();
        int pageCount = getPageCount();
        int recordCount = getRecordCount();
        if ((this.m_nCurrentPage < pageCount) & (this.m_nNumberEachPage > 0)) {
            int i = this.m_nCurrentPage + 1;
            int i2 = this.m_nNumberEachPage;
            if (recordCount - (i * i2) < 0) {
                i2 = recordCount - ((pageCount - 1) * i2);
            }
            int i3 = this.m_nCurrentPage * this.m_nNumberEachPage;
            Cursor cursor = null;
            try {
                synchronized (m_strDataBaseName) {
                }
                Cursor cursorRawQuery = getReadableDatabase().rawQuery("select * from MessageLog where UIpAddr = '" + this.m_strIpAdress + "' order by UTime desc Limit " + i2 + " offset " + i3, null);
                while (cursorRawQuery.moveToNext()) {
                    try {
                        String string = cursorRawQuery.getString(0);
                        int i4 = cursorRawQuery.getInt(1);
                        int i5 = cursorRawQuery.getInt(2);
                        if (i5 == 0) {
                            messagelogtype = IMessageLogs.MESSAGELOGTYPE.TYPE_TEXT;
                        } else {
                            messagelogtype = 1 == i5 ? IMessageLogs.MESSAGELOGTYPE.TYPE_FILE : null;
                        }
                        arrayList.add(new IMessageLogs.MESSAGELOGITEM(string, i4, messagelogtype, cursorRawQuery.getString(3)));
                    } catch (Exception unused) {
                        Cursor cursor = null;
                        cursor = cursorRawQuery;
                        cursor.close();
                    } catch (Throwable th) {
                        cursor = cursorRawQuery;
                        cursor.close();
                        throw th;
                    }
                }
                cursorRawQuery.close();
            } catch (Exception unused2) {
            } catch (Throwable th2) {
            }
        }
        this.m_nCurrentPage++;
        return arrayList;
    }

    public int getRecordCount() {
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("MessageLog", null, null, null, null, null, null);
        int count = cursorQuery.getCount();
        cursorQuery.close();
        return count;
    }

    public int getPageCount() {
        int recordCount = getRecordCount();
        int i = this.m_nNumberEachPage;
        if (i > 0) {
            return ((recordCount + i) - 1) / i;
        }
        return 0;
    }

    public int getCurrentPage() {
        return this.m_nCurrentPage;
    }

    public void delete(String str) {
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            writableDatabase.beginTransaction();
            try {
                writableDatabase.delete("MessageLog", "UIpAddr = '" + str + "'", null);
                writableDatabase.setTransactionSuccessful();
            } catch (Exception unused) {
            } catch (Throwable th) {
                writableDatabase.endTransaction();
                throw th;
            }
            writableDatabase.endTransaction();
        }
    }

    public void deleteAllTableData(String str) {
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            writableDatabase.beginTransaction();
            try {
                writableDatabase.delete(str, null, null);
                writableDatabase.setTransactionSuccessful();
            } catch (Exception unused) {
            } catch (Throwable th) {
                writableDatabase.endTransaction();
                throw th;
            }
            writableDatabase.endTransaction();
        }
    }

    public boolean insert(String str, int i, int i2, String str2) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("UIpAddr", str);
            contentValues.put("UTime", Integer.valueOf(i));
            contentValues.put("LogType", Integer.valueOf(i2));
            contentValues.put("PszContent", str2);
            writableDatabase.beginTransaction();
            try {
                writableDatabase.insert("MessageLog", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
                z = true;
            } catch (Exception unused) {
                writableDatabase.endTransaction();
            } catch (Throwable th) {
                writableDatabase.endTransaction();
                throw th;
            }
        }
        return z;
    }

    public boolean insertFolderPath(String str, String str2) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Category", str);
            contentValues.put("Path", str2);
            writableDatabase.beginTransaction();
            try {
                try {
                    writableDatabase.insert("FolderPath", null, contentValues);
                    writableDatabase.setTransactionSuccessful();
                    writableDatabase.endTransaction();
                    z = true;
                } finally {
                    writableDatabase.endTransaction();
                }
            } catch (Exception unused) {
            }
        }
        return z;
    }

    private List<IMessageLogs.MESSAGELOGITEM> getMsgLogItemsByCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String string = cursor.getString(0);
            int i = cursor.getInt(1);
            int i2 = cursor.getInt(2);
            IMessageLogs.MESSAGELOGTYPE messagelogtype = null;
            if (i2 == 0) {
                messagelogtype = IMessageLogs.MESSAGELOGTYPE.TYPE_TEXT;
            } else if (1 == i2) {
                messagelogtype = IMessageLogs.MESSAGELOGTYPE.TYPE_FILE;
            }
            arrayList.add(new IMessageLogs.MESSAGELOGITEM(string, i, messagelogtype, cursor.getString(3)));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    private ArrayList<FolderInfo> getFolderInfoByCursor(Cursor cursor) {
        ArrayList<FolderInfo> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(new FolderInfo(cursor.getString(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<HistoryFiles> getHistoryFilesRecord(String str) {
        ArrayList<HistoryFiles> arrayList = new ArrayList<>();
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("HistoryFiles", null, str, null, null, null, null);
        if (cursorQuery != null && cursorQuery.getCount() > 0) {
            cursorQuery.moveToFirst();
            while (!cursorQuery.isAfterLast()) {
                arrayList.add(new HistoryFiles(cursorQuery.getInt(0), cursorQuery.getString(1), cursorQuery.getString(2), cursorQuery.getInt(3), cursorQuery.getLong(4), cursorQuery.getInt(5), cursorQuery.getLong(6), cursorQuery.getString(7), cursorQuery.getString(8)));
                cursorQuery.moveToNext();
            }
        }
        cursorQuery.close();
        return arrayList;
    }

    public void deleteHistoryFilesRecord(String str) {
        SQLiteDatabase writableDatabase;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
            } catch (Throwable th) {
            }
            try {
                writableDatabase.beginTransaction();
                writableDatabase.delete("HistoryFiles", str, null);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Throwable th2) {
                sQLiteDatabase = writableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
                throw th;
            }
        }
    }

    public boolean insertHistoryFilesRecord(String str) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
                writableDatabase.beginTransaction();
                writableDatabase.execSQL(str);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                z = true;
            } catch (Exception unused) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Throwable th) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean updateHistoryFilesRecord(String str) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
                writableDatabase.beginTransaction();
                writableDatabase.execSQL(str);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                z = true;
            } catch (Exception unused) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Throwable th) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean updateHistoryFilesRecord(HistoryFiles historyFiles, HistoryFiles historyFiles2) {
        boolean z;
        ContentValues contentValues = new ContentValues();
        if (historyFiles2.getM_iID() != -1) {
            contentValues.put("ID", Integer.valueOf(historyFiles2.getM_iID()));
        }
        if (historyFiles2.getM_strMac() != null) {
            contentValues.put("sMac", historyFiles2.getM_strMac());
        }
        if (historyFiles2.getM_strDiscussID() != null) {
            contentValues.put("DiscussID", historyFiles2.getM_strDiscussID());
        }
        if (historyFiles2.getM_iTransStatus() != -1) {
            contentValues.put("TransStatus", Integer.valueOf(historyFiles2.getM_iTransStatus()));
        }
        if (historyFiles2.getM_lTime() != -1) {
            contentValues.put("Time", Long.valueOf(historyFiles2.getM_lTime()));
        }
        if (historyFiles2.getM_iType() != -1) {
            contentValues.put("Type", Integer.valueOf(historyFiles2.getM_iType()));
        }
        if (historyFiles2.getM_lSize() != -1) {
            contentValues.put("Size", Long.valueOf(historyFiles2.getM_lSize()));
        }
        if (historyFiles2.getM_strFileName() != null) {
            contentValues.put("FileName", historyFiles2.getM_strFileName());
        }
        if (historyFiles2.getM_strFileFullPath() != null) {
            contentValues.put("FileFullPath", historyFiles2.getM_strFileFullPath());
        }
        String str = "1=1";
        if (historyFiles.getM_iID() != -1) {
            str = "1=1 and ID = " + historyFiles.getM_iID();
        }
        if (historyFiles.getM_strMac() != null) {
            str = str + " and sMac = '" + historyFiles.getM_strMac() + "'";
        }
        if (historyFiles.getM_strDiscussID() != null) {
            str = str + " and DiscussID = '" + historyFiles.getM_strDiscussID() + "'";
        }
        if (historyFiles.getM_iTransStatus() != -1) {
            str = str + " and TransStatus = " + historyFiles.getM_iTransStatus();
        }
        if (historyFiles.getM_lTime() != -1) {
            str = str + " and Time = " + historyFiles.getM_lTime();
        }
        if (historyFiles.getM_iType() != -1) {
            str = str + " and Type = " + historyFiles.getM_iType();
        }
        if (historyFiles.getM_lSize() != -1) {
            str = str + " and Size = " + historyFiles.getM_lSize();
        }
        if (historyFiles.getM_strFileName() != null) {
            str = str + " and FileName = '" + historyFiles.getM_strFileName() + "'";
        }
        if (historyFiles.getM_strFileFullPath() != null) {
            str = str + " and FileFullPath = '" + historyFiles.getM_strFileFullPath() + "'";
        }
        synchronized (m_strDataBaseName) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                SQLiteDatabase writableDatabase = getWritableDatabase();
                try {
                    writableDatabase.beginTransaction();
                    int iUpdate = writableDatabase.update("HistoryFiles", contentValues, str, null);
                    writableDatabase.setTransactionSuccessful();
                    z = iUpdate > 0;
                    if (writableDatabase != null) {
                        writableDatabase.endTransaction();
                    }
                } catch (Exception unused) {
                    SQLiteDatabase sQLiteDatabase = null;
                    sQLiteDatabase = writableDatabase;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.endTransaction();
                    }
                } catch (Throwable th) {
                    sQLiteDatabase = writableDatabase;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.endTransaction();
                    }
                    throw th;
                }
            } catch (Exception unused2) {
            } catch (Throwable th2) {
            }
        }
        return z;
    }

    public ArrayList<ShareFiles> getShareFilesRecord(String str, String[] strArr) {
        ArrayList<ShareFiles> arrayList = new ArrayList<>();
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("ShareFiles", null, str, strArr, null, null, null);
        if (cursorQuery != null && cursorQuery.getCount() > 0) {
            cursorQuery.moveToFirst();
            while (!cursorQuery.isAfterLast()) {
                arrayList.add(new ShareFiles(cursorQuery.getInt(0), cursorQuery.getLong(1), cursorQuery.getInt(2), cursorQuery.getLong(3), cursorQuery.getString(4), cursorQuery.getString(5), cursorQuery.getString(6)));
                cursorQuery.moveToNext();
            }
        }
        cursorQuery.close();
        return arrayList;
    }

    public boolean insertShareFilesRecord(ArrayList<ShareFiles> arrayList) {
        boolean z;
        SQLiteDatabase writableDatabase;
        synchronized (m_strDataBaseName) {
            z = false;
            SQLiteDatabase sQLiteDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
            } catch (Exception unused) {
            } catch (Throwable th) {
            }
            try {
                writableDatabase.beginTransaction();
                for (int i = 0; i < arrayList.size(); i++) {
                    ShareFiles shareFiles = arrayList.get(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Time", Long.valueOf(shareFiles.getM_lTime()));
                    contentValues.put("Type", Integer.valueOf(shareFiles.getM_iType()));
                    contentValues.put("Size", Long.valueOf(shareFiles.getM_lSize()));
                    contentValues.put("Name", shareFiles.getM_strName());
                    contentValues.put("Path", shareFiles.getM_strPath());
                    contentValues.put("MAClist", shareFiles.getM_strMACList());
                    writableDatabase.insert("ShareFiles", null, contentValues);
                }
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                z = true;
            } catch (Exception unused2) {
                SQLiteDatabase sQLiteDatabase = null;
                sQLiteDatabase = writableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
            } catch (Throwable th2) {
                sQLiteDatabase = writableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean updateShareFilesRecord(ContentValues contentValues, String str, String[] strArr) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
                writableDatabase.beginTransaction();
                int iUpdate = writableDatabase.update("ShareFiles", contentValues, str, strArr);
                writableDatabase.setTransactionSuccessful();
                z = iUpdate > 0;
            } catch (Exception unused) {
                if (writableDatabase != null) {
                }
            } catch (Throwable th) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
            if (writableDatabase != null) {
                writableDatabase.endTransaction();
            }
        }
        return z;
    }

    public void deleteShareFilesRecord(String str, String[] strArr) {
        SQLiteDatabase writableDatabase;
        synchronized (m_strDataBaseName) {
            try {
                writableDatabase = getWritableDatabase();
            } catch (Throwable th) {
                writableDatabase = null;
            }
            try {
                writableDatabase.beginTransaction();
                writableDatabase.delete("ShareFiles", str, strArr);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Throwable th2) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
        }
    }

    public ArrayList<SharePassword> getSharePasswordRecord(String str) {
        ArrayList<SharePassword> arrayList = new ArrayList<>();
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("SharePassword", null, str, null, null, null, null);
        if (cursorQuery != null && cursorQuery.getCount() > 0) {
            cursorQuery.moveToFirst();
            while (!cursorQuery.isAfterLast()) {
                arrayList.add(new SharePassword(cursorQuery.getInt(0), cursorQuery.getString(1), cursorQuery.getString(2)));
                cursorQuery.moveToNext();
            }
        }
        cursorQuery.close();
        return arrayList;
    }

    public boolean insertSharePasswordRecord(SharePassword sharePassword) {
        boolean z;
        SQLiteDatabase writableDatabase;
        ContentValues contentValues = new ContentValues();
        contentValues.put("sMac", sharePassword.getM_strMac());
        contentValues.put("Password", sharePassword.getM_strPassword());
        synchronized (m_strDataBaseName) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
            } catch (Exception unused) {
            } catch (Throwable th) {
            }
            try {
                writableDatabase.beginTransaction();
                writableDatabase.insert("SharePassword", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                z = true;
            } catch (Exception unused2) {
                SQLiteDatabase sQLiteDatabase = null;
                sQLiteDatabase = writableDatabase;
                z = false;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
            } catch (Throwable th2) {
                sQLiteDatabase = writableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean updateSharePasswordRecord(SharePassword sharePassword) {
        boolean z;
        SQLiteDatabase writableDatabase;
        ContentValues contentValues = new ContentValues();
        contentValues.put("Password", sharePassword.getM_strPassword());
        synchronized (m_strDataBaseName) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
            } catch (Exception unused) {
            } catch (Throwable th) {
            }
            try {
                writableDatabase.beginTransaction();
                int iUpdate = writableDatabase.update("SharePassword", contentValues, "sMac = '" + sharePassword.getM_strMac() + "'", null);
                writableDatabase.setTransactionSuccessful();
                z = iUpdate > 0;
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Exception unused2) {
                SQLiteDatabase sQLiteDatabase = null;
                sQLiteDatabase = writableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
            } catch (Throwable th2) {
                sQLiteDatabase = writableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean insertDataRecord(String str, String str2, ContentValues contentValues) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
                writableDatabase.beginTransaction();
                writableDatabase.insert(str, str2, contentValues);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                z = true;
            } catch (Exception unused) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Throwable th) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean updateDataRecord(String str, ContentValues contentValues, String str2, String[] strArr) {
        boolean z;
        synchronized (m_strDataBaseName) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = getWritableDatabase();
                writableDatabase.beginTransaction();
                int iUpdate = writableDatabase.update(str, contentValues, str2, strArr);
                writableDatabase.setTransactionSuccessful();
                z = iUpdate > 0;
            } catch (Exception unused) {
                if (writableDatabase != null) {
                }
            } catch (Throwable th) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
            if (writableDatabase != null) {
                writableDatabase.endTransaction();
            }
        }
        return z;
    }

    public void deleteDataRecord(String str, String str2, String[] strArr) {
        SQLiteDatabase writableDatabase;
        synchronized (m_strDataBaseName) {
            try {
                writableDatabase = getWritableDatabase();
            } catch (Throwable th) {
                writableDatabase = null;
            }
            try {
                writableDatabase.beginTransaction();
                writableDatabase.delete(str, str2, strArr);
                writableDatabase.setTransactionSuccessful();
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
            } catch (Throwable th2) {
                if (writableDatabase != null) {
                    writableDatabase.endTransaction();
                }
                throw th;
            }
        }
    }

    public ArrayList<DiscussInfo> getDiscussInfoRecord(String str) {
        ArrayList<DiscussInfo> arrayList = new ArrayList<>();
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("DiscussInfo", null, str, null, null, null, null);
        if (cursorQuery != null && cursorQuery.getCount() > 0) {
            cursorQuery.moveToFirst();
            while (!cursorQuery.isAfterLast()) {
                DiscussInfo discussInfo = new DiscussInfo();
                boolean z = true;
                discussInfo.setStrId(cursorQuery.getString(1));
                discussInfo.setStrName(cursorQuery.getString(2));
                discussInfo.setStrAuthor(cursorQuery.getString(3));
                discussInfo.setStrsMember(cursorQuery.getString(4));
                discussInfo.setLCreateTime(cursorQuery.getLong(5));
                discussInfo.setLEndTime(cursorQuery.getLong(6));
                if (cursorQuery.getInt(7) != 1) {
                    z = false;
                }
                discussInfo.setBExit(z);
                arrayList.add(discussInfo);
                cursorQuery.moveToNext();
            }
        }
        cursorQuery.close();
        return arrayList;
    }

    public ArrayList<DiscussExeStatus> getDiscussExeStatusRecord(String str) {
        ArrayList<DiscussExeStatus> arrayList = new ArrayList<>();
        synchronized (m_strDataBaseName) {
        }
        Cursor cursorQuery = getReadableDatabase().query("DiscussExeStatus", null, str, null, null, null, null);
        if (cursorQuery != null && cursorQuery.getCount() > 0) {
            cursorQuery.moveToFirst();
            while (!cursorQuery.isAfterLast()) {
                DiscussExeStatus discussExeStatus = new DiscussExeStatus();
                discussExeStatus.setStrId(cursorQuery.getString(cursorQuery.getColumnIndex("DiscussID")));
                discussExeStatus.setStrDestMac(cursorQuery.getString(cursorQuery.getColumnIndex("DestMac")));
                discussExeStatus.setStrRecvMac(cursorQuery.getString(cursorQuery.getColumnIndex("RecvMac")));
                discussExeStatus.setLExeTime(cursorQuery.getLong(cursorQuery.getColumnIndex("ExeTime")));
                boolean z = false;
                discussExeStatus.setBIsJoin(cursorQuery.getInt(cursorQuery.getColumnIndex("IsJoin")) == 1);
                if (cursorQuery.getInt(cursorQuery.getColumnIndex("IsNotified")) == 1) {
                    z = true;
                }
                discussExeStatus.setBIsNotified(z);
                arrayList.add(discussExeStatus);
                cursorQuery.moveToNext();
            }
        }
        cursorQuery.close();
        return arrayList;
    }
}

