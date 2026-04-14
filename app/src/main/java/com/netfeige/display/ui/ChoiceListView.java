package com.netfeige.display.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.FileInfo;
import com.netfeige.common.Global;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SDCardInfo;
import com.netfeige.display.data.FileAdapter;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.filemanager.AsyncLoadedImage;
import com.netfeige.filemanager.Catalogue;
import com.netfeige.filemanager.FileManager;
import com.netfeige.util.SDCardUtil;
import java.io.File;
import java.util.ArrayList;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class ChoiceListView extends ListView {
    private Context m_context;
    private float m_fFlingDistance;
    private float m_fFlingHeight;
    private FileActivity m_fileActivity;
    private FileAdapter m_fileAdapter;
    private GestureDetector m_gestureDetector;
    private int m_nCurrentCount;
    public int m_nCurrentFileIndex;
    private int m_nFirst;
    private final int m_nHeightOff;
    private int m_nStartX;
    private int m_nStartY;
    private final int m_nWidthOff;
    private String m_strCurrentInFileActivityPath;
    private String m_strCurrentPath;
    private final String m_strFeigePath;

    public ChoiceListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.m_context = null;
        this.m_strCurrentPath = ServiceReference.DELIMITER;
        this.m_strCurrentInFileActivityPath = ServiceReference.DELIMITER;
        this.m_nCurrentFileIndex = -1;
        this.m_strFeigePath = "/mnt/sdcard/FeigeDownload/";
        this.m_nFirst = 0;
        this.m_nCurrentCount = 9;
        this.m_nWidthOff = 75;
        this.m_nHeightOff = 350;
        this.m_fFlingDistance = 50.0f;
        this.m_fFlingHeight = 50.0f;
        this.m_context = context;
        this.m_fileActivity = (FileActivity) context;
        FileAdapter fileAdapter = new FileAdapter(context);
        this.m_fileAdapter = fileAdapter;
        setAdapter((ListAdapter) fileAdapter);
        setLongClickable(true);
        this.m_gestureDetector = new GestureDetector(context, new ListViewGestureListener());
        setOnTouchListener(new View.OnTouchListener() { // from class: com.netfeige.display.ui.ChoiceListView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return ChoiceListView.this.m_gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    public String getM_strCurrentInFileActivityPath() {
        return this.m_strCurrentInFileActivityPath;
    }

    public void setM_strCurrentInFileActivityPath(String str) {
        this.m_strCurrentInFileActivityPath = str;
    }

    public FileAdapter getFileAdapter() {
        return this.m_fileAdapter;
    }

    public String getCurrentPath() {
        return this.m_strCurrentPath;
    }

    public int getStartX() {
        return this.m_nStartX;
    }

    public int getStartY() {
        return this.m_nStartY;
    }

    public void goneText() {
        ((TextView) ((FileActivity) this.m_context).findViewById(R.id.show_text)).setVisibility(8);
    }

    public void moveTo(String str) throws Exception {
        this.m_strCurrentPath = str;
        this.m_nCurrentFileIndex = -1;
        if (!str.endsWith(File.separator)) {
            this.m_strCurrentPath += File.separator;
        }
        File[] fileArrListFiles = new File(this.m_strCurrentPath).listFiles();
        setAdapter((ListAdapter) this.m_fileAdapter);
        this.m_fileAdapter.fileList.clear();
        if (fileArrListFiles == null || (fileArrListFiles.length) <= 0) {
            showText();
        } else {
            for (File file : fileArrListFiles) {
                Public_Tools.path2FileInfo(file.getAbsolutePath());
            }
        }
        setSelection(0);
        Public_Tools.fileCompare(this.m_fileAdapter.getFileList());
        this.m_fileAdapter.notifyDataSetChanged();
    }

    public void loadingImage(int i, int i2, Context context) {
        new AsyncLoadedImage(i, i2, context).execute(new Object[0]);
    }

    public void moveToFileActivity(String str) throws Exception {
        moveToBasic(str, null);
        FileActivity.m_textVPath.setText(this.m_strCurrentInFileActivityPath);
        if (!Catalogue.inFileActivityStack.isEmpty()) {
            FileActivity.m_hashMapKeepPosition.put(Catalogue.inFileActivityStack.peek(), Integer.valueOf(FileActivity.s_nCurrentPosition));
        }
        Catalogue.inFileActivityStack.push(this.m_strCurrentInFileActivityPath);
    }

    public void moveToFileActivity(String str, String str2) throws Exception {
        moveToBasic(str, str2);
        if (Global.g_whatFolder == Category.feige) {
            FileActivity.m_textVPath.setText("FeigeDownload/");
        } else {
            FileActivity.m_textVPath.setText(str2);
        }
        Catalogue.inFileActivityStack.push(str2);
    }

    private void moveToBasic(String str, String str2) {
        Global.g_isRoot = false;
        if (Global.g_nCompressed == 1) {
            this.m_fileActivity.getM_compress_progressBar().setVisibility(8);
        }
        FileActivity.s_btnSend.setVisibility(0);
        if (str == null && str2 != null && str2.equals("sdcard/")) {
            enterSDTopDir();
            return;
        }
        this.m_strCurrentInFileActivityPath = str;
        if (!str.endsWith(File.separator)) {
            this.m_strCurrentInFileActivityPath += File.separator;
        }
        addDataForFileActivity(new File(this.m_strCurrentInFileActivityPath), this.m_strCurrentInFileActivityPath, str2);
    }

    private void enterSDTopDir() {
        Global.g_bInFeige = false;
        this.m_fileAdapter.getFileList().clear();
        ImageAdapter.s_imageList.clear();
        IpmsgApplication.g_arrMusicList.clear();
        IpmsgApplication.g_arrVideoList.clear();
        Global.g_bIsClearImageList = true;
        Global.g_bIsClearAudioList = true;
        Public_Tools.backForward(this.m_fileAdapter.getFileList());
        ArrayList<SDCardInfo> sDCardInfo = SDCardUtil.getSDCardInfo(this.m_context);
        for (int i = 0; i < sDCardInfo.size(); i++) {
            if (sDCardInfo.get(i).isMounted()) {
                File file = new File(sDCardInfo.get(i).getMountPoint());
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFloder(file.isDirectory());
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getAbsolutePath());
                fileInfo.setSize(file.length());
                fileInfo.setLastTime(file.lastModified());
                this.m_fileAdapter.getFileList().add(fileInfo);
            }
        }
        this.m_fileAdapter.notifyDataSetChanged();
        Global.g_chiocePaths.clear();
        FileActivity.s_btnSend.setTextColor(this.m_context.getResources().getColor(R.color.textcolor3));
        FileActivity.s_btnSend.setEnabled(false);
        Global.g_bIsEnable = false;
    }

    public boolean moveBackShowFileActivity() throws Exception {
        Global.g_isAsyncLoadedImage = true;
        if (Global.g_nCompressed == 1) {
            this.m_fileActivity.getM_compress_progressBar().setVisibility(8);
        }
        goneText();
        if (Catalogue.inFileActivityStack.isEmpty()) {
            return false;
        }
        String strPop = Catalogue.inFileActivityStack.pop();
        FileActivity.m_hashMapKeepPosition.put(strPop, Integer.valueOf(FileActivity.s_nCurrentPosition));
        if (strPop.equals("img/")) {
            backSet();
            return true;
        }
        if (strPop.equals("audio/")) {
            backSet();
            return true;
        }
        if (strPop.equals("video/")) {
            backSet();
            return true;
        }
        if (strPop.equals("apk/")) {
            backSet();
            return true;
        }
        if (strPop.equals("document/")) {
            backSet();
            return true;
        }
        if (strPop.equals(ServiceReference.DELIMITER)) {
            backSet();
            return true;
        }
        if (strPop.equals("sdcard/")) {
            backSet();
            return true;
        }
        if (strPop.equals("feige/")) {
            backSet();
            return true;
        }
        moveBackFileActivity(Catalogue.inFileActivityStack.peek());
        return true;
    }

    private void backSet() {
        this.m_fileActivity.onBackPressed();
        Global.g_whatFolder = null;
        FileActivity.s_btnSend.setTextColor(this.m_context.getResources().getColor(R.color.textcolor3));
        FileActivity.s_btnSend.setEnabled(false);
        FileActivity.s_btnSend.setVisibility(8);
        Global.g_chiocePaths.clear();
    }

    private boolean moveBackFileActivity(String str) throws Exception {
        try {
            this.m_strCurrentInFileActivityPath = str;
            FileActivity.m_textVPath.setText(str);
            if (!this.m_strCurrentInFileActivityPath.endsWith(File.separator)) {
                this.m_strCurrentInFileActivityPath += File.separator;
            }
            if (this.m_strCurrentInFileActivityPath.equals("img/")) {
                backInFileActivity(FileManager.m_imageFolderList, "img/");
            } else if (this.m_strCurrentInFileActivityPath.equals("audio/")) {
                backInFileActivity(FileManager.m_audioFolderList, "audio/");
            } else if (this.m_strCurrentInFileActivityPath.equals("video/")) {
                backInFileActivity(FileManager.m_videoFolderList, "video/");
            } else if (this.m_strCurrentInFileActivityPath.equals("document/")) {
                backInFileActivity(FileManager.m_documentFolderList, "document/");
            } else if (this.m_strCurrentInFileActivityPath.equals("apk/")) {
                backInFileActivity(FileManager.m_apkFolderList, "apk/");
            } else if (this.m_strCurrentInFileActivityPath.equals("feige/")) {
                File file = new File("/mnt/sdcard/FeigeDownload/");
                FileActivity.m_textVPath.setText("FeigeDownload/");
                addDataForFileActivity(file, this.m_strCurrentInFileActivityPath, null);
            } else if (this.m_strCurrentInFileActivityPath.equals("sdcard/")) {
                enterSDTopDir();
            } else {
                if (this.m_strCurrentInFileActivityPath.equals("/mnt/sdcard/FeigeDownload/")) {
                    Global.g_bInFeige = true;
                } else {
                    Global.g_bInFeige = false;
                }
                addDataForFileActivity(new File(this.m_strCurrentInFileActivityPath), this.m_strCurrentInFileActivityPath, null);
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private void backInFileActivity(ArrayList<String> arrayList, String str) {
        if (Global.g_nCompressed == 1) {
            this.m_fileActivity.showPregressBar();
        }
        getFileAdapter().getFileList().clear();
        FileActivity.s_btnSend.setTextColor(this.m_context.getResources().getColor(R.color.textcolor3));
        FileActivity.s_btnSend.setEnabled(false);
        Public_Tools.backForward(getFileAdapter().getFileList());
        for (int i = 0; i < arrayList.size(); i++) {
            FileInfo fileInfo = new FileInfo();
            String str2 = arrayList.get(i);
            String folderName = Public_Tools.getFolderName(str2);
            fileInfo.setPath(str2);
            fileInfo.setName(folderName);
            fileInfo.setFloder(true);
            fileInfo.setLastTime(new File(str2).lastModified());
            this.m_fileAdapter.getFileList().add(fileInfo);
            Public_Tools.fileCompare(this.m_fileAdapter.getFileList());
        }
        setAdapter((ListAdapter) this.m_fileAdapter);
        recoverPosition(str);
        getFileAdapter().notifyDataSetChanged();
    }

    private void addData(File file, String str, String str2) {
        File[] fileArrListFiles = file.listFiles();
        this.m_fileAdapter.getFileList().clear();
        ImageAdapter.s_imageList.clear();
        IpmsgApplication.g_arrMusicList.clear();
        IpmsgApplication.g_arrVideoList.clear();
        Global.g_bIsClearImageList = true;
        Global.g_bIsClearAudioList = true;
        Public_Tools.backForward(this.m_fileAdapter.getFileList());
        if (fileArrListFiles != null && fileArrListFiles.length > 0) {
            AddDataInFileActivity(fileArrListFiles, fileArrListFiles.length);
        }
        if (this.m_fileAdapter.getFileList().size() <= 1) {
            showText();
        }
        setAdapter((ListAdapter) this.m_fileAdapter);
        recoverPosition(str, str2);
        this.m_fileAdapter.notifyDataSetChanged();
    }

    private void recoverPosition(String str, String str2) {
        if (str2 != null) {
            recoverPosition(str2);
        } else {
            recoverPosition(str);
        }
    }

    private void recoverPosition(String str) {
        if (FileActivity.m_hashMapKeepPosition.containsKey(str)) {
            int iIntValue = FileActivity.m_hashMapKeepPosition.get(str).intValue();
            setSelection(iIntValue);
            if (Global.g_isAsyncLoadedImage) {
                this.m_nFirst = iIntValue;
                loadingImage(iIntValue, this.m_nCurrentCount + iIntValue, this.m_context);
                return;
            }
            return;
        }
        if (Global.g_isAsyncLoadedImage) {
            this.m_nFirst = 0;
            loadingImage(0, this.m_nCurrentCount + 0, this.m_context);
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.ChoiceListView$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Category;

        static {
            int[] iArr = new int[Category.values().length];
            $SwitchMap$com$netfeige$common$Category = iArr;
            try {
                iArr[Category.image.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.audio.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.video.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.document.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.apk.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private void AddDataInFileActivity(File[] fileArr, int i) {
        try {
            if (Global.g_whatFolder != null) {
                int i2 = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
                int i3 = 0;
                if (i2 == 1) {
                    while (i3 < i) {
                        String name = fileArr[i3].getName();
                        if (Public_Tools.isImageFile(name)) {
                            inFor(fileArr, i3, name);
                        }
                        i3++;
                    }
                    return;
                }
                if (i2 == 2) {
                    while (i3 < i) {
                        String name2 = fileArr[i3].getName();
                        if (Public_Tools.isAudioFile(name2)) {
                            inFor(fileArr, i3, name2);
                        }
                        i3++;
                    }
                    return;
                }
                if (i2 == 3) {
                    while (i3 < i) {
                        String name3 = fileArr[i3].getName();
                        if (Public_Tools.isVideoFile(name3)) {
                            inFor(fileArr, i3, name3);
                        }
                        i3++;
                    }
                    return;
                }
                if (i2 == 4) {
                    while (i3 < i) {
                        String name4 = fileArr[i3].getName();
                        if (Public_Tools.isDocumentFile(name4)) {
                            inFor(fileArr, i3, name4);
                        }
                        i3++;
                    }
                    return;
                }
                if (i2 != 5) {
                    while (i3 < i) {
                        inFor(fileArr, i3, fileArr[i3].getName());
                        i3++;
                    }
                } else {
                    while (i3 < i) {
                        String name5 = fileArr[i3].getName();
                        if (Public_Tools.isApkFile(name5)) {
                            inFor(fileArr, i3, name5);
                        }
                        i3++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inFor(File[] fileArr, int i, String str) {
        if ((fileArr[i].isFile() || !FileManager.hasDot(fileArr[i].getName())) && !fileArr[i].isHidden()) {
            String absolutePath = fileArr[i].getAbsolutePath();
            long jLastModified = fileArr[i].lastModified();
            FileInfo fileInfo = new FileInfo();
            setFileInfo(fileArr, i, str, absolutePath, jLastModified, fileInfo);
            if (Public_Tools.isImageFile(str)) {
                loadImage(str, absolutePath, jLastModified);
            } else if (Public_Tools.isAudioFile(str)) {
                loadMedia(str, absolutePath, jLastModified, IpmsgApplication.g_arrMusicList);
            } else if (Public_Tools.isVideoFile(str)) {
                loadMedia(str, absolutePath, jLastModified, IpmsgApplication.g_arrVideoList);
            }
            this.m_fileAdapter.getFileList().add(fileInfo);
        }
    }

    private void setFileInfo(File[] fileArr, int i, String str, String str2, long j, FileInfo fileInfo) {
        fileInfo.setFloder(fileArr[i].isDirectory());
        fileInfo.setName(str);
        fileInfo.setPath(str2);
        fileInfo.setSize(fileArr[i].length());
        fileInfo.setLastTime(j);
    }

    private void loadImage(String str, String str2, long j) {
        ImageAdapter.s_imageList.add(new ImagePreview(0, str, str2, null, j));
    }

    private void loadMedia(String str, String str2, long j, ArrayList<Music> arrayList) {
        arrayList.add(new Music(0, str, str2, -1L, j));
    }

    private void addDataForFileActivity(File file, String str, String str2) {
        addData(file, str, str2);
        Global.g_chiocePaths.clear();
        FileActivity.s_btnSend.setTextColor(this.m_context.getResources().getColor(R.color.textcolor3));
        FileActivity.s_btnSend.setEnabled(false);
        Global.g_bIsEnable = false;
        Public_Tools.fileCompare(this.m_fileAdapter.getFileList());
        Public_Tools.imageCompare(ImageAdapter.s_imageList);
        Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
        Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
    }

    private void showText() {
        int iDip2px = Public_Tools.dip2px(this.m_context, 75.0f);
        int iDip2px2 = Public_Tools.dip2px(this.m_context, 350.0f);
        TextView textView = (TextView) this.m_fileActivity.findViewById(R.id.show_text);
        textView.setVisibility(0);
        textView.setPadding(iDip2px, iDip2px2, 0, 0);
        textView.bringToFront();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getposition(float f, float f2) {
        int[] iArr = new int[2];
        int i = -1;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).getLocationInWindow(iArr);
            if (f >= iArr[0] && f <= r4.getWidth() + iArr[0] && f2 >= iArr[1] && f2 <= r4.getHeight() + iArr[1]) {
                i = i2;
            }
        }
        return i;
    }

    private class ListViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        private ListViewGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            float rawX = motionEvent.getRawX();
            float rawY = motionEvent.getRawY();
            int i = ChoiceListView.this.getposition(rawX, rawY);
            if (Global.g_isInFileActivity) {
                if (i != 0 || FileActivity.s_nCurrentPosition != 0) {
                    View viewItem = ChoiceListView.this.m_fileActivity.getViewItem(rawX, rawY);
                    if (viewItem != null) {
                        if (Global.g_chiocePaths.size() > 0) {
                            ChoiceListView.this.m_fileAdapter.choiceFile(viewItem.findViewById(R.id.file_select_box_fileitem), ChoiceListView.this.getPositionForView(viewItem));
                        } else {
                            ChoiceListView choiceListView = ChoiceListView.this;
                            choiceListView.clickInFileActivity(viewItem, choiceListView.m_fileActivity);
                        }
                    } else {
                        try {
                            ChoiceListView.this.moveBackShowFileActivity();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Bundle extras = ChoiceListView.this.m_fileActivity.getIntent().getExtras();
                        if (extras.getString("flag") != null && extras.getString("flag").equals("myshare") && Catalogue.inFileActivityStack.size() <= 1) {
                            return false;
                        }
                        ChoiceListView.this.moveBackShowFileActivity();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            try {
                try {
                    if (motionEvent.getX() - motionEvent2.getX() <= ChoiceListView.this.m_fFlingDistance || Math.abs(motionEvent2.getY() - motionEvent.getY()) >= ChoiceListView.this.m_fFlingHeight) {
                        return false;
                    }
                    try {
                        ChoiceListView.this.moveBackShowFileActivity();
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                } catch (NullPointerException e2) {
                    e2.printStackTrace();
                    return false;
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clickInFileActivity(View view, FileActivity fileActivity) {
        if (view != null) {
            FileInfo fileInfo = this.m_fileAdapter.getFileList().get(FileActivity.s_choiceListView.getPositionForView(view));
            try {
                if (fileInfo.isFloder()) {
                    moveToFileActivity(fileInfo.getPath());
                } else {
                    FileManager.getIFileManager(this.m_context).openFile(new File(fileInfo.getPath()));
                }
                return;
            } catch (ActivityNotFoundException unused) {
                Context context = this.m_context;
                Public_Tools.showToast(context, context.getResources().getString(R.string.activity_not_found), 1);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            moveBackShowFileActivity();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}

