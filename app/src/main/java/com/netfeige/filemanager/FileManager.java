package com.netfeige.filemanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.DBHelper;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SDCardInfo;
import com.netfeige.common.ShareFiles;
import com.netfeige.common.StorageDevice;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.FileActivity;
import com.netfeige.display.ui.ImagePreviewActivity;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.display.ui.MyShareActivity;
import com.netfeige.display.ui.PlayMusicActivity;
import com.netfeige.display.ui.PlayVideoActivity;
import com.netfeige.display.ui.TxtBrowserActivity;
import com.netfeige.dlna.HttpServer;
import com.netfeige.enums.FeigeRemoveState;
import com.netfeige.enums.FileAccessAuth;
import com.netfeige.kits.DataConfig;
import com.netfeige.service.MusicService;
import com.netfeige.util.OpenFileUtil;
import com.netfeige.util.SDCardUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class FileManager implements IFileManager {
    private static final int ERROR = -1;
    private static IFileManager m_IFileManager = null;
    private static Context m_context = null;
    private static final int m_nCacheSpace = 65536;
    private static final String m_strFileExists = "鏂囦欢宸插瓨鍦?";
    private static final String m_strInSubdirectory = "目标文件夹是源文件夹的子文件夹，不能粘贴，";;
    public static String m_strPrompt;
    private final String[][] MIME_MapTable;
    private final int m_nApkMsgWath;
    private final int m_nAudioMsgWath;
    private final int m_nCompressStopMsgWath;
    private final int m_nDocumentMsgWath;
    private final int m_nImageMsgWath;
    private final int m_nVideoMsgWath;
    private FeigeDirRemoveRunnableImp m_removeRunnableImp;
    private final String m_strAndroidPath1;
    private final String m_strAndroidPath2;
    private final String m_strAudio1;
    private final String m_strAudio2;
    private final String m_strCamera1;
    private final String m_strCamera2;
    private String m_strFeige;
    private final String[] m_strFiltratePathForAudio;
    private final String[] m_strFiltratePathForImage;
    private final String[] m_strFiltratePathForNull;
    private final String[] m_strFiltratePathForVideo;
    private String m_strSdPath;
    private final String m_strVideo1;
    private final String m_strVideo2;
    private Vector<String> m_vecFiltratePathForAudio;
    private Vector<String> m_vecFiltratePathForImage;
    private Vector<String> m_vecFiltratePathForNull;
    private Vector<String> m_vecFiltratePathForVideo;
    public static ArrayList<String> m_imageList = new ArrayList<>();
    public static ArrayList<String> m_audioList = new ArrayList<>();
    public static ArrayList<String> m_videoList = new ArrayList<>();
    public static ArrayList<String> m_documentList = new ArrayList<>();
    public static ArrayList<String> m_apkList = new ArrayList<>();
    public static ArrayList<String> m_apkFolderList = new ArrayList<>();
    public static ArrayList<String> m_documentFolderList = new ArrayList<>();
    public static ArrayList<String> m_imageFolderList = new ArrayList<>();
    public static ArrayList<String> m_audioFolderList = new ArrayList<>();
    public static ArrayList<String> m_videoFolderList = new ArrayList<>();
    public static String g_filePathForCopy = "";
    public static String g_filePathForCut = "";
    public static StorageDevice g_storageDevice = new StorageDevice();
    public static boolean g_bStopPasteThread = false;
    private long m_lFileLength = 0;
    private String m_strDocumentCount = "-1";
    private String m_strApkCount = "-1";
    private String m_strImageCount = "-1";
    private String m_strAudioCount = "-1";
    private String m_strVideoCount = "-1";

    public FeigeDirRemoveRunnableImp getRemoveRunnableImp() {
        return this.m_removeRunnableImp;
    }

    private FileManager(Context context) throws Throwable {
        this.m_vecFiltratePathForImage = null;
        this.m_vecFiltratePathForAudio = null;
        this.m_vecFiltratePathForVideo = null;
        this.m_vecFiltratePathForNull = null;
        this.m_strSdPath = null;
        String defaultDownloadPath = Public_Tools.getDefaultDownloadPath();
        this.m_strFeige = defaultDownloadPath;
        this.m_strCamera1 = "/mnt/sdcard/DCIM/Camera";
        this.m_strCamera2 = "/mnt/sdcard2/DCIM/Camera";
        this.m_strAudio1 = "/mnt/sdcard/music";
        this.m_strAudio2 = "/mnt/sdcard2/music";
        this.m_strVideo1 = "/mnt/sdcard/video";
        this.m_strVideo2 = "/mnt/sdcard2/video";
        this.m_strAndroidPath1 = "/mnt/sdcard/Android";
        this.m_strAndroidPath2 = "/mnt/sdcard2/Android";
        this.m_strFiltratePathForImage = new String[]{defaultDownloadPath, "/mnt/sdcard/Android", "/mnt/sdcard2/Android", "/mnt/sdcard/DCIM/Camera", "/mnt/sdcard2/DCIM/Camera"};
        this.m_strFiltratePathForAudio = new String[]{defaultDownloadPath, "/mnt/sdcard/Android", "/mnt/sdcard2/Android", "/mnt/sdcard/music", "/mnt/sdcard2/music", "/mnt/sdcard/DCIM/Camera", "/mnt/sdcard2/DCIM/Camera"};
        this.m_strFiltratePathForVideo = new String[]{defaultDownloadPath, "/mnt/sdcard/Android", "/mnt/sdcard2/Android", "/mnt/sdcard/video", "/mnt/sdcard2/video", "/mnt/sdcard/DCIM/Camera", "/mnt/sdcard2/DCIM/Camera"};
        this.m_strFiltratePathForNull = new String[]{defaultDownloadPath};
        this.m_nImageMsgWath = 10;
        this.m_nAudioMsgWath = 11;
        this.m_nVideoMsgWath = 12;
        this.m_nDocumentMsgWath = 13;
        this.m_nApkMsgWath = 14;
        this.m_nCompressStopMsgWath = 17;
        this.m_removeRunnableImp = new FeigeDirRemoveRunnableImp();
        this.MIME_MapTable = new String[][]{new String[]{".3gp", "video/3gpp"}, new String[]{".apk", "application/vnd.android.package-archive"}, new String[]{".asf", "video/x-ms-asf"}, new String[]{".avi", "video/x-msvideo"}, new String[]{".bin", HttpServer.MIME_DEFAULT_BINARY}, new String[]{".bmp", "image/bmp"}, new String[]{".c", HttpServer.MIME_PLAINTEXT}, new String[]{".class", HttpServer.MIME_DEFAULT_BINARY}, new String[]{".conf", HttpServer.MIME_PLAINTEXT}, new String[]{".cpp", HttpServer.MIME_PLAINTEXT}, new String[]{".doc", "application/msword"}, new String[]{".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, new String[]{".xls", "application/vnd.ms-excel"}, new String[]{".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, new String[]{".exe", HttpServer.MIME_DEFAULT_BINARY}, new String[]{".gif", "image/gif"}, new String[]{".gtar", "application/x-gtar"}, new String[]{".gz", "application/x-gzip"}, new String[]{".h", HttpServer.MIME_PLAINTEXT}, new String[]{".htm", HttpServer.MIME_HTML}, new String[]{".html", HttpServer.MIME_HTML}, new String[]{".jar", "application/java-archive"}, new String[]{".java", HttpServer.MIME_PLAINTEXT}, new String[]{".jpeg", "image/jpeg"}, new String[]{".jpg", "image/jpeg"}, new String[]{".js", "application/x-javascript"}, new String[]{".log", HttpServer.MIME_PLAINTEXT}, new String[]{".m3u", "audio/x-mpegurl"}, new String[]{".m4a", "audio/mp4a-latm"}, new String[]{".m4b", "audio/mp4a-latm"}, new String[]{".m4p", "audio/mp4a-latm"}, new String[]{".m4u", "video/vnd.mpegurl"}, new String[]{".m4v", "video/x-m4v"}, new String[]{".mov", "video/quicktime"}, new String[]{".mp2", "audio/x-mpeg"}, new String[]{".mp3", "audio/x-mpeg"}, new String[]{".mp4", "video/mp4"}, new String[]{".mpc", "application/vnd.mpohun.certificate"}, new String[]{".mpe", "video/mpeg"}, new String[]{".mpeg", "video/mpeg"}, new String[]{".mpg", "video/mpeg"}, new String[]{".mpg4", "video/mp4"}, new String[]{".mpga", "audio/mpeg"}, new String[]{".msg", "application/vnd.ms-outlook"}, new String[]{".ogg", "audio/ogg"}, new String[]{".pdf", "application/pdf"}, new String[]{".png", "image/png"}, new String[]{".pps", "application/vnd.ms-powerpoint"}, new String[]{".ppt", "application/vnd.ms-powerpoint"}, new String[]{".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, new String[]{".prop", HttpServer.MIME_PLAINTEXT}, new String[]{".rc", HttpServer.MIME_PLAINTEXT}, new String[]{".rmvb", "audio/x-pn-realaudio"}, new String[]{".rtf", "application/rtf"}, new String[]{".sh", HttpServer.MIME_PLAINTEXT}, new String[]{".tar", "application/x-tar"}, new String[]{".tgz", "application/x-compressed"}, new String[]{".txt", HttpServer.MIME_PLAINTEXT}, new String[]{".wav", "audio/x-wav"}, new String[]{".wma", "audio/x-ms-wma"}, new String[]{".wmv", "audio/x-ms-wmv"}, new String[]{".wps", "application/vnd.ms-works"}, new String[]{".xml", HttpServer.MIME_PLAINTEXT}, new String[]{".z", "application/x-compress"}, new String[]{".zip", "application/x-zip-compressed"}, new String[]{"", "*/*"}};
        m_context = context;
        this.m_strSdPath = Public_Tools.getSDCardPath();
        Vector<String> vector = new Vector<>();
        this.m_vecFiltratePathForImage = vector;
        forAddFiltrate(this.m_strFiltratePathForImage, vector);
        Vector<String> vector2 = new Vector<>();
        this.m_vecFiltratePathForAudio = vector2;
        forAddFiltrate(this.m_strFiltratePathForAudio, vector2);
        Vector<String> vector3 = new Vector<>();
        this.m_vecFiltratePathForVideo = vector3;
        forAddFiltrate(this.m_strFiltratePathForVideo, vector3);
        Vector<String> vector4 = new Vector<>();
        this.m_vecFiltratePathForNull = vector4;
        forAddFiltrate(this.m_strFiltratePathForNull, vector4);
    }

    private void forAddFiltrate(String[] strArr, Vector<String> vector) {
        for (String str : strArr) {
            vector.add(str);
        }
    }

    public static IFileManager getIFileManager(Context context) {
        if (m_IFileManager == null) {
            m_IFileManager = new FileManager(context);
        }
        return m_IFileManager;
    }

    @Override // com.netfeige.filemanager.IFileManager
    public boolean createFolder(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                return false;
            }
            file.mkdir();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    @Override // com.netfeige.filemanager.IFileManager
    public void openFile(File file) {
        if (file.exists()) {
            if (!file.isFile()) {
                openFolder(file);
                return;
            }
            if (file.length() > 0) {
                if (Public_Tools.isImageFile(file.getName())) {
                    openImageFile(file);
                    return;
                }
                if (Public_Tools.isAudioFile(file.getName())) {
                    openAudioFile(file);
                    return;
                }
                if (Public_Tools.isVideoFile(file.getName())) {
                    openVideoFile(file);
                    return;
                } else if (Public_Tools.isTxtFile(file.getName())) {
                    openTxtFile(file);
                    return;
                } else {
                    openOtherFile(file);
                    return;
                }
            }
            Toast.makeText(m_context, R.string.sizeis0_notify, 0).show();
            return;
        }
        Toast.makeText(m_context, R.string.no_open_notify, 0).show();
    }

    private void openFolder(File file) {
        if (m_context instanceof IpmsgActivity) {
            try {
                IpmsgActivity.s_fileListView.moveToCategory(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openOtherFile(File file) {
        try {
            m_context.startActivity(OpenFileUtil.openFile(m_context, file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAudioFile(File file) {
        try {
            long duration = Public_Tools.getDuration(file.getPath());
            if (duration != 0) {
                if (!Global.g_bIsFromMianBackBtn) {
                    IpmsgApplication.g_arrPlayingList.clear();
                    IpmsgApplication.g_arrPlayingList = (ArrayList) IpmsgApplication.g_arrMusicList.clone();
                    MusicService.s_bIsPause = false;
                }
                Intent intent = new Intent();
                intent.setClass(m_context, PlayMusicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("filePath", file.getAbsolutePath());
                bundle.putLong("time", duration);
                intent.putExtras(bundle);
                m_context.startActivity(intent);
                Global.g_bIsFromMianBackBtn = false;
                return;
            }
            Toast.makeText(m_context, R.string.no_play_notify, 0).show();
        } catch (Exception unused) {
            playAudio(file.getPath());
        } catch (NoClassDefFoundError unused2) {
            playAudio(file.getPath());
        }
    }

    private void openVideoFile(File file) {
        try {
            long duration = Public_Tools.getDuration(file.getPath());
            if (duration != 0) {
                IpmsgApplication.g_playingVideoList.clear();
                IpmsgApplication.g_playingVideoList = (ArrayList) IpmsgApplication.g_arrVideoList.clone();
                int musicPositionOnPath = Public_Tools.getMusicPositionOnPath(IpmsgApplication.g_playingVideoList, file.getAbsolutePath());
                if (musicPositionOnPath == -1) {
                    Toast.makeText(m_context, R.string.no_open_notify, 0).show();
                } else {
                    IpmsgApplication.g_playingVideoList.get(musicPositionOnPath).setTime(duration);
                    Intent intent = new Intent();
                    intent.setClass(m_context, PlayVideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentIndex", musicPositionOnPath);
                    intent.putExtras(bundle);
                    m_context.startActivity(intent);
                }
            } else {
                Toast.makeText(m_context, R.string.no_play_notify, 0).show();
            }
        } catch (Exception unused) {
            openOtherFile(file);
        } catch (NoClassDefFoundError unused2) {
            openOtherFile(file);
        }
    }

    private void playAudio(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse("file://" + str), "audio/*");
        m_context.startActivity(intent);
    }

    private void openImageFile(File file) {
        Intent intent = new Intent();
        intent.setClass(m_context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", file.getAbsolutePath());
        bundle.putBoolean("AVTransport", false);
        intent.putExtras(bundle);
        m_context.startActivity(intent);
    }

    private void openTxtFile(File file) {
        Intent intent = new Intent();
        intent.setClass(m_context, TxtBrowserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", file.getAbsolutePath());
        intent.putExtras(bundle);
        m_context.startActivity(intent);
    }

    @Override // com.netfeige.filemanager.IFileManager
    public boolean Rename(File file, String str) {
        String absolutePath = file.getAbsolutePath();
        String strSubstring = absolutePath.substring(0, absolutePath.lastIndexOf(ServiceReference.DELIMITER));
        if (strSubstring.length() == 0) {
            strSubstring = ServiceReference.DELIMITER;
        }
        String str2 = strSubstring + ServiceReference.DELIMITER + str;
        boolean zRenameTo = file.renameTo(new File(str2));
        if (zRenameTo && !file.isFile()) {
            try {
                renamedUpdate(absolutePath, str2);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return zRenameTo;
    }

    public static void renamedUpdate(String str, String str2) {
        replaceOldPath(m_imageFolderList, str, str2);
        replaceOldPath(m_audioFolderList, str, str2);
        replaceOldPath(m_videoFolderList, str, str2);
        replaceOldPath(m_documentFolderList, str, str2);
        replaceOldPath(m_apkFolderList, str, str2);
    }

    private static void replaceOldPath(ArrayList<String> arrayList, String str, String str2) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).startsWith(str)) {
                arrayList.set(i, arrayList.get(i).replaceFirst(str, str2));
                Global.g_bIsUpdateSQLite = true;
            }
        }
    }

    @Override // com.netfeige.filemanager.IFileManager
    public void copyFile(File file) {
        g_filePathForCut = "";
        g_filePathForCopy = file.getPath();
        Global.g_bWaitPaste = 1;
    }

    @Override // com.netfeige.filemanager.IFileManager
    public boolean pasteFile(String str, String str2) {
        File file = new File(str);
        boolean zPasteFolder = false;
        if (!file.exists()) {
            return false;
        }
        if (g_filePathForCopy != "") {
            if (file.isFile()) {
                return paste(str, str2);
            }
            if (file.isDirectory()) {
                return pasteFolder(str, str2);
            }
            return false;
        }
        if (g_filePathForCut == "") {
            return false;
        }
        if (file.isFile()) {
            zPasteFolder = paste(str, str2);
        } else if (file.isDirectory()) {
            zPasteFolder = pasteFolder(str, str2);
        }
        return zPasteFolder ? deleteFile(file) : zPasteFolder;
    }

    public boolean pasteFolder(String str, String str2) {
        boolean z;
        File file = new File(str);
        String dirName = getDirName(str);
        String str3 = str2.endsWith(File.separator) ? str2 + dirName : str2 + File.separator + dirName;
        if (str3.startsWith(str)) {
            if (str3.equals(str)) {
                structureSameNotify(str3, m_strFileExists);
            } else {
                structureSameNotify(str3, m_strInSubdirectory);
            }
            return false;
        }
        File file2 = new File(str3);
        if (file2.exists()) {
            structureSameNotify(str3, m_strFileExists);
            return false;
        }
        file2.mkdirs();
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles.length == 0) {
            z = true;
        } else {
            boolean zPasteFolder = false;
            for (File file3 : fileArrListFiles) {
                if (file3.isFile()) {
                    zPasteFolder = paste(file3.getAbsolutePath(), str3);
                } else if (file3.isDirectory()) {
                    zPasteFolder = pasteFolder(file3.getAbsolutePath(), str3);
                }
                if (!zPasteFolder) {
                    break;
                }
            }
            z = zPasteFolder;
        }
        if (z) {
            Public_Tools.updateFolderList(str, str3);
        }
        return z;
    }

    private void structureSameNotify(String str, String str2) {
        m_strPrompt += "\n" + str + " : " + str2;
        Global.g_bIsRepeatName = true;
    }

    private boolean paste(String str, String str2) {
        String str3 = str2 + str.substring(str.lastIndexOf(File.separator));
        if (str3.equals(str)) {
            structureSameNotify(str3, m_strFileExists);
            Global.g_bIsRepeatName = true;
            return false;
        }
        File file = new File(str3);
        if (file.exists() && file.isFile()) {
            structureSameNotify(str3, m_strFileExists);
            Global.g_bIsRepeatName = true;
            return false;
        }
        new File(str2).mkdirs();
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[65536];
            while (true) {
                int i = fileInputStream.read(bArr);
                if (i != -1) {
                    fileOutputStream.write(bArr, 0, i);
                } else {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileInputStream.close();
                    return true;
                }
            }
        } catch (IOException unused) {
            return false;
        }
    }

    public static String getDirName(String str) {
        if (str.endsWith(File.separator)) {
            str = str.substring(0, str.lastIndexOf(File.separator));
        }
        return str.substring(str.lastIndexOf(File.separator) + 1);
    }

    public class FeigeDirRemoveRunnableImp implements Runnable {
        private static final int m_iNotifyInterval = 800;
        private String m_strNewFeigePath;
        private String m_strOldFeigePath;
        private Thread m_thread = null;
        private FeigeRemoveState m_runState = FeigeRemoveState.STOPED;
        private long m_lRemovedSize = 0;
        private long m_iTimeStamp = 0;
        private Handler m_handlerGrogress = null;

        public FeigeDirRemoveRunnableImp() {
        }

        public FeigeRemoveState getRunState() {
            return this.m_runState;
        }

        public void setRunState(FeigeRemoveState feigeRemoveState) {
            this.m_runState = feigeRemoveState;
        }

        public String getStrOldFeigePath() {
            return this.m_strOldFeigePath;
        }

        public void setStrOldFeigePath(String str) {
            this.m_strOldFeigePath = str;
        }

        public String getStrNewFeigePath() {
            return this.m_strNewFeigePath;
        }

        public void setStrNewFeigePath(String str) {
            this.m_strNewFeigePath = str;
        }

        public long getlRemovedSize() {
            return this.m_lRemovedSize;
        }

        public void setlRemovedSize(long j) {
            this.m_lRemovedSize = j;
        }

        public Handler getHandlerGrogress() {
            return this.m_handlerGrogress;
        }

        public void setHandlerGrogress(Handler handler) {
            this.m_handlerGrogress = handler;
        }

        public void start() {
            Thread thread = new Thread(this);
            this.m_thread = thread;
            thread.start();
            this.m_iTimeStamp = System.currentTimeMillis();
            setRunState(FeigeRemoveState.REMOVING);
        }

        public void interrupt() {
            if (this.m_thread == null || Thread.currentThread().isInterrupted()) {
                return;
            }
            this.m_thread.interrupt();
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                try {
                    this.m_lRemovedSize = 0L;
                    if (pasteDirectory(this.m_strOldFeigePath, this.m_strNewFeigePath)) {
                        DataConfig.getInstance(FileManager.m_context).Write(35, FileManager.g_storageDevice.getStrMountPoint());
                        Global.g_bFeigeDownloadChanged = true;
                        if (this.m_handlerGrogress != null) {
                            this.m_handlerGrogress.sendEmptyMessage(1);
                        }
                        setRunState(FeigeRemoveState.OLDFEIGE_DELETING);
                        FileManager.getIFileManager(FileManager.m_context).deleteFile(new File(this.m_strOldFeigePath));
                        if (this.m_handlerGrogress != null) {
                            this.m_handlerGrogress.sendEmptyMessage(3);
                        }
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        setRunState(FeigeRemoveState.NEWFEIGE_DELETING);
                        FileManager.getIFileManager(FileManager.m_context).deleteFile(new File(this.m_strNewFeigePath + File.separator + FileManager.getDirName(this.m_strOldFeigePath)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                this.m_thread = null;
                setRunState(FeigeRemoveState.STOPED);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:39:?, code lost:
        
            return r4;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean pasteDirectory(java.lang.String r8, java.lang.String r9) {
            /*
                r7 = this;
                r0 = 2
                r1 = 0
                java.io.File r2 = new java.io.File     // Catch: java.lang.Exception -> L7c
                r2.<init>(r8)     // Catch: java.lang.Exception -> L7c
                java.lang.String r8 = com.netfeige.filemanager.FileManager.getDirName(r8)     // Catch: java.lang.Exception -> L7c
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L7c
                r3.<init>()     // Catch: java.lang.Exception -> L7c
                r3.append(r9)     // Catch: java.lang.Exception -> L7c
                java.lang.String r9 = java.io.File.separator     // Catch: java.lang.Exception -> L7c
                r3.append(r9)     // Catch: java.lang.Exception -> L7c
                r3.append(r8)     // Catch: java.lang.Exception -> L7c
                java.lang.String r8 = r3.toString()     // Catch: java.lang.Exception -> L7c
                java.io.File r9 = new java.io.File     // Catch: java.lang.Exception -> L7c
                r9.<init>(r8)     // Catch: java.lang.Exception -> L7c
                r9.mkdirs()     // Catch: java.lang.Exception -> L7c
                java.io.File[] r9 = r2.listFiles()     // Catch: java.lang.Exception -> L7c
                if (r9 == 0) goto L72
                int r2 = r9.length     // Catch: java.lang.Exception -> L7c
                if (r2 != 0) goto L32
                r1 = 1
                goto L84
            L32:
                int r2 = r9.length     // Catch: java.lang.Exception -> L7c
                r3 = 0
                r4 = 0
            L35:
                if (r3 >= r2) goto L70
                r5 = r9[r3]     // Catch: java.lang.Exception -> L7c
                java.lang.Thread r6 = java.lang.Thread.currentThread()     // Catch: java.lang.Exception -> L7c
                boolean r6 = r6.isInterrupted()     // Catch: java.lang.Exception -> L7c
                if (r6 == 0) goto L44
                goto L84
            L44:
                boolean r6 = r5.isFile()     // Catch: java.lang.Exception -> L7c
                if (r6 == 0) goto L53
                java.lang.String r4 = r5.getAbsolutePath()     // Catch: java.lang.Exception -> L7c
                boolean r4 = r7.paste(r4, r8)     // Catch: java.lang.Exception -> L7c
                goto L61
            L53:
                boolean r6 = r5.isDirectory()     // Catch: java.lang.Exception -> L7c
                if (r6 == 0) goto L61
                java.lang.String r4 = r5.getAbsolutePath()     // Catch: java.lang.Exception -> L7c
                boolean r4 = r7.pasteDirectory(r4, r8)     // Catch: java.lang.Exception -> L7c
            L61:
                if (r4 != 0) goto L6d
                android.os.Handler r8 = r7.m_handlerGrogress     // Catch: java.lang.Exception -> L7c
                if (r8 == 0) goto L70
                android.os.Handler r8 = r7.m_handlerGrogress     // Catch: java.lang.Exception -> L7c
                r8.sendEmptyMessage(r0)     // Catch: java.lang.Exception -> L7c
                goto L70
            L6d:
                int r3 = r3 + 1
                goto L35
            L70:
                r1 = r4
                goto L84
            L72:
                android.os.Handler r8 = r7.m_handlerGrogress     // Catch: java.lang.Exception -> L7c
                if (r8 == 0) goto L84
                android.os.Handler r8 = r7.m_handlerGrogress     // Catch: java.lang.Exception -> L7c
                r8.sendEmptyMessage(r0)     // Catch: java.lang.Exception -> L7c
                goto L84
            L7c:
                android.os.Handler r8 = r7.m_handlerGrogress
                if (r8 == 0) goto L84
                r8.sendEmptyMessage(r0)
            L84:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.netfeige.filemanager.FileManager.FeigeDirRemoveRunnableImp.pasteDirectory(java.lang.String, java.lang.String):boolean");
        }

        private boolean paste(String str, String str2) {
            File file = new File(str2 + str.substring(str.lastIndexOf(File.separator)));
            try {
                FileInputStream fileInputStream = new FileInputStream(str);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[65536];
                while (true) {
                    int i = fileInputStream.read(bArr);
                    if (i != -1) {
                        if (Thread.currentThread().isInterrupted()) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            fileInputStream.close();
                            return false;
                        }
                        fileOutputStream.write(bArr, 0, i);
                        this.m_lRemovedSize += (long) i;
                        if (System.currentTimeMillis() - this.m_iTimeStamp > 800) {
                            this.m_iTimeStamp = System.currentTimeMillis();
                            if (this.m_handlerGrogress != null) {
                                this.m_handlerGrogress.sendEmptyMessage(0);
                            }
                        }
                    } else {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        fileInputStream.close();
                        return true;
                    }
                }
            } catch (IOException | Exception unused) {
                return false;
            }
        }
    }

    @Override // com.netfeige.filemanager.IFileManager
    public boolean deleteFile(File file) {
        boolean zDelete;
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            zDelete = file.delete();
        } else if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles != null) {
                for (File file2 : fileArrListFiles) {
                    deleteFile(file2);
                }
            }
            zDelete = file.delete();
        } else {
            zDelete = false;
        }
        if (!file.isFile() && Public_Tools.isInDeletedDirectory(MusicService.s_nCurrentPlayingPath, file.getAbsolutePath()) > -1 && MusicService.s_mediaPlayer != null) {
            MusicService.s_mediaPlayer.stop();
            MusicService.s_mediaPlayer.release();
            MusicService.s_mediaPlayer = null;
            IpmsgActivity.s_imageBtnBackgroundPlay.setVisibility(4);
            Global.g_bBackgroundPlay = false;
        }
        return zDelete;
    }

    private String getMIMEType(File file) {
        String lowerCase;
        String name = file.getName();
        int iLastIndexOf = name.lastIndexOf(".");
        String str = "*/*";
        if (iLastIndexOf < 0 || (lowerCase = name.substring(iLastIndexOf, name.length()).toLowerCase()) == "") {
            return "*/*";
        }
        int i = 0;
        while (true) {
            String[][] strArr = this.MIME_MapTable;
            if (i >= strArr.length) {
                return str;
            }
            if (lowerCase.equals(strArr[i][0])) {
                str = this.MIME_MapTable[i][1];
            }
            i++;
        }
    }

    @Override // com.netfeige.filemanager.IFileManager
    public void cutFile(File file) {
        g_filePathForCopy = "";
        g_filePathForCut = file.getPath();
        Global.g_bWaitPaste = 1;
    }

    @Override // com.netfeige.filemanager.IFileManager
    public String[] getFileDetail(File file) {
        String str;
        String name = file.getName();
        String shortSize = Public_Tools.getShortSize(getLength(file));
        String absolutePath = file.getAbsolutePath();
        FileAccessAuth fileAccessAuthAuthority = Public_Tools.authority(file);
        if (fileAccessAuthAuthority == FileAccessAuth.R_OK) {
            str = "鍙";
        } else if (fileAccessAuthAuthority == FileAccessAuth.W_OK) {
            str = "鍙啓";
        } else if (fileAccessAuthAuthority == FileAccessAuth.RW_OK) {
            str = "鍙鍙啓";
        } else {
            str = fileAccessAuthAuthority == FileAccessAuth.NONE ? "涓嶅彲璇讳笉鍙啓" : "鏈煡";
        }
        return new String[]{name, shortSize, str, Public_Tools.lastModifiedTime(file.lastModified()), absolutePath};
    }

    private long getLength(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                this.m_lFileLength = file.length();
            } else if (file.isDirectory()) {
                this.m_lFileLength = 0L;
            }
        }
        return this.m_lFileLength;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filtrateFolder(String str, Vector<String> vector) {
        boolean zHasDot = hasDot(str);
        boolean zFilterSpecifyFolder = filterSpecifyFolder(str);
        if (vector.contains(str) || zHasDot || zFilterSpecifyFolder) {
            return;
        }
        getMyFiles(str, vector);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getMyFiles(String str, Vector<String> vector) {
        try {
            File file = new File(str);
            if (file.isFile()) {
                if (file.isHidden()) {
                    return;
                }
                String name = file.getName();
                String absolutePath = file.getAbsolutePath();
                if (Global.g_nBrowseMethod == 0) {
                    specifyFileDispose(name, absolutePath);
                    return;
                } else {
                    specifyFolderDispose(name, absolutePath);
                    return;
                }
            }
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles == null || fileArrListFiles.length <= 0) {
                return;
            }
            for (File file2 : fileArrListFiles) {
                if (vector == null) {
                    getMyFiles(file2.getPath(), vector);
                } else {
                    filtrateFolder(file2.getPath(), vector);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void specifyFileDispose(String str, String str2) {
        if (Public_Tools.isImageFile(str)) {
            handleInCompress(str2, m_imageFolderList, m_imageList, this.m_strImageCount, Category.image, 10, "imageState");
            return;
        }
        if (Public_Tools.isAudioFile(str)) {
            handleInCompress(str2, m_audioFolderList, m_audioList, this.m_strAudioCount, Category.audio, 11, "audioState");
            return;
        }
        if (Public_Tools.isVideoFile(str)) {
            handleInCompress(str2, m_videoFolderList, m_videoList, this.m_strVideoCount, Category.video, 12, "videoState");
        } else if (Public_Tools.isDocumentFile(str)) {
            handleInCompress(str2, m_documentFolderList, m_documentList, this.m_strDocumentCount, Category.document, 13, "documentState");
        } else if (Public_Tools.isApkFile(str)) {
            handleInCompress(str2, m_apkFolderList, m_apkList, this.m_strApkCount, Category.apk, 14, "apkState");
        }
    }

    private void specifyFolderDispose(String str, String str2) {
        if (Public_Tools.isImageFile(str)) {
            handleInCompress(str2, m_imageFolderList, m_imageList, this.m_strImageCount, Category.image, 10, "imageState");
            return;
        }
        if (Public_Tools.isAudioFile(str)) {
            handleInCompress(str2, m_audioFolderList, m_audioList, this.m_strAudioCount, Category.audio, 11, "audioState");
            return;
        }
        if (Public_Tools.isVideoFile(str)) {
            handleInCompress(str2, m_videoFolderList, m_videoList, this.m_strVideoCount, Category.video, 12, "videoState");
        } else if (Public_Tools.isDocumentFile(str)) {
            handleInCompress(str2, m_documentFolderList, m_documentList, this.m_strDocumentCount, Category.document, 13, "documentState");
        } else if (Public_Tools.isApkFile(str)) {
            handleInCompress(str2, m_apkFolderList, m_apkList, this.m_strApkCount, Category.apk, 14, "apkState");
        }
    }

    private void handleInCompress(String str, ArrayList<String> arrayList, ArrayList<String> arrayList2, String str2, Category category, int i, String str3) {
        if (Global.g_nBrowseMethod == 0) {
            sendMessage(str, arrayList, arrayList2, category, i, str3);
        } else {
            sendMessage(str, arrayList, category, i, str3);
        }
    }

    private void sendMessage(String str, ArrayList<String> arrayList, ArrayList<String> arrayList2, Category category, int i, String str2) {
        String folderPath = Public_Tools.getFolderPath(str);
        inSendMessage(str, arrayList2, category, i, str2);
        if (arrayList.contains(folderPath)) {
            return;
        }
        arrayList.add(folderPath);
    }

    private void sendMessage(String str, ArrayList<String> arrayList, Category category, int i, String str2) {
        inSendMessage(Public_Tools.getFolderPath(str), arrayList, category, i, str2);
    }

    private void inSendMessage(String str, ArrayList<String> arrayList, Category category, int i, String str2) {
        if (arrayList.contains(str)) {
            return;
        }
        arrayList.add(str);
        StringBuilder sb = new StringBuilder();
        sb.append(arrayList.size() - 1);
        sb.append("");
        String string = sb.toString();
        if (Global.g_whatFolder != category || Global.g_bInCategoryDir) {
            return;
        }
        Context context = m_context;
        if (context instanceof IpmsgActivity) {
            Message message = new Message();
            packageMessage(string, i, str2, message);
            ((IpmsgActivity) context).getM_compressHandler().sendMessage(message);
        } else if (context instanceof FileActivity) {
            Message message2 = new Message();
            packageMessage(string, i, str2, message2);
            ((FileActivity) context).getM_compressHandler().sendMessage(message2);
        }
    }

    private void packageMessage(String str, int i, String str2, Message message) {
        message.what = i;
        Bundle bundle = new Bundle();
        bundle.putInt(str2, Integer.parseInt(str));
        message.setData(bundle);
    }

    @Override // com.netfeige.filemanager.IFileManager
    public void compressFolder() {
        this.m_strDocumentCount = "-1";
        this.m_strApkCount = "-1";
        this.m_strAudioCount = "-1";
        this.m_strVideoCount = "-1";
        this.m_strImageCount = "-1";
        new compressFolderThread().start();
    }

    private class compressFolderThread extends Thread {
        private compressFolderThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (Global.g_whatFolder == Category.image) {
                basicTraverseFeige();
                basicTraverse(FileManager.this.m_vecFiltratePathForNull);
            } else if (Global.g_whatFolder == Category.audio) {
                basicTraverseFeige();
                basicTraverse(FileManager.this.m_vecFiltratePathForNull);
            } else if (Global.g_whatFolder == Category.video) {
                basicTraverseFeige();
                basicTraverse(FileManager.this.m_vecFiltratePathForNull);
            } else {
                basicTraverseFeige();
                basicTraverse(FileManager.this.m_vecFiltratePathForNull);
            }
        }

        private void basicTraverse(Vector<String> vector) {
            try {
                ArrayList<SDCardInfo> sDCardInfo = SDCardUtil.getSDCardInfo(FileManager.m_context);
                for (int i = 0; i < sDCardInfo.size(); i++) {
                    if (sDCardInfo.get(i).isMounted()) {
                        FileManager.this.filtrateFolder(sDCardInfo.get(i).getMountPoint(), vector);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = 17;
            if (!(FileManager.m_context instanceof IpmsgActivity)) {
                if (FileManager.m_context instanceof FileActivity) {
                    ((FileActivity) FileManager.m_context).getM_compressHandler().sendMessage(message);
                    return;
                }
                return;
            }
            ((IpmsgActivity) FileManager.m_context).getM_compressHandler().sendMessage(message);
        }

        private void basicTraverseFeige() {
            try {
                FileManager.this.getMyFiles(FileManager.this.m_strFeige, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void basicTraverseImages() {
            try {
                FileManager.this.getMyFiles("/mnt/sdcard2/DCIM/Camera", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                FileManager.this.getMyFiles("/mnt/sdcard/DCIM/Camera", null);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        private void basicTraverseAudios() {
            try {
                FileManager.this.getMyFiles("/mnt/sdcard2/music", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                FileManager.this.getMyFiles("/mnt/sdcard/music", null);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        private void basicTraverseVideos() {
            try {
                FileManager.this.getMyFiles("/mnt/sdcard2/video", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                FileManager.this.getMyFiles("/mnt/sdcard/video", null);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static boolean hasDot(String str) {
        try {
            String strSubstring = str.substring(str.lastIndexOf(ServiceReference.DELIMITER) + 1, str.length());
            if (strSubstring == null || strSubstring == "") {
                return false;
            }
            return strSubstring.charAt(0) == '.';
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean filterSpecifyFolder(String str) {
        File file = new File(str);
        try {
            if (!file.isDirectory()) {
                return false;
            }
            if (!file.isHidden() && !Public_Tools.isEmptyFolder(str)) {
                String name = new File(str).getName();
                if (!name.endsWith("ache") && !name.endsWith("humb") && !name.endsWith("humbnail") && !name.endsWith("encent") && !name.endsWith("Tmp") && !name.endsWith("emp") && !name.endsWith("offline")) {
                    if (!name.endsWith("tmp")) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getFileSize(File file) throws Exception {
        if (!file.exists()) {
            return 0L;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        long jAvailable = fileInputStream.available();
        fileInputStream.close();
        return jAvailable;
    }

    public static long getFolderSize(File file) throws Exception {
        long length;
        File[] fileArrListFiles = file.listFiles();
        long j = 0;
        for (int i = 0; i < fileArrListFiles.length; i++) {
            if (fileArrListFiles[i].isDirectory()) {
                length = getFolderSize(fileArrListFiles[i]);
            } else {
                length = fileArrListFiles[i].length();
            }
            j += length;
        }
        return j;
    }

    public static void copyfile(File file, File file2, int i, Handler handler) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            byte[] bArr = new byte[65536];
            while (true) {
                int i2 = fileInputStream.read(bArr);
                if (i2 != -1) {
                    fileOutputStream.write(bArr, 0, i2);
                    handler.sendEmptyMessage(i);
                } else {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileInputStream.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static long getAvailableInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public static long getTotalInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
    }

    public static long getAvailableExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return -1L;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public static long getTotalExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return -1L;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
    }

    public static long getAvailableExternalMemory2Size() {
        if (!new File("/mnt/sdcard2").exists()) {
            return -1L;
        }
        StatFs statFs = new StatFs(new File("/mnt/sdcard2").getPath());
        return ((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks());
    }

    @Override // com.netfeige.filemanager.IFileManager
    public void addMyShareRecord(String str, Context context, Vector<String> vector) {
        new AddMyShareRecordThread(str, context, vector).start();
    }

    private class AddMyShareRecordThread extends Thread {
        private Context m_context;
        private String m_strMacs;
        private Vector<String> m_waitSharefiles;

        public AddMyShareRecordThread(String str, Context context, Vector<String> vector) {
            this.m_strMacs = "";
            this.m_strMacs = str;
            this.m_context = context;
            this.m_waitSharefiles = vector;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            for (int i = 0; i < this.m_waitSharefiles.size(); i++) {
                ArrayList<ShareFiles> shareFilesRecord = DBHelper.getInstance(this.m_context).getShareFilesRecord("Path = ?", new String[]{this.m_waitSharefiles.get(i)});
                if (shareFilesRecord.isEmpty()) {
                    File file = new File(this.m_waitSharefiles.get(i));
                    shareFilesRecord.add(new ShareFiles(-1, new Date().getTime() / 1000, file.isFile() ? 6 : 1, Public_Tools.getFileSize(file), file.getName(), file.getPath(), this.m_strMacs));
                    DBHelper.getInstance(this.m_context).insertShareFilesRecord(shareFilesRecord);
                } else {
                    ShareFiles shareFiles = shareFilesRecord.get(0);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Size", Long.valueOf(Public_Tools.getFileSize(this.m_waitSharefiles.get(i))));
                    contentValues.put("MAClist", this.m_strMacs);
                    DBHelper.getInstance(this.m_context).updateShareFilesRecord(contentValues, "ID = ?", new String[]{String.valueOf(shareFiles.getM_iID())});
                }
            }
            if (this.m_waitSharefiles.size() > 0) {
                Message message = new Message();
                message.what = 0;
                ((MyShareActivity) this.m_context).getM_handler().sendMessage(message);
            }
        }
    }
}

