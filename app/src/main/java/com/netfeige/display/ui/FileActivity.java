package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.DBHelper;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.dlna.ContentTree;
import com.netfeige.filemanager.Catalogue;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.service.IpmsgService;
import com.netfeige.util.SDCardUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class FileActivity extends Activity implements IpmsgService.EventHandler {
    public static HashMap<String, Integer> m_hashMapKeepPosition;
    public static TextView m_textVPath;
    public static Button s_btnSend;
    public static ChoiceListView s_choiceListView;
    public static int s_nCurrentPosition;
    public static Category s_whatFolder;
    private Button m_btnBack;
    private ProgressBar m_compress_progressBar;
    private IpmsgApplication m_ipmsgApp;
    private int m_nFirst;
    private String m_strCheckCompress;
    private int m_nCurrentCount = 9;
    private DBHelper m_dbHelper = null;
    private final int m_nImageMsgWath = 10;
    private final int m_nAudioMsgWath = 11;
    private final int m_nVideoMsgWath = 12;
    private final int m_nDocumentMsgWath = 13;
    private final int m_nApkMsgWath = 14;
    private final int m_nCompressStopMsgWath = 17;
    private IDataConfig m_iDataConfig = null;
    private final String m_strCompressed = ContentTree.VIDEO_ID;
    private final int m_nFirstItem = 1;
    private final int m_nInCatalogue = 2;
    private String m_strFlag = "sendfile";
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.FileActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                FileActivity.s_choiceListView.moveTo(FileActivity.s_choiceListView.getCurrentPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.handleMessage(message);
        }
    };
    private Handler m_compressHandler = new Handler() { // from class: com.netfeige.display.ui.FileActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (Global.g_nBrowseMethod == 0) {
                FileActivity.this.updateShowAll(message, i);
            } else {
                FileActivity.this.updateCatalogue(message, i);
            }
        }
    };

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str, boolean z) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileMessage(String str, MsgRecord msgRecord) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyMessage(HostInformation hostInformation, MsgRecord msgRecord) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) throws Throwable {
        super.onCreate(bundle);
        setContentView(R.layout.file);
        initBasic();
        initControl();
        initData();
    }

    private void initBasic() {
        try {
            this.m_iDataConfig = DataConfig.getInstance(getApplicationContext());
            m_hashMapKeepPosition = new HashMap<>();
            this.m_dbHelper = DBHelper.getInstance(this);
            IpmsgApplication ipmsgApplication = (IpmsgApplication) getApplication();
            this.m_ipmsgApp = ipmsgApplication;
            ipmsgApplication.ipmsgService.ehList.add(this);
            Bundle extras = getIntent().getExtras();
            if (extras.getString("flag") != null) {
                this.m_strFlag = extras.getString("flag");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.FileActivity$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
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
            try {
                $SwitchMap$com$netfeige$common$Category[Category.feige.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.memory.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.sdcard.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    private void initData() throws Throwable {
        try {
            switch (AnonymousClass3.$SwitchMap$com$netfeige$common$Category[s_whatFolder.ordinal()]) {
                case 1:
                    clickCatalogue(true, Category.image, FileManager.m_imageFolderList, "img/");
                    loadingImage();
                    break;
                case 2:
                    clickCatalogue(false, Category.audio, FileManager.m_audioFolderList, "audio/");
                    break;
                case 3:
                    clickCatalogue(false, Category.video, FileManager.m_videoFolderList, "video/");
                    break;
                case 4:
                    clickCatalogue(false, Category.document, FileManager.m_documentFolderList, "document/");
                    break;
                case 5:
                    clickCatalogue(true, Category.apk, FileManager.m_apkFolderList, "apk/");
                    break;
                case 6:
                    clickNoCompress(Category.feige, Public_Tools.getDefaultDownloadPath(), "feige/");
                    break;
                case 7:
                    clickNoCompress(Category.memory, ServiceReference.DELIMITER, ServiceReference.DELIMITER);
                    break;
                case 8:
                    clickNoCompress(Category.sdcard, null, "sdcard/");
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void afterFileLoaded(String str) {
        Public_Tools.fileCompare(s_choiceListView.getFileAdapter().getFileList());
        ChoiceListView choiceListView = s_choiceListView;
        choiceListView.setAdapter((ListAdapter) choiceListView.getFileAdapter());
        Global.g_listViewCurrentAdpter = 2;
        Catalogue.inFileActivityStack.push(str);
        recoverPosition(str);
        m_textVPath.setText(str);
    }

    private void startCompress() {
        FileManager.getIFileManager(this).compressFolder();
        Global.g_nCompressed = 1;
        showPregressBar();
    }

    private void clickCatalogue(boolean z, Category category, ArrayList<String> arrayList, String str) {
        Global.g_isAsyncLoadedImage = z;
        s_choiceListView.getFileAdapter().getFileList().clear();
        Public_Tools.backForward(s_choiceListView.getFileAdapter().getFileList());
        Global.g_whatFolder = category;
        s_btnSend.setVisibility(0);
        String strRead = DataConfig.getInstance(getApplicationContext()).Read(28);
        this.m_strCheckCompress = strRead;
        if (strRead == ContentTree.ROOT_ID) {
            if (Global.g_nCompressed != 1) {
                startCompress();
            } else {
                loadFolder2(arrayList, s_choiceListView);
                showPregressBar();
            }
        } else {
            if (Global.g_nBrowseMethod == 1) {
                loadFolder(arrayList, s_choiceListView);
            } else if (Global.g_nBrowseMethod == 0) {
                allBrowse(arrayList, category, s_choiceListView);
            }
            if (Global.g_nCompressed == 1) {
                showPregressBar();
            }
        }
        afterFileLoaded(str);
    }

    private void loadFolder(ArrayList<String> arrayList, ChoiceListView choiceListView) {
        for (int i = 0; i < arrayList.size(); i++) {
            Public_Tools.path2FileInfoForChoice(arrayList.get(i), choiceListView);
        }
    }

    private void loadFolder2(ArrayList<String> arrayList, ChoiceListView choiceListView) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (Global.g_nBrowseMethod == 0) {
                File file = new File(arrayList.get(i));
                if (file.exists() && file.isDirectory()) {
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        specialFilling(Global.g_whatFolder, fileArrListFiles, i2, choiceListView);
                    }
                }
            } else {
                Public_Tools.path2FileInfoForChoice(arrayList.get(i), choiceListView);
            }
        }
    }

    private void clickNoCompress(Category category, String str, String str2) {
        if (str2.equals("sdcard/") && SDCardUtil.getSDCardInfo(this).isEmpty()) {
            Toast.makeText(this, "鏃燬D鍗★紒", 0).show();
            return;
        }
        Global.g_whatFolder = category;
        Global.g_listViewCurrentAdpter = 2;
        try {
            s_choiceListView.moveToFileActivity(str, str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        s_choiceListView.setM_strCurrentInFileActivityPath(str);
    }

    private void recoverPosition(String str) {
        if (getHashMapKeepPosition().containsKey(str)) {
            int iIntValue = getHashMapKeepPosition().get(str).intValue();
            s_choiceListView.setSelection(iIntValue);
            this.m_nFirst = iIntValue;
            s_choiceListView.loadingImage(iIntValue, this.m_nCurrentCount + iIntValue, this);
            return;
        }
        this.m_nFirst = 0;
        s_choiceListView.loadingImage(0, this.m_nCurrentCount + 0, this);
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.back_file);
        this.m_btnBack = button;
        button.setOnClickListener(new BackBtnClickListener());
        s_btnSend = (Button) findViewById(R.id.send_btn_file);
        if (this.m_strFlag.equals("myshare")) {
            s_btnSend.setText("下一步");
        }
        s_btnSend.setOnClickListener(new SendBtnClickListener());
        TextView textView = (TextView) findViewById(R.id.path_text_file);
        m_textVPath = textView;
        textView.setText(R.string.choice_send_file);
        ChoiceListView choiceListView = (ChoiceListView) findViewById(R.id.files_listView_file);
        s_choiceListView = choiceListView;
        choiceListView.setAdapter((ListAdapter) choiceListView.getFileAdapter());
        s_choiceListView.setOnScrollListener(new ListViewScrollListen());
        this.m_compress_progressBar = (ProgressBar) findViewById(R.id.compress_progressBar);
    }

    private class BackBtnClickListener implements View.OnClickListener {
        private BackBtnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                if (FileActivity.s_choiceListView.moveBackShowFileActivity()) {
                    return;
                }
                if (Global.g_nCompressed == 1) {
                    FileActivity.this.m_compress_progressBar.setVisibility(8);
                }
                FileActivity.this.onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SendBtnClickListener implements View.OnClickListener {
        private SendBtnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (Global.g_bIsEnable) {
                FileActivity.this.m_ipmsgApp.waitSendfiles.clear();
                for (int i = 0; i < FileActivity.s_choiceListView.getFileAdapter().fileList.size(); i++) {
                    if (FileActivity.s_choiceListView.getFileAdapter().fileList.get(i).isCheck) {
                        FileActivity.this.m_ipmsgApp.waitSendfiles.add(FileActivity.s_choiceListView.getFileAdapter().fileList.get(i).path);
                    }
                }
                if (FileActivity.this.m_ipmsgApp.waitSendfiles.size() > 0) {
                    FileActivity.s_whatFolder = Global.g_whatFolder;
                    Global.g_chiocePaths.clear();
                    FileActivity.this.onBackPressed();
                } else {
                    FileActivity fileActivity = FileActivity.this;
                    Public_Tools.showToast(fileActivity, fileActivity.getResources().getString(R.string.send_file_prompt), 0);
                }
            }
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_ipmsgApp.currentActivity = this;
        if (this.m_ipmsgApp.g_bBackRuning) {
            this.m_ipmsgApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_ipmsgApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.m_ipmsgApp.currentActivity == this) {
            this.m_ipmsgApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_ipmsgApp, getString(R.string.app_name), DataConfig.getInstance(this).Read(0), R.drawable.ic_launcher);
        }
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.m_ipmsgApp.ipmsgService.ehList.remove(this);
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            Global.g_whatFolder = null;
            Global.g_chiocePaths.clear();
            s_btnSend.setTextColor(getResources().getColor(R.color.textcolor3));
            s_btnSend.setEnabled(false);
            onBackPressed();
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Global.g_isInFileActivity = false;
        Catalogue.inFileActivityStack.clear();
        super.onBackPressed();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public View getViewItem(float f, float f2) {
        int[] iArr = new int[2];
        View view = null;
        for (int i = 0; i < s_choiceListView.getChildCount(); i++) {
            View childAt = s_choiceListView.getChildAt(i);
            childAt.getLocationInWindow(iArr);
            if (f >= iArr[0] && f <= childAt.getWidth() + iArr[0] && f2 >= iArr[1] && f2 <= childAt.getHeight() + iArr[1]) {
                view = childAt;
            }
        }
        return view;
    }

    public ChoiceListView getFileListView() {
        return s_choiceListView;
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str) {
        Message.obtain(this.handler).sendToTarget();
    }

    private class ListViewScrollListen implements AbsListView.OnScrollListener {
        private ListViewScrollListen() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            FileActivity.s_nCurrentPosition = i;
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 0 && Global.g_isAsyncLoadedImage) {
                FileActivity.this.loadingImage();
            }
        }
    }

    public void loadingImage() {
        ChoiceListView choiceListView = s_choiceListView;
        int i = s_nCurrentPosition;
        choiceListView.loadingImage(i, this.m_nCurrentCount + i, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCatalogue(Message message, int i) {
        if (i != 17) {
            switch (i) {
                case 10:
                    compressedUpdateCategory(message, "imageState", FileManager.m_imageFolderList, Category.image);
                    break;
                case 11:
                    compressedUpdateCategory(message, "audioState", FileManager.m_audioFolderList, Category.audio);
                    break;
                case 12:
                    compressedUpdateCategory(message, "videoState", FileManager.m_videoFolderList, Category.video);
                    break;
                case 13:
                    compressedUpdateCategory(message, "documentState", FileManager.m_documentFolderList, Category.document);
                    break;
                case 14:
                    compressedUpdateCategory(message, "apkState", FileManager.m_apkFolderList, Category.apk);
                    break;
            }
        }
        compressStop();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateShowAll(Message message, int i) {
        if (i != 17) {
            switch (i) {
                case 10:
                    compressedUpdateCategory(message, "imageState", FileManager.m_imageList, Category.image);
                    break;
                case 11:
                    compressedUpdateCategory(message, "audioState", FileManager.m_audioList, Category.audio);
                    break;
                case 12:
                    compressedUpdateCategory(message, "videoState", FileManager.m_videoList, Category.video);
                    break;
                case 13:
                    compressedUpdateCategory(message, "documentState", FileManager.m_documentList, Category.document);
                    break;
                case 14:
                    compressedUpdateCategory(message, "apkState", FileManager.m_apkList, Category.apk);
                    break;
            }
        }
        compressStop();
    }

    private void compressStop() {
        Global.g_nCompressed = 2;
        this.m_compress_progressBar.setVisibility(8);
        Public_Tools.fileCompare(s_choiceListView.getFileAdapter().getFileList());
        s_choiceListView.getFileAdapter().notifyDataSetChanged();
        Public_Tools.writeSqlite(this.m_dbHelper);
        if (Global.g_nBrowseMethod == 0) {
            FileManager.m_imageList.clear();
            FileManager.m_audioList.clear();
            FileManager.m_videoList.clear();
            FileManager.m_documentList.clear();
            FileManager.m_apkList.clear();
        }
        this.m_iDataConfig.Write(28, ContentTree.VIDEO_ID);
        if (Global.g_isAsyncLoadedImage) {
            s_choiceListView.loadingImage(1, this.m_nCurrentCount, this);
        }
    }

    public Handler getM_compressHandler() {
        return this.m_compressHandler;
    }

    public ProgressBar getM_compress_progressBar() {
        return this.m_compress_progressBar;
    }

    public void showPregressBar() {
        this.m_compress_progressBar.setVisibility(0);
        this.m_compress_progressBar.bringToFront();
    }

    private void compressedUpdateCategory(Message message, String str, ArrayList<String> arrayList, Category category) {
        try {
            int i = message.getData().getInt(str);
            if (Global.g_whatFolder == category) {
                Public_Tools.path2FileInfoForChoice(arrayList.get(i), s_choiceListView);
                s_choiceListView.getFileAdapter().notifyDataSetChanged();
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void allBrowse(ArrayList<String> arrayList, Category category, ChoiceListView choiceListView) {
        try {
            ImageAdapter.s_imageList.clear();
            IpmsgApplication.g_arrMusicList.clear();
            IpmsgApplication.g_arrVideoList.clear();
            choiceListView.getFileAdapter().getFileList().clear();
            Global.g_bIsClearImageList = true;
            Global.g_bIsClearAudioList = true;
            Public_Tools.backForward(choiceListView.getFileAdapter().getFileList());
            for (int i = 0; i < arrayList.size(); i++) {
                File file = new File(arrayList.get(i));
                if (file.exists() && file.isDirectory()) {
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        specialFilling(category, fileArrListFiles, i2, choiceListView);
                    }
                }
            }
            Public_Tools.imageCompare(ImageAdapter.s_imageList);
            Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
            Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
        } catch (NullPointerException | Exception unused) {
        }
    }

    public void specialFilling(Category category, File[] fileArr, int i, ChoiceListView choiceListView) {
        if (fileArr[i].isHidden()) {
            return;
        }
        int i2 = AnonymousClass3.$SwitchMap$com$netfeige$common$Category[category.ordinal()];
        if (i2 == 1) {
            if (Public_Tools.isImageFile(fileArr[i].getName())) {
                addData(fileArr, i, choiceListView);
                ImageAdapter.s_imageList.add(new ImagePreview(0, fileArr[i].getName(), fileArr[i].getAbsolutePath(), null, fileArr[i].lastModified()));
                return;
            }
            return;
        }
        if (i2 == 2) {
            if (Public_Tools.isAudioFile(fileArr[i].getName())) {
                addData(fileArr, i, choiceListView);
                IpmsgApplication.g_arrMusicList.add(new Music(0, fileArr[i].getName(), fileArr[i].getAbsolutePath(), -1L, fileArr[i].lastModified()));
                return;
            }
            return;
        }
        if (i2 == 3) {
            if (Public_Tools.isVideoFile(fileArr[i].getName())) {
                addData(fileArr, i, choiceListView);
                IpmsgApplication.g_arrVideoList.add(new Music(0, fileArr[i].getName(), fileArr[i].getAbsolutePath(), -1L, fileArr[i].lastModified()));
                return;
            }
            return;
        }
        if (i2 == 4) {
            if (Public_Tools.isDocumentFile(fileArr[i].getName())) {
                addData(fileArr, i, choiceListView);
            }
        } else if (i2 == 5 && Public_Tools.isApkFile(fileArr[i].getName())) {
            addData(fileArr, i, choiceListView);
        }
    }

    private void addData(File[] fileArr, int i, ChoiceListView choiceListView) {
        choiceListView.getFileAdapter().getFileList().add(Public_Tools.path2FileInfo(fileArr[i].getAbsolutePath()));
    }

    public HashMap<String, Integer> getHashMapKeepPosition() {
        return m_hashMapKeepPosition;
    }
}

