package com.netfeige.kits;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;
import com.netfeige.common.Public_Tools;
import com.netfeige.dlna.ContentTree;

/* JADX INFO: loaded from: classes.dex */
public class DataConfig implements IDataConfig {
    private static Context m_Context = null;
    private static IDataConfig m_DataConfig = null;
    private static final String m_PreferencesFile = "IPMSG_CONFIG";
    private static SharedPreferences m_SharedPreferences = null;
    private static final String m_strAutoRecvFile = "1";
    private static final String m_strBrowseMethod = "1";
    private static final String m_strChatGuide = "0";
    private static final String m_strCheckCompress = "0";
    private static final String m_strCheckUpdate = "1";
    private static final String m_strDelFilePrompt = "1";
    private static final String m_strFeigeGuide = "0";
    private static final String m_strFeigePosition = "-1";
    private static final String m_strGroupName = "Android";
    private static final String m_strHeadImage = "0";
    private static final String m_strMainGuide = "0";
    private static final String m_strMsgNotification = "1";
    private static final String m_strPromptAudio = "1";
    private static final String m_strSendAudio = "1";
    private static final String m_strSortConfig = "0";
    private static final String m_strWTGuide = "0";
    private SparseArray<String> m_mapDefaultList = new SparseArray<>();

    private DataConfig() {
        DefaultConfig();
    }

    private void DefaultConfig() {
        this.m_mapDefaultList.put(0, Public_Tools.getLocalHostName());
        this.m_mapDefaultList.put(1, "Android");
        this.m_mapDefaultList.put(2, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(3, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(4, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(5, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(6, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(7, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(28, ContentTree.ROOT_ID);
        this.m_mapDefaultList.put(29, ContentTree.VIDEO_ID);
        this.m_mapDefaultList.put(30, ContentTree.ROOT_ID);
        this.m_mapDefaultList.put(31, ContentTree.ROOT_ID);
        this.m_mapDefaultList.put(32, ContentTree.ROOT_ID);
        this.m_mapDefaultList.put(33, ContentTree.ROOT_ID);
        this.m_mapDefaultList.put(34, ContentTree.ROOT_ID);
        this.m_mapDefaultList.put(35, m_strFeigePosition);
        this.m_mapDefaultList.put(36, ContentTree.ROOT_ID);
    }

    public static IDataConfig getInstance(Context context) {
        if (context == null) {
            return null;
        }
        if (m_DataConfig == null) {
            m_DataConfig = new DataConfig();
        }
        if (m_Context != context) {
            m_Context = context;
            m_SharedPreferences = context.getSharedPreferences(m_PreferencesFile, 0);
        }
        return m_DataConfig;
    }

    @Override // com.netfeige.kits.IDataConfig
    public boolean Reset() {
        if ((this.m_mapDefaultList.size() == 0) || (m_SharedPreferences == null)) {
            return false;
        }
        for (int i = 0; i < this.m_mapDefaultList.size(); i++) {
            Write(i, this.m_mapDefaultList.valueAt(i));
        }
        return true;
    }

    @Override // com.netfeige.kits.IDataConfig
    public String Read(int i) {
        try {
            if (m_SharedPreferences == null) {
                return "";
            }
            return m_SharedPreferences.getString(String.valueOf(i), this.m_mapDefaultList.get(i));
        } catch (ClassCastException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    @Override // com.netfeige.kits.IDataConfig
    public boolean Write(int i, String str) {
        SharedPreferences sharedPreferences = m_SharedPreferences;
        if (sharedPreferences == null) {
            return false;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString(Integer.valueOf(i).toString(), str);
        return editorEdit.commit();
    }

    @Override // com.netfeige.kits.IDataConfig
    public boolean writeBoolean(int i, boolean z) {
        SharedPreferences sharedPreferences = m_SharedPreferences;
        if (sharedPreferences == null) {
            return false;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putBoolean(String.valueOf(i), z);
        return editorEdit.commit();
    }

    @Override // com.netfeige.kits.IDataConfig
    public boolean readBoolean(int i, boolean z) {
        try {
            return m_SharedPreferences == null ? z : m_SharedPreferences.getBoolean(Integer.valueOf(i).toString(), z);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    @Override // com.netfeige.kits.IDataConfig
    public SharedPreferences getSharedPreferences() {
        return m_SharedPreferences;
    }
}

