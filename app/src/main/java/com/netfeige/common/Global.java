package com.netfeige.common;

import java.util.ArrayList;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class Global {
    public static HostInformation g_hostInfo;
    public static Vector<String> g_filePath = new Vector<>();
    public static Vector<String> g_chiocePaths = new Vector<>();
    public static Vector<String> g_pastePaths = new Vector<>();
    public static ArrayList<String> g_sendUserList = new ArrayList<>();
    public static boolean g_bIsInEmptyDir = false;
    public static int g_listViewCurrentAdpter = 1;
    public static Category g_whatFolder = null;
    public static boolean g_isInFileActivity = false;
    public static boolean g_isRoot = true;
    public static int g_nCompressed = 0;
    public static boolean g_isAsyncLoadedImage = true;
    public static boolean g_bIsDelete = false;
    public static Vector<Integer> g_deletedPos = new Vector<>();
    public static boolean g_bBackgroundPlay = false;
    public static boolean g_bIsFromMianBackBtn = false;
    public static boolean g_bInFeige = false;
    public static boolean g_bIsUpdateSQLite = false;
    public static boolean g_bIsEnable = false;
    public static boolean g_bLimited = false;
    public static boolean g_bOpenAudioInChatActivity = false;
    public static int g_nBrowseMethod = 1;
    public static boolean g_bInCategoryDir = false;
    public static boolean g_bInRoot = true;
    public static int g_bWaitPaste = 0;
    public static boolean g_bIsRepeatName = false;
    public static boolean g_bFeigeDownloadChanged = false;
    public static boolean g_bMultipleChoice = false;
    public static HostInformation g_hostInformation = null;
    public static boolean g_bIsClearImageList = false;
    public static ArrayList<ImagePreview> g_imageListCache = new ArrayList<>();
    public static boolean g_bIsClearAudioList = false;
    public static ArrayList<Music> g_audioListCache = new ArrayList<>();
    public static ArrayList<Music> g_videoListCache = new ArrayList<>();
    public static int g_nStatusHeight = 0;
    public static boolean g_bChangedHead = false;
    public static boolean g_bWorking = false;
    public static String g_strSharePath = null;
    public static int g_nNumber = 1;
    public static boolean g_bInChoiceRemote = false;
    public static boolean g_bInImageFromRemote = true;
    public static boolean g_bWiFiAPWorking = false;
    public static int g_nDensityDpi = 1;

    public enum UserHandleType {
        addUser,
        modifyUser,
        removeUser
    }
}

