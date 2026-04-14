package com.netfeige.display.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.broadcast.NetStatusBroadcast;
import com.netfeige.common.Category;
import com.netfeige.common.DBHelper;
import com.netfeige.common.FileInfo;
import com.netfeige.common.FolderInfo;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.IUpdateNotify;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SoundPlayer;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.data.ResourceExpandableListAdapter;
import com.netfeige.display.data.UserAdapter;
import com.netfeige.display.ui.dialog.DeleteDialog;
import com.netfeige.display.ui.dialog.DetailDialog;
import com.netfeige.display.ui.dialog.ExitDialog;
import com.netfeige.display.ui.dialog.ExitWiFiNotifyDialog;
import com.netfeige.display.ui.dialog.NewDialog;
import com.netfeige.display.ui.dialog.RenameDialog;
import com.netfeige.display.ui.dialog.SortDialog;
import com.netfeige.display.ui.dialog.UpdateDialog;
import com.netfeige.display.ui.wifi.WifiMainActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.enums.FileAccessAuth;
import com.netfeige.filemanager.Catalogue;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.protocol.Protocol_Discuss;
import com.netfeige.service.IpmsgService;
import com.netfeige.service.MusicService;
import com.netfeige.util.PermissionsUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class IpmsgActivity extends Activity implements IpmsgService.EventHandler, IpmsgService.EntryBroadcastEventHandler, NetStatusBroadcast.EventHandler, SensorEventListener, IUpdateNotify, IpmsgService.DiscussInviteEventHandler, IpmsgService.DiscussExitEventHandler {
    private static final int MENU_EXIT = 2;
    private static final int MENU_SET = 1;
    private static final int REQUEST_CODE = 1;
    public static PackageManager g_packageManager = null;
    public static WindowManager g_windowManager = null;
    public static UserAdapter m_UserAdapter = null;
    private static DBHelper m_dbHelper = null;
    private static final int m_nUpdateDialog = 2;
    public static PopupWindow m_popupWindowSend;
    public static ResourceExpandableListAdapter m_resourceExpandableListAdapter;
    public static FileListView s_fileListView;
    public static ImageButton s_imageBtnBackgroundPlay;
    public static int s_nCurrentposition;
    private Button mBtnDiscuss;
    private ImageView mImgDiscussPrompt;
    private ExpandableListView m_ExpandableLVResource;
    private Animation m_animation;
    private Button m_btnCancel;
    public Button m_btnDragbar;
    private GestureDetector m_btnGestureDetector;
    private Button m_btnMore;
    private Button m_btnParentFolder;
    private Button m_btnPaste;
    private Button m_btnPopupCancel;
    private Button m_btnPopupCopy;
    private Button m_btnPopupCut;
    private Button m_btnPopupDelete;
    private Button m_btnPopupDetail;
    private Button m_btnPopupPrint;
    private Button m_btnPopupRename;
    private Button m_btnPopupSend;
    private Button m_btnPopupShare;
    private Button m_btnPopupShowTo;
    private Button m_btnRefresh;
    private Button m_btnSetting;
    private Button m_btnWT;
    private ProgressBar m_compress_progressBar;
    private DetailDialog m_detailDialog;
    private DeleteDialog m_dialogDeleteNotify;
    private DragEnum m_dragEnum;
    private GestureDetector m_gridVGestureDetector;
    public CustomGridView m_gridVUsers;
    private HashMap<String, Integer> m_hashMapKeepPosition;
    private HorizontalScrollView m_horizontalScrollView;
    private ImageButton m_imageBtnForLayout;
    private ImageView m_imageVSwitch;
    private IpmsgApplication m_ipmsgApp;
    private LinearLayout m_linearLBackRoot;
    private LinearLayout m_linearLDragBar;
    private LinearLayout m_linearLNewBuild;
    private LinearLayout m_linearLRefresh;
    private LinearLayout m_linearLSetupWifi;
    private LinearLayout m_linearLShowFiles;
    private LinearLayout m_linearLSort;
    private LinearLayout m_linearLSort2;
    private LinearLayout m_linearLTopNavigation;
    private int m_nMusic;
    private int m_nPopX;
    private int m_nTerminateMusic;
    private PopupWindow m_popupWindowBrowser;
    private PopupWindow m_popupWindowDomain;
    private PopupWindow m_popupWindowPaste;
    private Rect m_rectWindowArea;
    private SensorManager m_sensorManager;
    private SoundPool m_soundPool;
    private TextView m_textVContent;
    private TextView m_textVNumber;
    private TextView m_textVSetupWifi;
    private TextView m_textVSwitch;
    private TextView m_textVUseWt;
    private UpdateDialog m_updateDialog;
    private View m_viewFramel;
    private final int m_nDialogSortId = 1;
    private final int m_nDragCategory = 0;
    private final int m_nUsersCategory = 1;
    private final int m_nMsgCategory = 2;
    private final int m_nStartupCategory = 3;
    private final int m_nFileCategory = 4;
    private final int m_nWifiCategory = 5;
    private final int m_nUpdateCategory = 6;
    private final int m_nAnimationCategory = 7;
    private final int m_nListModifyUsers = 0;
    private final int m_nListAddMsg = 1;
    private final int m_nDiscussMsg = 2;
    private Category m_keepWhatFolder = null;
    private DragProcess m_dragProcess = new DragProcess();
    private SoundPlayer m_player = null;
    private ImageView m_imageVAnimationPic = null;
    private int m_nDiffNum = -1;
    private int m_nSelectedX = 0;
    private int m_nSelectedY = 0;
    private TextView m_TextViewContext = null;
    private Date lastClickDate = null;
    private final int m_nContext_Menu_Cut = 1;
    private final int m_nContext_Menu_Copy = 2;
    private final int m_nContext_Menu_Delete = 4;
    private final int m_nContext_Menu_Rename = 5;
    private final int m_nContext_Menu_Detail = 6;
    private final int m_nContext_Menu_Print = 7;
    private final int m_nContext_Menu_Send = 8;
    private final int m_nInCatalogue = 2;
    private boolean m_isSensor = false;
    private int m_nCurrentCount = 9;
    private AlertDialog.Builder m_compressBuilder = null;
    private LayoutInflater m_inflater = null;
    private File m_file = null;
    private final int m_nPopUpX = 5;
    private final int m_nImageMsgWath = 10;
    private final int m_nAudioMsgWath = 11;
    private final int m_nVideoMsgWath = 12;
    private final int m_nDocumentMsgWath = 13;
    private final int m_nApkMsgWath = 14;
    private final int m_nCompressStopMsgWath = 17;
    private final String m_strCompressed = ContentTree.VIDEO_ID;
    private SortDialog m_dialogSort = null;
    private int m_nFirstItem = 1;
    private AlertDialog.Builder m_dialogPrompt = null;
    private IDataConfig m_iDataConfig = null;
    private final Handler operateContextMenuhandler = new Handler(Looper.getMainLooper()) { // from class: com.netfeige.display.ui.IpmsgActivity.11
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.getData().getInt("state");
            if (i == 4) {
                IpmsgActivity.this.toastShow(4);
            } else {
                if (i != 5) {
                    return;
                }
                IpmsgActivity.this.toastShow(5);
            }
        }
    };
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.IpmsgActivity.13
        /* JADX WARN: Removed duplicated region for block: B:60:0x0165 A[PHI: r1
  0x0165: PHI (r1v23 int) = (r1v16 int), (r1v38 int) binds: [B:77:0x01e7, B:52:0x0131] A[DONT_GENERATE, DONT_INLINE]] */
        @Override // android.os.Handler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void handleMessage(android.os.Message r11) {
            /*
                Method dump skipped, instruction units count: 614
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.ui.IpmsgActivity.AnonymousClass13.handleMessage(android.os.Message):void");
        }
    };
    private ServiceConnection _connection = new ServiceConnection() { // from class: com.netfeige.display.ui.IpmsgActivity.14
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IpmsgActivity.this.m_ipmsgApp.ipmsgService = ((IpmsgService.LocalBinder) iBinder).getService();
            IpmsgActivity.this.initData();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            IpmsgActivity.this.m_ipmsgApp.ipmsgService = null;
        }
    };
    private int m_nAcceleration = 12;
    private int m_nLeft = 10;
    private int m_nSetHeight = 500;
    private int m_nOffset = 26;
    private Handler m_compressHandler = new Handler() { // from class: com.netfeige.display.ui.IpmsgActivity.16
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (Global.g_nBrowseMethod == 0) {
                IpmsgActivity.this.updateShowAll(message, i);
            } else {
                IpmsgActivity.this.updateCatalogue(message, i);
            }
        }
    };
    private Handler PasteHandler = new Handler() { // from class: com.netfeige.display.ui.IpmsgActivity.21
        @Override // android.os.Handler
        public void handleMessage(Message message) throws Throwable {
            int i = message.what;
            if (i == 0) {
                Public_Tools.fileCompare(IpmsgActivity.s_fileListView.getFileAdapter().getFileList());
                IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
                if (message.getData().getBoolean("isRepeat")) {
                    IpmsgActivity.this.showPromptDialog(FileManager.m_strPrompt + "\n");
                } else {
                    IpmsgActivity.this.toast(R.string.pastecompleted);
                }
                IpmsgActivity.this.loadingImage();
                Global.g_bWaitPaste = 0;
                Global.g_pastePaths.clear();
            } else if (i == 1 && message.getData().getBoolean("isPaste")) {
                try {
                    String strPeek = Catalogue.categoryStack.peek();
                    if (strPeek.equals("feige/")) {
                        strPeek = Public_Tools.getDefaultDownloadPath();
                    } else if (strPeek.equals("sdcard/")) {
                        strPeek = Public_Tools.getSDCardPath();
                    }
                    IpmsgActivity.s_fileListView.reLoadCategory(strPeek);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(message);
        }
    };
    private String m_strNewName = null;

    public enum DragEnum {
        TOP,
        MIDDLE_UP,
        MIDDLE_DOWN,
        BOTTOM
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toastShow(int i) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyMessage(HostInformation hostInformation, MsgRecord msgRecord) {
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int i) {
        if (i == 1) {
            return this.m_dialogSort;
        }
        if (i != 2) {
            return null;
        }
        return this.m_updateDialog;
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int i, Dialog dialog) {
        if (i != 1 && i == 2) {
            TextView textVContent = this.m_updateDialog.getTextVContent();
            StringBuilder sb = new StringBuilder();
            sb.append((Object) Html.fromHtml(getString(R.string.version_code_colon) + this.m_ipmsgApp.newVersion + "<br/>" + getString(R.string.new_version_changed_colon)));
            sb.append(this.m_ipmsgApp.newVersionChanged);
            textVContent.setText(sb.toString());
        }
        super.onPrepareDialog(i, dialog);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PermissionsUtils.getInstance().requestPermissions(this, 17);
        setContentView(R.layout.main);
        initBasic();
        initControl();
        startBindService();
    }

    private void initBasic() {
        try {
            g_packageManager = getPackageManager();
            Global.g_bWorking = true;
            Global.g_nStatusHeight = getStatusBarHeight();
            this.m_ipmsgApp = (IpmsgApplication) getApplication();
            this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
            m_dbHelper = DBHelper.getInstance(this);
            this.m_iDataConfig = DataConfig.getInstance(getApplicationContext());
            readSqlite();
            try {
                this.m_ipmsgApp.g_strDelFilePrompt = ContentTree.VIDEO_ID;
                this.m_iDataConfig.Write(7, ContentTree.VIDEO_ID);
                Global.g_nBrowseMethod = Integer.valueOf(this.m_iDataConfig.Read(29)).intValue();
            } catch (NumberFormatException unused) {
                Global.g_nBrowseMethod = 1;
            } catch (Exception unused2) {
                Global.g_nBrowseMethod = 1;
            }
            g_windowManager = getWindowManager();
            initBrowserPopupWindow();
            initDomainPopupWindow();
            initPastePopupWindow();
            initSendPopupWindow();
            this.m_compressBuilder = new AlertDialog.Builder(this);
            this.m_gridVGestureDetector = new GestureDetector(this, new GridViewGestureListener());
            this.m_btnGestureDetector = new GestureDetector(this, new ButtonGestureListener());
            this.m_dragEnum = DragEnum.BOTTOM;
            this.m_sensorManager = (SensorManager) getSystemService("sensor");
            SoundPool soundPool = new SoundPool(10, 1, 5);
            this.m_soundPool = soundPool;
            this.m_nMusic = soundPool.load(this, R.raw.sou, 1);
            this.m_nTerminateMusic = this.m_soundPool.load(this, R.raw.terminate, 1);
            this.m_player = new SoundPlayer();
            this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
            this.m_dialogSort = new SortDialog(this, this.m_inflater);
            this.m_hashMapKeepPosition = new HashMap<>();
            this.m_dialogPrompt = new AlertDialog.Builder(this);
            this.m_updateDialog = new UpdateDialog(this, this.m_ipmsgApp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBindService() {
        this.m_ipmsgApp.g_iUserColumn = 1;
        try {
            if (this.m_ipmsgApp.ipmsgService == null) {
                Intent intent = new Intent(this, (Class<?>) IpmsgService.class);
                startService(intent);
                bindService(intent, this._connection, 1);
            } else {
                initData();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void initControl() {
        initLayout();
        initButton();
        initView();
        initBar();
    }

    private void initBar() {
        this.m_compress_progressBar = (ProgressBar) findViewById(R.id.compress_progressBar);
    }

    private void initView() {
        this.m_ExpandableLVResource = (ExpandableListView) findViewById(R.id.expendlist_main);
        CustomGridView customGridView = (CustomGridView) findViewById(R.id.users_gridView);
        this.m_gridVUsers = customGridView;
        customGridView.setLongClickable(true);
        this.m_gridVUsers.setOnTouchListener(new View.OnTouchListener() { // from class: com.netfeige.display.ui.IpmsgActivity.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return IpmsgActivity.this.m_gridVGestureDetector.onTouchEvent(motionEvent);
            }
        });
        FileListView fileListView = (FileListView) findViewById(R.id.files_listView_main);
        s_fileListView = fileListView;
        fileListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeOnPreDrawListener());
        s_fileListView.setOnScrollListener(new ListViewScrollListen());
        this.m_textVContent = (TextView) findViewById(R.id.show_text);
        this.m_viewFramel = findViewById(R.id.framel_main);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initData() {
        this.m_ipmsgApp.ipmsgService.ehList.add(this);
        this.m_ipmsgApp.ipmsgService.entryBroadcastEHList.add(this);
        this.m_ipmsgApp.ipmsgService.mDiscussInviteEHList.add(this);
        this.m_ipmsgApp.ipmsgService.mDiscussExitEHList.add(this);
        ResourceExpandableListAdapter resourceExpandableListAdapter = new ResourceExpandableListAdapter(this, this.m_ipmsgApp.ipmsgService.m_resourceGroupList, R.layout.grouplist_resource, new String[]{"name_text_grouplist_resource"}, new int[]{R.id.name_text_grouplist_resource}, this.m_ipmsgApp.ipmsgService.m_resourceChildList, R.layout.childlist_resource, new String[]{"name_text_childlist_resource"}, new int[]{R.id.name_text_childlist_resource}, new int[]{R.drawable.myshare, R.drawable.lanshare});
        m_resourceExpandableListAdapter = resourceExpandableListAdapter;
        this.m_ExpandableLVResource.setAdapter(resourceExpandableListAdapter);
        this.m_ExpandableLVResource.expandGroup(1);
        this.m_ExpandableLVResource.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.2
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                if (i == 0 && i2 == 0) {
                    IpmsgActivity.this.startActivity(new Intent(IpmsgActivity.this, (Class<?>) MyShareActivity.class));
                }
                if (i != 0 || i2 != 1) {
                    return false;
                }
                IpmsgActivity.this.startActivity(new Intent(IpmsgActivity.this, (Class<?>) LanShareActivity.class));
                return false;
            }
        });
        UserAdapter userAdapter = new UserAdapter(this, R.layout.useritem, this.m_ipmsgApp.ipmsgService.userList);
        m_UserAdapter = userAdapter;
        this.m_gridVUsers.setAdapter((ListAdapter) userAdapter);
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.update(this);
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.entryService(null, true);
    }

    private class ViewTreeOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private ViewTreeOnPreDrawListener() {
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            IpmsgActivity.this.m_rectWindowArea = new Rect();
            IpmsgActivity.this.getWindow().findViewById(android.R.id.content).getDrawingRect(IpmsgActivity.this.m_rectWindowArea);
            IpmsgActivity.s_fileListView.getWindowVisibleDisplayFrame(IpmsgActivity.this.m_rectWindowArea);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) IpmsgActivity.s_fileListView.getLayoutParams();
            layoutParams.height = 0;
            IpmsgActivity.s_fileListView.setLayoutParams(layoutParams);
            Environment.getExternalStorageState().equals("mounted");
            IpmsgActivity.this.new StartUpProcess().start();
            IpmsgActivity.s_fileListView.getViewTreeObserver().removeOnPreDrawListener(this);
            return true;
        }
    }

    private void initButton() {
        Button button = (Button) findViewById(R.id.refresh_btn);
        this.m_btnRefresh = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.m_btnRefresh.setEnabled(false);
                if (IpmsgActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.isBroadcasting()) {
                    return;
                }
                IpmsgActivity.this.refresh(false);
            }
        });
        Button button2 = (Button) findViewById(R.id.setting_btn);
        this.m_btnSetting = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.startActivity(new Intent(IpmsgActivity.this, (Class<?>) OptionActivity.class));
            }
        });
        Button button3 = (Button) findViewById(R.id.back_file_manage_btn);
        this.m_btnParentFolder = button3;
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    IpmsgActivity.s_fileListView.moveBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button button4 = (Button) findViewById(R.id.discuss_btn_main);
        this.mBtnDiscuss = button4;
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.startActivity(new Intent(IpmsgActivity.this, (Class<?>) DiscussActivity.class));
            }
        });
        this.mImgDiscussPrompt = (ImageView) findViewById(R.id.unread_prompt_img_main);
        Button button5 = (Button) findViewById(R.id.wt_btn_main);
        this.m_btnWT = button5;
        button5.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.startActivity(new Intent(IpmsgActivity.this, (Class<?>) WifiMainActivity.class));
            }
        });
        Button button6 = (Button) findViewById(R.id.more_btn);
        this.m_btnMore = button6;
        button6.setOnClickListener(new BtnMoreOnClickListener());
        this.m_imageBtnForLayout = (ImageButton) findViewById(R.id.backgroundplay2);
        ImageButton imageButton = (ImageButton) findViewById(R.id.backgroundplay);
        s_imageBtnBackgroundPlay = imageButton;
        imageButton.setOnClickListener(new BackgroundPlayOnClickListener());
        Button button7 = (Button) findViewById(R.id.drag_bar_btn);
        this.m_btnDragbar = button7;
        button7.setLongClickable(true);
        this.m_btnDragbar.setOnTouchListener(new BtnDragbarOnTouchListener());
    }

    private class BackgroundPlayOnClickListener implements View.OnClickListener {
        private BackgroundPlayOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.s_imageBtnBackgroundPlay.setVisibility(4);
            Global.g_bBackgroundPlay = false;
            Global.g_bIsFromMianBackBtn = true;
            IpmsgActivity.this.skipMusic();
        }
    }

    private class BtnDragbarOnTouchListener implements View.OnTouchListener {
        private BtnDragbarOnTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0 && Global.g_bIsInEmptyDir) {
                IpmsgActivity ipmsgActivity = IpmsgActivity.this;
                ipmsgActivity.m_TextViewContext = (TextView) ipmsgActivity.findViewById(R.id.show_text);
                IpmsgActivity.this.m_TextViewContext.setVisibility(8);
            }
            return IpmsgActivity.this.m_btnGestureDetector.onTouchEvent(motionEvent);
        }
    }

    private class BtnMoreOnClickListener implements View.OnClickListener {
        private BtnMoreOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (Global.g_whatFolder == Category.image || Global.g_whatFolder == Category.audio || Global.g_whatFolder == Category.video || Global.g_whatFolder == Category.document || Global.g_whatFolder == Category.apk) {
                View viewFindViewById = IpmsgActivity.this.findViewById(R.id.top_navigation_layout);
                IpmsgActivity.this.m_popupWindowBrowser.showAtLocation(viewFindViewById, 53, IpmsgActivity.this.m_nPopX, Global.g_nStatusHeight + viewFindViewById.getHeight());
            } else if (Global.g_whatFolder == Category.feige || Global.g_whatFolder == Category.memory || Global.g_whatFolder == Category.sdcard) {
                View viewFindViewById2 = IpmsgActivity.this.findViewById(R.id.top_navigation_layout);
                IpmsgActivity.this.m_popupWindowDomain.showAtLocation(viewFindViewById2, 53, IpmsgActivity.this.m_nPopX, Global.g_nStatusHeight + viewFindViewById2.getHeight());
            }
        }
    }

    private void initLayout() {
        this.m_linearLSetupWifi = (LinearLayout) findViewById(R.id.setup_wifi_layout_main);
        TextView textView = (TextView) findViewById(R.id.setup_wifi_text_main);
        this.m_textVSetupWifi = textView;
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Public_Tools.entryNetSetting();
            }
        });
        TextView textView2 = (TextView) findViewById(R.id.use_wt_text_main);
        this.m_textVUseWt = textView2;
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.startActivity(new Intent(IpmsgActivity.this, (Class<?>) WifiMainActivity.class));
            }
        });
        this.m_linearLDragBar = (LinearLayout) findViewById(R.id.drag_bar_layout);
        this.m_linearLTopNavigation = (LinearLayout) findViewById(R.id.top_navigation_layout);
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem menuItem) {
        FileInfo fileInfo = (FileInfo) s_fileListView.getFileAdapter().getItem(((AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position);
        if (fileInfo != null) {
            this.m_file = new File(fileInfo.getPath());
        }
        FileAccessAuth fileAccessAuthAuthority = Public_Tools.authority(this.m_file);
        switch (menuItem.getItemId()) {
            case 1:
                if (Global.g_bWaitPaste == 2) {
                    toast(R.string.pasting);
                } else {
                    cut(fileAccessAuthAuthority);
                }
                break;
            case 2:
                if (Global.g_bWaitPaste == 2) {
                    toast(R.string.pasting);
                } else {
                    copy(fileAccessAuthAuthority);
                }
                break;
            case 4:
                delete(fileInfo.getPath(), fileAccessAuthAuthority);
                break;
            case 5:
                longClickRename(fileInfo.getPath(), fileAccessAuthAuthority);
                break;
            case 6:
                detail(this.m_file);
                break;
            case 7:
                print(fileInfo.getPath());
                break;
            case 8:
                send(fileInfo.getPath());
                break;
        }
        return super.onContextItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void send(String str) {
        Intent intent = new Intent(this, (Class<?>) ChoiceUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", str);
        bundle.putString("activity", "IpmsgActivity");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cut(FileAccessAuth fileAccessAuth) {
        if (fileAccessAuth == FileAccessAuth.RW_OK) {
            FileManager.getIFileManager(this).cutFile(this.m_file);
            Global.g_pastePaths.clear();
            Global.g_pastePaths.addAll(Global.g_filePath);
            cancelChoice();
            showPopupWindowPaste(true);
            return;
        }
        toast(R.string.noauthority);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void copy(FileAccessAuth fileAccessAuth) {
        if (fileAccessAuth == FileAccessAuth.R_OK || fileAccessAuth == FileAccessAuth.RW_OK) {
            FileManager.getIFileManager(this).copyFile(this.m_file);
            Global.g_pastePaths.clear();
            Global.g_pastePaths.addAll(Global.g_filePath);
            cancelChoice();
            showPopupWindowPaste(true);
            return;
        }
        toast(R.string.noauthority);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delete(String str, FileAccessAuth fileAccessAuth) {
        if (fileAccessAuth == FileAccessAuth.RW_OK || fileAccessAuth == FileAccessAuth.W_OK) {
            FileInfo fileInfo = null;
            int i = 0;
            while (true) {
                if (i >= s_fileListView.getFileAdapter().getFileList().size()) {
                    break;
                }
                if (str.equals(s_fileListView.getFileAdapter().getFileList().get(i).getPath())) {
                    fileInfo = s_fileListView.getFileAdapter().getFileList().get(i);
                    break;
                }
                i++;
            }
            if (this.m_ipmsgApp.g_strDelFilePrompt.equals(ContentTree.VIDEO_ID)) {
                DeleteDialog deleteDialog = new DeleteDialog(this, fileInfo);
                this.m_dialogDeleteNotify = deleteDialog;
                deleteDialog.show();
                return;
            } else {
                if (Global.g_filePath.size() > 0) {
                    if (s_fileListView.isInner(fileInfo.getPath(), Global.g_filePath)) {
                        s_fileListView.deleteFileInfo(Global.g_filePath);
                        if (m_popupWindowSend.isShowing()) {
                            m_popupWindowSend.dismiss();
                        }
                        Global.g_filePath.clear();
                        return;
                    }
                    s_fileListView.deleteFileInfo(fileInfo);
                    return;
                }
                s_fileListView.deleteFileInfo(fileInfo);
                return;
            }
        }
        toast(R.string.noauthority);
    }

    private void detail(File file) {
        String[] fileDetail = FileManager.getIFileManager(this).getFileDetail(file);
        View viewInflate = this.m_inflater.inflate(R.layout.filedetaildialog, (ViewGroup) findViewById(R.id.detaildialog));
        ((TextView) viewInflate.findViewById(R.id.tvshowname)).setText(fileDetail[0]);
        ((TextView) viewInflate.findViewById(R.id.tvshowsize)).setText(fileDetail[1]);
        ((TextView) viewInflate.findViewById(R.id.tvshowauthority)).setText(fileDetail[2]);
        ((TextView) viewInflate.findViewById(R.id.tvshowtime)).setText(fileDetail[3]);
        ((TextView) viewInflate.findViewById(R.id.tvshowpath)).setText(Public_Tools.cutName(fileDetail[4]));
        new AlertDialog.Builder(this).setTitle(R.string.detail).setView(viewInflate).setPositiveButton(R.string.confirm, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void print(String str) {
        Intent intent = new Intent();
        intent.setClass(this, PrintActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", str);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showPopupWindowPaste(boolean z) {
        if (z) {
            this.m_popupWindowPaste.showAtLocation(this.m_viewFramel, 80, 0, 0);
        } else {
            this.m_popupWindowPaste.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void longClickRename(String str, FileAccessAuth fileAccessAuth) {
        String name;
        if (fileAccessAuth == FileAccessAuth.RW_OK || fileAccessAuth == FileAccessAuth.W_OK) {
            FileInfo fileInfo = null;
            int i = 0;
            while (true) {
                if (i >= s_fileListView.getFileAdapter().getFileList().size()) {
                    i = 0;
                    break;
                } else {
                    if (str.equals(s_fileListView.getFileAdapter().getFileList().get(i).getPath())) {
                        fileInfo = s_fileListView.getFileAdapter().getFileList().get(i);
                        break;
                    }
                    i++;
                }
            }
            final RenameDialog renameDialog = new RenameDialog(this, i, fileInfo);
            if (this.m_file.isFile()) {
                int iLastIndexOf = fileInfo.getName().lastIndexOf(".");
                name = iLastIndexOf != -1 ? fileInfo.getName().substring(0, iLastIndexOf) : "";
            } else {
                name = fileInfo.getName();
            }
            renameDialog.getEditTFolderName().setText(name);
            int iLastIndexOf2 = fileInfo.getName().lastIndexOf(46);
            if (iLastIndexOf2 == -1) {
                iLastIndexOf2 = fileInfo.getName().length();
            }
            renameDialog.getEditTFolderName().requestFocus();
            renameDialog.getEditTFolderName().setSelection(iLastIndexOf2);
            renameDialog.getEditTFolderName().setFocusable(true);
            renameDialog.getEditTFolderName().setFocusableInTouchMode(true);
            new Timer().schedule(new TimerTask() { // from class: com.netfeige.display.ui.IpmsgActivity.10
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ((InputMethodManager) renameDialog.getEditTFolderName().getContext().getSystemService("input_method")).showSoftInput(renameDialog.getEditTFolderName(), 0);
                }
            }, 600L);
            renameDialog.show();
            return;
        }
        toast(R.string.noauthority);
    }

    public void startOperateContextMenuThread() {
        new operateContextMenuThread(5, this.m_file, this.operateContextMenuhandler).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteOperation(File file) {
        FileManager.getIFileManager(this).deleteFile(file);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renameOperation(File file, String str) {
        FileManager.getIFileManager(this).Rename(file, str);
    }

    @Override // android.app.Activity
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    private class operateContextMenuThread extends Thread {
        private File file;
        private Handler handler;
        private int what;

        public operateContextMenuThread(int i, File file, Handler handler) {
            this.what = i;
            this.file = file;
            this.handler = handler;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int i = this.what;
            if (i == 4) {
                IpmsgActivity.this.deleteOperation(this.file);
                IpmsgActivity.this.sendHandlerMsg(this.handler, 4);
            } else {
                if (i != 5) {
                    return;
                }
                IpmsgActivity ipmsgActivity = IpmsgActivity.this;
                ipmsgActivity.renameOperation(this.file, ipmsgActivity.m_strNewName);
                IpmsgActivity.this.sendHandlerMsg(this.handler, 5);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendHandlerMsg(Handler handler, int i) {
        Message messageObtainMessage = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("state", i);
        messageObtainMessage.setData(bundle);
        handler.sendMessage(messageObtainMessage);
    }

    private class ButtonGestureListener extends GestureDetector.SimpleOnGestureListener {
        private int offsetHeight;

        private ButtonGestureListener() {
            this.offsetHeight = 50;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (IpmsgActivity.this.m_dragEnum != DragEnum.MIDDLE_UP) {
                if (IpmsgActivity.this.m_dragEnum != DragEnum.TOP) {
                    if (IpmsgActivity.this.m_dragEnum != DragEnum.MIDDLE_DOWN) {
                        if (IpmsgActivity.this.m_dragEnum == DragEnum.BOTTOM) {
                            IpmsgActivity.this.m_dragEnum = DragEnum.MIDDLE_UP;
                        }
                    } else {
                        IpmsgActivity.this.m_dragEnum = DragEnum.BOTTOM;
                    }
                } else {
                    IpmsgActivity.this.m_dragEnum = DragEnum.MIDDLE_DOWN;
                }
            } else {
                IpmsgActivity.this.m_dragEnum = DragEnum.TOP;
            }
            IpmsgActivity.this.m_dragProcess.start();
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            float y = motionEvent.getY();
            float rawY = motionEvent2.getRawY();
            int iHeight = IpmsgActivity.this.m_rectWindowArea.height();
            if (motionEvent2.getAction() == 2) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) IpmsgActivity.s_fileListView.getLayoutParams();
                layoutParams.height = iHeight - ((int) ((rawY + IpmsgActivity.this.m_btnDragbar.getHeight()) - y));
                if (layoutParams.height > iHeight - IpmsgActivity.this.m_linearLDragBar.getHeight()) {
                    layoutParams.height = iHeight - IpmsgActivity.this.m_linearLDragBar.getHeight();
                } else if (layoutParams.height < 0) {
                    layoutParams.height = 0;
                }
                IpmsgActivity.s_fileListView.setLayoutParams(layoutParams);
                int height = IpmsgActivity.s_fileListView.getHeight();
                if (height < (iHeight - this.offsetHeight) - IpmsgActivity.this.m_linearLDragBar.getHeight()) {
                    IpmsgActivity.this.showBtn();
                } else if (height >= (iHeight - this.offsetHeight) - IpmsgActivity.this.m_linearLDragBar.getHeight()) {
                    IpmsgActivity.this.notShowBtn();
                }
                if (height > 80) {
                    if (Global.g_filePath.size() > 0) {
                        IpmsgActivity.this.showSendPopupWindow();
                        if (!IpmsgActivity.m_popupWindowSend.isShowing()) {
                            IpmsgActivity.m_popupWindowSend.showAtLocation(IpmsgActivity.this.m_viewFramel, 80, 0, 0);
                        }
                    }
                    if (Global.g_bWaitPaste == 1 && !IpmsgActivity.this.m_popupWindowPaste.isShowing()) {
                        IpmsgActivity.this.showPopupWindowPaste(true);
                    }
                }
            }
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            float rawY = motionEvent.getRawY();
            float rawY2 = motionEvent2.getRawY();
            int top = IpmsgActivity.s_fileListView.getTop();
            if (rawY2 - rawY < 0.0f) {
                if (top - 50 <= IpmsgActivity.this.m_rectWindowArea.height() / 2) {
                    IpmsgActivity.this.m_dragEnum = DragEnum.TOP;
                } else {
                    IpmsgActivity.this.m_dragEnum = DragEnum.MIDDLE_UP;
                }
            } else if (top < IpmsgActivity.this.m_rectWindowArea.height() / 2) {
                IpmsgActivity.this.m_dragEnum = DragEnum.MIDDLE_DOWN;
            } else {
                IpmsgActivity.this.m_dragEnum = DragEnum.BOTTOM;
            }
            IpmsgActivity.this.m_dragProcess.start();
            return false;
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        Global.g_whatFolder = this.m_keepWhatFolder;
        Global.g_isInFileActivity = false;
        try {
            if (Global.g_bIsDelete) {
                for (int i = 0; i < Global.g_deletedPos.size(); i++) {
                    s_fileListView.getFileAdapter().getFileList().remove(Global.g_deletedPos.get(i).intValue());
                }
                s_fileListView.getFileAdapter().notifyDataSetChanged();
                Global.g_bIsDelete = false;
                Global.g_deletedPos.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Global.g_bBackgroundPlay && this.m_dragEnum != DragEnum.TOP) {
            s_imageBtnBackgroundPlay.setVisibility(0);
        } else {
            s_imageBtnBackgroundPlay.setVisibility(4);
        }
        if (Global.g_bFeigeDownloadChanged) {
            Catalogue.categoryStack.clear();
            s_fileListView.backSet();
            Global.g_bFeigeDownloadChanged = false;
        }
        if (Global.g_bIsClearImageList) {
            ImageAdapter.s_imageList.clear();
            ImageAdapter.s_imageList = (ArrayList) Global.g_imageListCache.clone();
            Global.g_imageListCache.clear();
            Global.g_bIsClearImageList = false;
        }
        if (Global.g_bIsClearAudioList) {
            IpmsgApplication.g_arrMusicList.clear();
            IpmsgApplication.g_arrMusicList = (ArrayList) Global.g_audioListCache.clone();
            Global.g_audioListCache.clear();
            Global.g_bIsClearAudioList = false;
        }
        if (Global.g_videoListCache.size() > 0) {
            IpmsgApplication.g_arrVideoList.clear();
            IpmsgApplication.g_arrVideoList = (ArrayList) Global.g_videoListCache.clone();
            Global.g_videoListCache.clear();
        }
        if (Global.g_bChangedHead) {
            Global.g_bChangedHead = false;
            this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.entryService(new ArrayList<>(this.m_ipmsgApp.ipmsgService.userList), true);
        }
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.m_ipmsgApp.currentActivity == this) {
            this.m_ipmsgApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_ipmsgApp, getString(R.string.app_name), this.m_iDataConfig.Read(0), R.drawable.ic_launcher);
        }
        super.onStop();
        this.m_sensorManager.unregisterListener(this);
        this.m_keepWhatFolder = Global.g_whatFolder;
        Global.g_imageListCache = (ArrayList) ImageAdapter.s_imageList.clone();
        Global.g_audioListCache = (ArrayList) IpmsgApplication.g_arrMusicList.clone();
        Global.g_videoListCache = (ArrayList) IpmsgApplication.g_arrVideoList.clone();
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_ipmsgApp.currentActivity = this;
        UserAdapter userAdapter = m_UserAdapter;
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
        discussUnreadPrompt();
        if (this.m_ipmsgApp.g_bBackRuning) {
            this.m_ipmsgApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_ipmsgApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
        SensorManager sensorManager = this.m_sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(1), 3);
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.m_sensorManager.unregisterListener(this);
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Date date = new Date();
        if (this.lastClickDate != null && date.getTime() - this.lastClickDate.getTime() < 1000) {
            musicServiceStop();
            updateSqlite();
            exit();
        } else {
            this.lastClickDate = date;
            Public_Tools.showToast(this, getString(R.string.exit_prompt), 0);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.m_ipmsgApp.ipmsgService != null) {
            this.m_ipmsgApp.ipmsgService.ehList.remove(this);
            this.m_ipmsgApp.ipmsgService.entryBroadcastEHList.remove(this);
            this.m_ipmsgApp.ipmsgService.mDiscussInviteEHList.remove(this);
            this.m_ipmsgApp.ipmsgService.mDiscussExitEHList.remove(this);
        }
        NetStatusBroadcast.ehList.remove(this);
        super.onDestroy();
    }

    public void exit() {
        this.m_iDataConfig.Write(29, String.valueOf(Global.g_nBrowseMethod));
        if (hasFileTro()) {
            createExitDialog();
            return;
        }
        if (Global.g_bWiFiAPWorking) {
            new ExitWiFiNotifyDialog(this, this.m_ipmsgApp).show();
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        startActivity(intent);
        Public_Tools.exitApp(this.m_ipmsgApp);
    }

    private boolean hasFileTro() {
        if (this.m_ipmsgApp.ipmsgService.fileMsgs.size() > 0) {
            Iterator<Map.Entry<String, ArrayList<MsgRecord>>> it = this.m_ipmsgApp.ipmsgService.fileMsgs.entrySet().iterator();
            while (it.hasNext()) {
                ArrayList<MsgRecord> value = it.next().getValue();
                for (int i = 0; i < value.size(); i++) {
                    if (value.get(i).getFileId() != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void createExitDialog() {
        new ExitDialog(this, this.m_ipmsgApp).show();
    }

    private void musicServiceStop() {
        try {
            MusicService.getMusicService().stopSelf();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, getString(R.string.setup)).setIcon(R.drawable.setup);
        menu.add(1, 2, 1, getString(R.string.exit)).setIcon(R.drawable.exit);
        setMenuBackground();
        return true;
    }

    protected void setMenuBackground() {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() { // from class: com.netfeige.display.ui.IpmsgActivity.12
            @Override // android.view.LayoutInflater.Factory
            public View onCreateView(String str, Context context, AttributeSet attributeSet) {
                if (str.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
                    try {
                        final View viewCreateView = IpmsgActivity.this.getLayoutInflater().createView(str, null, attributeSet);
                        new Handler().post(new Runnable() { // from class: com.netfeige.display.ui.IpmsgActivity.12.1
                            @Override // java.lang.Runnable
                            public void run() {
                                viewCreateView.setBackgroundColor(IpmsgActivity.this.getResources().getColor(R.color.black));
                            }
                        });
                        return viewCreateView;
                    } catch (InflateException | ClassNotFoundException unused) {
                    }
                }
                return null;
            }
        });
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            startActivity(new Intent(this, (Class<?>) OptionActivity.class));
        } else if (itemId == 2) {
            musicServiceStop();
            updateSqlite();
            exit();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    class DragProcess implements Runnable {
        private Thread thread = null;
        private boolean running = false;

        DragProcess() {
        }

        public synchronized void start() {
            Thread thread = new Thread(this);
            this.thread = thread;
            this.running = true;
            thread.start();
        }

        public synchronized void stop() {
            this.running = false;
            this.thread = null;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (this.running) {
                Message messageObtain = Message.obtain(IpmsgActivity.this.handler);
                messageObtain.what = 0;
                messageObtain.sendToTarget();
                try {
                    Thread.sleep(1L);
                } catch (Exception unused) {
                }
            }
        }
    }

    class StartUpProcess implements Runnable {
        private Thread thread = null;

        StartUpProcess() {
        }

        public synchronized void start() {
            Thread thread = new Thread(this);
            this.thread = thread;
            thread.start();
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                Thread.sleep(1500L);
            } catch (Exception unused) {
            }
            Message messageObtain = Message.obtain(IpmsgActivity.this.handler);
            messageObtain.what = 3;
            messageObtain.sendToTarget();
            NetStatusBroadcast.ehList.add(IpmsgActivity.this);
        }
    }

    class HiddenWifiPromptProcess implements Runnable {
        private Thread thread = null;

        HiddenWifiPromptProcess() {
        }

        public synchronized void start() {
            Thread thread = new Thread(this);
            this.thread = thread;
            thread.start();
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                Thread.sleep(5000L);
            } catch (Exception unused) {
            }
            Message messageObtain = Message.obtain(IpmsgActivity.this.handler);
            messageObtain.what = 5;
            messageObtain.arg1 = 2;
            messageObtain.sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBtn() {
        this.m_btnRefresh.setVisibility(0);
        this.m_btnSetting.setVisibility(0);
        this.m_btnMore.setVisibility(8);
        this.m_btnWT.setVisibility(0);
        this.m_btnParentFolder.setVisibility(8);
        if (Global.g_bBackgroundPlay) {
            this.m_imageBtnForLayout.setVisibility(4);
            s_imageBtnBackgroundPlay.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notShowBtn() {
        this.m_btnRefresh.setVisibility(8);
        this.m_btnSetting.setVisibility(8);
        this.m_btnWT.setVisibility(8);
        if (!Global.g_bInRoot) {
            this.m_btnMore.setVisibility(0);
            this.m_btnParentFolder.setVisibility(0);
        }
        if (Global.g_bBackgroundPlay) {
            this.m_imageBtnForLayout.setVisibility(8);
            s_imageBtnBackgroundPlay.setVisibility(8);
        }
    }

    public boolean fileDragLocation(int i, int i2, Vector<String> vector, Vector<String> vector2) {
        int[] iArr = new int[2];
        boolean z = false;
        for (int i3 = 0; i3 < this.m_gridVUsers.getChildCount(); i3++) {
            View childAt = this.m_gridVUsers.getChildAt(i3);
            childAt.getLocationInWindow(iArr);
            if (i >= iArr[0] && i <= childAt.getWidth() + iArr[0] && i2 >= iArr[1] && i2 <= childAt.getHeight() + iArr[1] && i2 > this.m_linearLTopNavigation.getBottom() && i2 <= s_fileListView.getTop()) {
                this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, vector, this.m_ipmsgApp.ipmsgService.userList.get(this.m_gridVUsers.getPositionForView(childAt)), this.m_ipmsgApp.ipmsgService, vector2, 0);
                z = true;
            }
        }
        return z;
    }

    public boolean fileDragLocation(int i, int i2, String str, Vector<String> vector) {
        boolean z = false;
        try {
            View gridViewItem = getGridViewItem(i, i2);
            if (gridViewItem == null) {
                return false;
            }
            z = true;
            HostInformation hostInformation = this.m_ipmsgApp.ipmsgService.userList.get(this.m_gridVUsers.getPositionForView(gridViewItem));
            Vector<String> vector2 = new Vector<>();
            vector2.add(str);
            this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, vector2, hostInformation, this.m_ipmsgApp.ipmsgService, vector, 0);
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return z;
        } catch (Exception e2) {
            e2.printStackTrace();
            return z;
        }
    }

    public void fileDragLocation(float f, HostInformation hostInformation, Vector<String> vector, Vector<String> vector2) {
        if (f <= this.m_linearLTopNavigation.getBottom() || f > s_fileListView.getTop()) {
            return;
        }
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, vector, hostInformation, this.m_ipmsgApp.ipmsgService, vector2, 0);
    }

    public void fileDragLocation(float f, HostInformation hostInformation, String str, Vector<String> vector) {
        if (f <= this.m_linearLTopNavigation.getBottom() || f > s_fileListView.getTop()) {
            return;
        }
        Vector<String> vector2 = new Vector<>();
        vector2.add(str);
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, vector2, hostInformation, this.m_ipmsgApp.ipmsgService, vector, 0);
    }

    public void fileDragLocation(HostInformation hostInformation, Vector<String> vector, Vector<String> vector2) {
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, vector, hostInformation, this.m_ipmsgApp.ipmsgService, vector2, 0);
    }

    public void fileDragLocation(int i, int i2) {
        int[] iArr = new int[2];
        for (int i3 = 0; i3 < this.m_gridVUsers.getChildCount(); i3++) {
            View childAt = this.m_gridVUsers.getChildAt(i3);
            childAt.getLocationInWindow(iArr);
            if (i >= iArr[0] && i <= childAt.getWidth() + iArr[0] && i2 >= iArr[1] && i2 <= childAt.getHeight() + iArr[1] && i2 > this.m_linearLTopNavigation.getBottom() && i2 <= s_fileListView.getTop()) {
                HostInformation hostInformation = this.m_ipmsgApp.ipmsgService.userList.get(this.m_gridVUsers.getPositionForView(childAt));
                if (hostInformation.equals(Global.g_hostInfo)) {
                    m_UserAdapter.setHostInformation(null);
                    Global.g_hostInfo = null;
                    m_UserAdapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(this, (Class<?>) ChatActivity.class);
                    intent.putExtra("ip", hostInformation.IpAddr.netAddr.getHostAddress());
                    intent.putExtra("strMacAddr", hostInformation.strMacAddr);
                    intent.putExtra("name", hostInformation.pszUserName);
                    startActivityForResult(intent, 1);
                }
            }
        }
    }

    public View getGridViewItem(int i, int i2) {
        int[] iArr = new int[2];
        for (int i3 = 0; i3 < this.m_gridVUsers.getChildCount(); i3++) {
            View childAt = this.m_gridVUsers.getChildAt(i3);
            childAt.getLocationInWindow(iArr);
            if (i >= iArr[0] && i <= childAt.getWidth() + iArr[0] && i2 >= iArr[1] && i2 <= childAt.getHeight() + iArr[1] && i2 > this.m_linearLTopNavigation.getBottom() && i2 <= s_fileListView.getTop()) {
                return childAt;
            }
        }
        return null;
    }

    public void cancelHighlightDisplay() {
        for (int i = 0; i < this.m_gridVUsers.getChildCount(); i++) {
            this.m_gridVUsers.getChildAt(i).setBackgroundColor(0);
        }
    }

    public View getViewItem(float f, float f2) {
        int[] iArr = new int[2];
        View view = null;
        for (int i = 0; i < s_fileListView.getChildCount(); i++) {
            View childAt = s_fileListView.getChildAt(i);
            childAt.getLocationInWindow(iArr);
            if (f >= iArr[0] && f <= childAt.getWidth() + iArr[0] && f2 >= iArr[1] && f2 <= childAt.getHeight() + iArr[1]) {
                view = childAt;
            }
        }
        return view;
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 1;
        messageObtain.arg1 = 0;
        messageObtain.obj = hostInformation;
        Collections.sort(this.m_ipmsgApp.ipmsgService.userList, new UserComparer());
        messageObtain.sendToTarget();
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
        if (msgRecord == null || msgRecord.isSend()) {
            return;
        }
        if (this.m_ipmsgApp.g_strPromptAudio.equals(ContentTree.VIDEO_ID)) {
            this.m_player.play(this, R.raw.message, false);
        }
        if (str != null) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 2;
            if (str.startsWith(Protocol_Discuss.smPreID)) {
                messageObtain.arg1 = 2;
            } else {
                messageObtain.arg1 = 1;
            }
            messageObtain.sendToTarget();
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileMessage(String str, MsgRecord msgRecord) {
        if (msgRecord.getFileId() == -1) {
            if (this.m_ipmsgApp.g_strPromptAudio.equals(ContentTree.VIDEO_ID)) {
                this.m_player.play(this, R.raw.message, false);
            }
            if (str != null) {
                Message messageObtain = Message.obtain(this.handler);
                messageObtain.what = 2;
                if (str.startsWith(Protocol_Discuss.smPreID)) {
                    messageObtain.arg1 = 2;
                } else {
                    messageObtain.arg1 = 1;
                }
                messageObtain.sendToTarget();
            }
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str, boolean z) {
        Message message = new Message();
        message.what = 4;
        Bundle bundle = new Bundle();
        bundle.putString("fileName", str);
        bundle.putBoolean("isFolder", z);
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    @Override // com.netfeige.broadcast.NetStatusBroadcast.EventHandler
    public void wifiStatusNotification(boolean z) {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 5;
        messageObtain.arg1 = !z ? 0 : 1;
        messageObtain.sendToTarget();
    }

    private class GridViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        private GridViewGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            View gridViewItem = IpmsgActivity.this.getGridViewItem((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
            if (gridViewItem != null) {
                try {
                    HostInformation hostInformation = IpmsgActivity.this.m_ipmsgApp.ipmsgService.userList.get(IpmsgActivity.this.m_gridVUsers.getPositionForView(gridViewItem));
                    if (hostInformation == IpmsgActivity.m_UserAdapter.getHostInformation()) {
                        IpmsgActivity.m_UserAdapter.setHostInformation(null);
                        Global.g_hostInfo = null;
                    } else {
                        IpmsgActivity.m_UserAdapter.setHostInformation(hostInformation);
                        Global.g_hostInfo = hostInformation;
                    }
                    IpmsgActivity.m_UserAdapter.notifyDataSetChanged();
                } catch (Exception unused) {
                }
            }
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            try {
                IpmsgActivity.this.fileDragLocation((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                IpmsgActivity.m_UserAdapter.setHostInformation(null);
                Global.g_hostInfo = null;
                if (Global.g_nCompressed != 1) {
                    return false;
                }
                IpmsgActivity.this.m_compress_progressBar.setVisibility(8);
                return false;
            } catch (Exception unused) {
                return false;
            }
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            try {
                if (Math.abs(motionEvent.getRawX() - motionEvent2.getRawX()) >= 100.0f && Math.abs(f) >= 600.0f && Math.abs(motionEvent.getRawY() - motionEvent2.getRawY()) / Math.abs(motionEvent.getRawX() - motionEvent2.getRawX()) <= 1.0f) {
                    if (IpmsgActivity.this.m_ipmsgApp.g_iUserColumn == 4 && IpmsgActivity.this.m_ipmsgApp.g_iUserItemLeft == -1) {
                        IpmsgActivity.this.m_ipmsgApp.g_iUserItemLeft = ((RelativeLayout) IpmsgActivity.this.m_gridVUsers.getChildAt(0).findViewById(R.id.header_rlayout)).getLeft();
                    }
                    if (IpmsgActivity.this.m_ipmsgApp.g_iUserColumn == 4) {
                        IpmsgActivity.this.m_ipmsgApp.g_iUserColumn = 1;
                        IpmsgActivity.this.m_gridVUsers.setNumColumns(1);
                    } else {
                        IpmsgActivity.this.m_ipmsgApp.g_iUserColumn = 4;
                        IpmsgActivity.this.m_gridVUsers.setNumColumns(4);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return super.onFling(motionEvent, motionEvent2, f, f2);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (Math.abs(motionEvent.getRawY() - motionEvent2.getRawY()) / Math.abs(motionEvent.getRawX() - motionEvent2.getRawX()) > 1.0f) {
                return super.onScroll(motionEvent, motionEvent2, f, f2);
            }
            return false;
        }
    }

    class UserComparer implements Comparator<HostInformation> {
        UserComparer() {
        }

        @Override // java.util.Comparator
        public int compare(HostInformation hostInformation, HostInformation hostInformation2) {
            return hostInformation.pszUserName.compareTo(hostInformation2.pszUserName);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        HostInformation hostInformation = Global.g_hostInfo;
        Vector<String> vector = Global.g_filePath;
        int type = sensorEvent.sensor.getType();
        float[] fArr = sensorEvent.values;
        if (type == 1) {
            try {
                if (Math.abs(fArr[0]) > this.m_nAcceleration || Math.abs(fArr[1]) > this.m_nAcceleration) {
                    if (hostInformation == null || vector.size() <= 0) {
                        if (hostInformation != null) {
                            if (this.m_ipmsgApp.g_strSendAudio.equals(ContentTree.VIDEO_ID)) {
                                this.m_soundPool.play(this.m_nTerminateMusic, 1.0f, 1.0f, 0, 0, 1.0f);
                                return;
                            }
                            return;
                        } else {
                            if (vector.size() <= 0 || !this.m_ipmsgApp.g_strSendAudio.equals(ContentTree.VIDEO_ID)) {
                                return;
                            }
                            this.m_soundPool.play(this.m_nTerminateMusic, 1.0f, 1.0f, 0, 0, 1.0f);
                            return;
                        }
                    }
                    this.m_isSensor = true;
                    sloshAnimation(vector.get(0), hostInformation);
                    sensorSendFile(hostInformation, vector);
                    Global.g_filePath.clear();
                    if (m_popupWindowSend.isShowing()) {
                        m_popupWindowSend.dismiss();
                    }
                    for (int i = 0; i < s_fileListView.getFileAdapter().getFileList().size(); i++) {
                        s_fileListView.getFileAdapter().getFileList().get(i).setCheck(false);
                    }
                    s_fileListView.getFileAdapter().notifyDataSetChanged();
                    if (this.m_ipmsgApp.g_strSendAudio.equals(ContentTree.VIDEO_ID)) {
                        this.m_soundPool.play(this.m_nMusic, 1.0f, 1.0f, 0, 0, 1.0f);
                    }
                }
            } catch (Exception unused) {
            }
        }
    }

    private void sensorSendFile(HostInformation hostInformation, Vector<String> vector) {
        if (Global.g_whatFolder != null) {
            int i = AnonymousClass24.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
            if (i == 1) {
                fileDragLocation(hostInformation, vector, FileListView.m_vecLimitedApk);
                return;
            }
            if (i == 2) {
                fileDragLocation(hostInformation, vector, FileListView.m_vecLimitedAudio);
                return;
            }
            if (i == 3) {
                fileDragLocation(hostInformation, vector, FileListView.m_vecLimitedDocument);
                return;
            }
            if (i == 4) {
                fileDragLocation(hostInformation, vector, FileListView.m_vecLimitedImage);
            } else if (i == 5) {
                fileDragLocation(hostInformation, vector, FileListView.m_vecLimitedVideo);
            } else {
                fileDragLocation(hostInformation, vector, null);
            }
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.IpmsgActivity$24, reason: invalid class name */
    static /* synthetic */ class AnonymousClass24 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Category;
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$display$ui$IpmsgActivity$DragEnum;

        static {
            int[] iArr = new int[Category.values().length];
            $SwitchMap$com$netfeige$common$Category = iArr;
            try {
                iArr[Category.apk.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.audio.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.document.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.image.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.video.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            int[] iArr2 = new int[DragEnum.values().length];
            $SwitchMap$com$netfeige$display$ui$IpmsgActivity$DragEnum = iArr2;
            try {
                iArr2[DragEnum.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$IpmsgActivity$DragEnum[DragEnum.MIDDLE_UP.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$IpmsgActivity$DragEnum[DragEnum.MIDDLE_DOWN.ordinal()] = 3;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$IpmsgActivity$DragEnum[DragEnum.BOTTOM.ordinal()] = 4;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    public void dragAnimation(HostInformation hostInformation) {
        startAnimation((s_fileListView.m_nCurrentFileIndex >= 0 ? s_fileListView.getFileAdapter().fileList.get(s_fileListView.m_nCurrentFileIndex) : null).getPath(), hostInformation, s_fileListView.getStartX(), s_fileListView.getStartY());
    }

    private void sloshAnimation(String str, HostInformation hostInformation) {
        startAnimation(str, hostInformation, this.m_nLeft, getStartY());
    }

    public float getStartY() {
        int top;
        int i = s_fileListView.m_nCurrentFileIndex - this.m_nDiffNum;
        if (i >= 0) {
            top = (i * 50) + s_fileListView.getTop();
        } else {
            top = this.m_nSetHeight;
        }
        return top;
    }

    private void startAnimation(String str, HostInformation hostInformation, float f, float f2) {
        ImageView imageView = (ImageView) findViewById(R.id.drag_imageView);
        this.m_imageVAnimationPic = imageView;
        imageView.setVisibility(0);
        if (this.m_isSensor) {
            setBackgroundForSensor(str);
            this.m_isSensor = false;
        } else {
            setBackground(str);
        }
        this.m_imageVAnimationPic.bringToFront();
        View childAt = null;
        try {
            childAt = this.m_gridVUsers.getChildAt(m_UserAdapter.getPosition(hostInformation));
        } catch (Exception unused) {
        }
        if (childAt != null) {
            int[] iArr = new int[2];
            childAt.getLocationInWindow(iArr);
            int i = iArr[0];
            int i2 = this.m_nOffset;
            int i3 = i + i2;
            this.m_nSelectedX = i3;
            int i4 = iArr[1] - i2;
            this.m_nSelectedY = i4;
            setAnimation(f, f2, i3, i4);
            return;
        }
        this.m_nSelectedX = (this.m_btnDragbar.getLeft() + (this.m_btnDragbar.getWidth() / 2)) - 20;
        this.m_nSelectedY = (s_fileListView.getTop() - (this.m_btnDragbar.getHeight() / 2)) - 20;
        if (this.m_nSelectedX < 0) {
            this.m_nSelectedX = this.m_btnDragbar.getLeft() + (this.m_btnDragbar.getWidth() / 2);
        }
        if (this.m_nSelectedY < 0) {
            this.m_nSelectedY = 0;
        }
        setAnimation(f, f2, this.m_nSelectedX, this.m_nSelectedY);
    }

    private void setBackground(String str) {
        boolean zIsInner = s_fileListView.isInner(str, Global.g_filePath);
        if (Global.g_filePath.size() > 1 && zIsInner) {
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundResource(R.drawable.sendfiles);
            return;
        }
        File file = new File(str);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFloder(file.isDirectory());
        fileInfo.setName(file.getName());
        fileInfo.setPath(file.getPath());
        fileInfo.setSize(file.length());
        if (!fileInfo.isFloder()) {
            String str2 = fileInfo.name;
            Bitmap bitmap = s_fileListView.getmDragBitmap();
            if (bitmap != null) {
                this.m_imageVAnimationPic.setImageBitmap(bitmap);
                this.m_imageVAnimationPic.setBackgroundColor(0);
                return;
            } else {
                this.m_imageVAnimationPic.setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(str2, true, true));
                return;
            }
        }
        if (fileInfo.path.equals(Public_Tools.getDefaultDownloadPath())) {
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundResource(R.drawable.filer_feige);
        } else {
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundResource(R.drawable.folder_icon);
        }
    }

    private void setBackgroundForSensor(String str) {
        if (Global.g_filePath.size() > 1) {
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundResource(R.drawable.sendfiles);
        } else if (Global.g_filePath.size() == 1) {
            sizeIs1();
        } else {
            sizeDy1(str);
        }
    }

    private void sizeDy1(String str) {
        Bitmap bitmap;
        File file = new File(str);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFloder(file.isDirectory());
        fileInfo.setName(file.getName());
        fileInfo.setPath(file.getPath());
        fileInfo.setSize(file.length());
        if (!fileInfo.isFloder()) {
            String str2 = fileInfo.name;
            if (Public_Tools.isImageFile(str2)) {
                try {
                    if (s_fileListView.m_nCurrentFileIndex == -1 || (bitmap = s_fileListView.getFileAdapter().getFileList().get(s_fileListView.m_nCurrentFileIndex).getBitmap()) == null) {
                        return;
                    }
                    this.m_imageVAnimationPic.setImageBitmap(bitmap);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(str2, true, true));
            return;
        }
        if (fileInfo.path.equals(Public_Tools.getDefaultDownloadPath())) {
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundResource(R.drawable.filer_feige);
        } else {
            this.m_imageVAnimationPic.setImageBitmap(null);
            this.m_imageVAnimationPic.setBackgroundResource(R.drawable.folder_icon);
        }
    }

    private void sizeIs1() {
        Bitmap bitmap;
        try {
            FileInfo fileInfo = s_fileListView.getFileAdapter().getFileList().get(Public_Tools.getPositionOnPath(s_fileListView.getFileAdapter().getFileList(), Global.g_filePath.get(0)));
            if (!fileInfo.isFloder()) {
                if (Public_Tools.isImageFile(fileInfo.getName()) && (bitmap = fileInfo.getBitmap()) != null) {
                    this.m_imageVAnimationPic.setImageBitmap(bitmap);
                    this.m_imageVAnimationPic.setBackgroundColor(0);
                } else {
                    this.m_imageVAnimationPic.setImageBitmap(null);
                    this.m_imageVAnimationPic.setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(fileInfo.getName(), true, true));
                }
            } else if (fileInfo.path.equals(Public_Tools.getDefaultDownloadPath())) {
                this.m_imageVAnimationPic.setImageBitmap(null);
                this.m_imageVAnimationPic.setBackgroundResource(R.drawable.filer_feige);
            } else {
                this.m_imageVAnimationPic.setImageBitmap(null);
                this.m_imageVAnimationPic.setBackgroundResource(R.drawable.folder_icon);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void backAnimation(String str, int i, int i2, int i3, int i4, boolean z) {
        this.m_imageVAnimationPic = (ImageView) findViewById(R.id.drag_imageView);
        setBackground(str);
        this.m_imageVAnimationPic.bringToFront();
        this.m_imageVAnimationPic.setVisibility(0);
        if (z) {
            i2 += Public_Tools.dip2px(this, 180.0f);
        }
        setAnimation(i3, i4, i, i2);
    }

    private void setAnimation(float f, float f2, int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(f, i, f2, i2);
        this.m_animation = translateAnimation;
        translateAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.netfeige.display.ui.IpmsgActivity.15
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                IpmsgActivity.this.m_imageVAnimationPic.setVisibility(8);
            }
        });
        this.m_animation.setDuration(600L);
        this.m_animation.setRepeatCount(0);
        this.m_imageVAnimationPic.startAnimation(this.m_animation);
    }

    public int getDensityDpi() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.densityDpi / 160;
    }

    public void refresh(boolean z) {
        IpmsgService.printerList.clear();
        Global.g_hostInformation = null;
        m_UserAdapter.setHostInformation(null);
        Global.g_hostInfo = null;
        ArrayList<HostInformation> arrayList = new ArrayList<>(this.m_ipmsgApp.ipmsgService.userList);
        int i = 0;
        if (!Public_Tools.isWifiConnect()) {
            if (this.m_ipmsgApp.ipmsgService.fileMsgs.size() > 0) {
                Iterator<Map.Entry<String, ArrayList<MsgRecord>>> it = this.m_ipmsgApp.ipmsgService.fileMsgs.entrySet().iterator();
                while (it.hasNext()) {
                    for (MsgRecord msgRecord : it.next().getValue()) {
                        if (msgRecord.getFileId() != -1) {
                            msgRecord.setFileId(-1L);
                        }
                    }
                }
            }
            this.m_linearLSetupWifi.setVisibility(0);
            new HiddenWifiPromptProcess().start();
            this.m_ipmsgApp.ipmsgService.userList.clear();
        } else {
            HashMap map = new HashMap();
            if (this.m_ipmsgApp.ipmsgService.fileMsgs.size() > 0) {
                for (Map.Entry<String, ArrayList<MsgRecord>> entry : this.m_ipmsgApp.ipmsgService.fileMsgs.entrySet()) {
                    Iterator<MsgRecord> it2 = entry.getValue().iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            if (it2.next().getFileId() != -1) {
                                map.put(entry.getKey(), "");
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            while (i < this.m_ipmsgApp.ipmsgService.userList.size()) {
                if (map.get(this.m_ipmsgApp.ipmsgService.userList.get(i).strMacAddr) == null && !this.m_ipmsgApp.ipmsgService.userList.get(i).unreadMsg) {
                    this.m_ipmsgApp.ipmsgService.userList.remove(i);
                    i--;
                }
                i++;
            }
        }
        m_UserAdapter.notifyDataSetChanged();
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.entryService(arrayList, z);
    }

    @Override // com.netfeige.common.IUpdateNotify
    public void onGetUpdateResult(int i, String str, String str2, String str3) {
        this.m_ipmsgApp.updateStatus = i;
        this.m_ipmsgApp.newVersion = str;
        this.m_ipmsgApp.newVersionChanged = str2;
        this.m_ipmsgApp.updatePath = str3;
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 6;
        messageObtain.sendToTarget();
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
        Public_Tools.fileCompare(s_fileListView.getFileAdapter().getFileList());
        Public_Tools.imageCompare(ImageAdapter.s_imageList);
        Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
        Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
        s_fileListView.getFileAdapter().notifyDataSetChanged();
        Public_Tools.writeSqlite(m_dbHelper);
        if (Global.g_nBrowseMethod == 0) {
            FileManager.m_imageList.clear();
            FileManager.m_audioList.clear();
            FileManager.m_videoList.clear();
            FileManager.m_documentList.clear();
            FileManager.m_apkList.clear();
        }
        this.m_iDataConfig.Write(28, ContentTree.VIDEO_ID);
        if (Global.g_isAsyncLoadedImage) {
            s_fileListView.loadingImage(this.m_nFirstItem, this.m_nCurrentCount, this);
        }
    }

    public Handler getM_compressHandler() {
        return this.m_compressHandler;
    }

    public Button getM_btnDragbar() {
        return this.m_btnDragbar;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && i2 == -1) {
            try {
                Bundle extras = intent.getExtras();
                String string = extras != null ? extras.getString("folderPath") : null;
                if (string != null) {
                    s_fileListView.getWindowVisibleDisplayFrame(this.m_rectWindowArea);
                    ((LinearLayout.LayoutParams) s_fileListView.getLayoutParams()).height = this.m_rectWindowArea.height() / 2;
                    Catalogue.categoryStack.clear();
                    Catalogue.categoryStack.add("feige/");
                    Global.g_whatFolder = Category.feige;
                    this.m_keepWhatFolder = Category.feige;
                    s_fileListView.moveToCategory(string);
                    Global.g_listViewCurrentAdpter = 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ProgressBar getM_compress_progressBar() {
        return this.m_compress_progressBar;
    }

    public Rect getM_rectWindowArea() {
        return this.m_rectWindowArea;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void skipMusic() {
        FileManager.getIFileManager(this).openFile(new File(MusicService.s_nCurrentPlayingPath));
    }

    public void sameNameOprate(int i, FileInfo fileInfo, String str, String str2, int i2, int i3) {
        try {
            s_fileListView.getFileAdapter().getFileList().get(i).setName(this.m_strNewName);
            s_fileListView.getFileAdapter().getFileList().remove(i3);
            s_fileListView.getFileAdapter().notifyDataSetChanged();
            new operateContextMenuThread(5, this.m_file, this.operateContextMenuhandler).start();
            if (i2 != -1) {
                fileInfo.setPath(str2.substring(0, i2) + ServiceReference.DELIMITER + this.m_strNewName);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void differentNameOperate(int i, String str, String str2, int i2) {
        int musicPositionOnPath;
        s_fileListView.getFileAdapter().getFileList().get(i).setName(this.m_strNewName);
        if (Public_Tools.isImageFile(str)) {
            int imagePositionOnPath = Public_Tools.getImagePositionOnPath(ImageAdapter.s_imageList, str2);
            if (imagePositionOnPath != -1) {
                ImageAdapter.s_imageList.get(imagePositionOnPath).setM_strImageName(this.m_strNewName);
                ImageAdapter.s_imageList.get(imagePositionOnPath).setM_strImagePath(str2.substring(0, i2) + ServiceReference.DELIMITER + this.m_strNewName);
            }
        } else if (Public_Tools.isAudioFile(str)) {
            int musicPositionOnPath2 = Public_Tools.getMusicPositionOnPath(IpmsgApplication.g_arrMusicList, str2);
            if (musicPositionOnPath2 != -1) {
                IpmsgApplication.g_arrMusicList.get(musicPositionOnPath2).setName(this.m_strNewName);
                IpmsgApplication.g_arrMusicList.get(musicPositionOnPath2).setPath(str2.substring(0, i2) + ServiceReference.DELIMITER + this.m_strNewName);
            }
        } else if (Public_Tools.isVideoFile(str) && (musicPositionOnPath = Public_Tools.getMusicPositionOnPath(IpmsgApplication.g_arrVideoList, str2)) != -1) {
            IpmsgApplication.g_arrVideoList.get(musicPositionOnPath).setName(this.m_strNewName);
            IpmsgApplication.g_arrVideoList.get(musicPositionOnPath).setPath(str2.substring(0, i2) + ServiceReference.DELIMITER + this.m_strNewName);
        }
        s_fileListView.getFileAdapter().notifyDataSetChanged();
    }

    private void compressedUpdateCategory(Message message, String str, ArrayList<String> arrayList, Category category) {
        int i = message.getData().getInt(str);
        if (Global.g_whatFolder == category) {
            Public_Tools.path2FileInfoForMian(arrayList.get(i), s_fileListView);
            s_fileListView.getFileAdapter().notifyDataSetChanged();
        }
    }

    private void readSqlite() throws Exception {
        if (this.m_iDataConfig.Read(28).equals(ContentTree.VIDEO_ID)) {
            ArrayList<FolderInfo> allFolderInfo = m_dbHelper.getAllFolderInfo();
            for (int i = 0; i < allFolderInfo.size(); i++) {
                String category = allFolderInfo.get(i).getCategory();
                String path = allFolderInfo.get(i).getPath();
                if (category.equals("image")) {
                    if (!FileManager.m_imageFolderList.contains(path)) {
                        FileManager.m_imageFolderList.add(path);
                    }
                } else if (category.equals("audio")) {
                    if (!FileManager.m_audioFolderList.contains(path)) {
                        FileManager.m_audioFolderList.add(path);
                    }
                } else if (category.equals("video")) {
                    if (!FileManager.m_videoFolderList.contains(path)) {
                        FileManager.m_videoFolderList.add(path);
                    }
                } else if (category.equals("document")) {
                    if (!FileManager.m_documentFolderList.contains(path)) {
                        FileManager.m_documentFolderList.add(path);
                    }
                } else if (category.equals("apk") && !FileManager.m_apkFolderList.contains(path)) {
                    FileManager.m_apkFolderList.add(path);
                }
            }
        }
    }

    private class ListViewScrollListen implements AbsListView.OnScrollListener {
        private ListViewScrollListen() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            IpmsgActivity.s_nCurrentposition = i;
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 0) {
                IpmsgActivity.this.m_nDiffNum = IpmsgActivity.s_nCurrentposition;
                if (Global.g_isAsyncLoadedImage) {
                    IpmsgActivity.this.loadingImage();
                }
            }
        }
    }

    public void loadingImage() {
        FileListView fileListView = s_fileListView;
        int i = s_nCurrentposition;
        fileListView.loadingImage(i, this.m_nCurrentCount + i, this);
    }

    public static void updateSqlite() {
        if (Global.g_bIsUpdateSQLite) {
            m_dbHelper.deleteAllTableData("FolderPath");
            Public_Tools.writeSqlite(m_dbHelper);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshCompress() {
        s_fileListView.getFileAdapter().getFileList().clear();
        ImageAdapter.s_imageList.clear();
        IpmsgApplication.g_arrMusicList.clear();
        IpmsgApplication.g_arrVideoList.clear();
        Public_Tools.backForward(s_fileListView.getFileAdapter().getFileList());
        s_fileListView.getFileAdapter().notifyDataSetChanged();
        Global.g_filePath.clear();
        FileManager.m_imageList.clear();
        FileManager.m_audioList.clear();
        FileManager.m_videoList.clear();
        FileManager.m_documentList.clear();
        FileManager.m_apkList.clear();
        FileManager.m_imageFolderList.clear();
        FileManager.m_audioFolderList.clear();
        FileManager.m_videoFolderList.clear();
        FileManager.m_documentFolderList.clear();
        FileManager.m_apkFolderList.clear();
        Global.g_bIsUpdateSQLite = true;
        s_nCurrentposition = 0;
        startCompress();
    }

    private void startCompress() {
        FileManager.getIFileManager(this).compressFolder();
        Global.g_nCompressed = 1;
        showPregressBar();
    }

    public void showPregressBar() {
        this.m_compress_progressBar.setVisibility(0);
        this.m_compress_progressBar.bringToFront();
    }

    private void updateSendFile(String str, FileInfo fileInfo, int i) {
        if (i != -1) {
            if (i == 0) {
                ImageAdapter.s_imageList.add(new ImagePreview(0, fileInfo.getName(), fileInfo.getPath(), null, fileInfo.getLastTime()));
                Public_Tools.imageCompare(ImageAdapter.s_imageList);
                updateFolderList(fileInfo, FileManager.m_imageFolderList);
                loadingImage();
                return;
            }
            if (i == 1) {
                IpmsgApplication.g_arrMusicList.add(new Music(0, fileInfo.getName(), fileInfo.getPath(), fileInfo.getLastTime()));
                Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
                updateFolderList(fileInfo, FileManager.m_audioFolderList);
                return;
            }
            if (i == 2) {
                IpmsgApplication.g_arrVideoList.add(new Music(0, fileInfo.getName(), fileInfo.getPath(), fileInfo.getLastTime()));
                Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
                updateFolderList(fileInfo, FileManager.m_videoFolderList);
                return;
            }
            if (i == 3) {
                updateFolderList(fileInfo, FileManager.m_documentFolderList);
            } else if (i == 4) {
                updateFolderList(fileInfo, FileManager.m_apkFolderList);
                loadingImage();
            } else {
                updateFeigeFolder(fileInfo);
            }
        }
    }

    private void updateFolderList(FileInfo fileInfo, ArrayList<String> arrayList) {
        if (!arrayList.contains(Public_Tools.getDefaultDownloadPath())) {
            arrayList.add(Public_Tools.getDefaultDownloadPath());
            Global.g_bIsUpdateSQLite = true;
        }
        updateFeigeFolder(fileInfo);
    }

    private void updateFeigeFolder(FileInfo fileInfo) {
        if (Global.g_whatFolder == Category.feige) {
            s_fileListView.getFileAdapter().getFileList().add(fileInfo);
            Public_Tools.fileCompare(s_fileListView.getFileAdapter().getFileList());
            s_fileListView.getFileAdapter().notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFile(String str, String str2) {
        updateSendFile(str2, Public_Tools.path2FileInfo(str2), Public_Tools.fileCategory(str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFolder(String str, String str2) {
        FileInfo fileInfoPath2FileInfo = Public_Tools.path2FileInfo(str2);
        if (Global.g_bInFeige) {
            s_fileListView.getFileAdapter().getFileList().add(fileInfoPath2FileInfo);
            Public_Tools.fileCompare(s_fileListView.getFileAdapter().getFileList());
            s_fileListView.getFileAdapter().notifyDataSetChanged();
        }
    }

    private boolean isSpecifiedCategory(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFolderList(String str, int[] iArr, int i, ArrayList<String> arrayList) {
        if (!isSpecifiedCategory(iArr, i) || arrayList.contains(str)) {
            return;
        }
        arrayList.add(str);
        Global.g_bIsUpdateSQLite = true;
    }

    private void initBrowserPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupwindow, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindowBrowser = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup));
        this.m_popupWindowBrowser.setFocusable(true);
        this.m_popupWindowBrowser.setOutsideTouchable(true);
        this.m_popupWindowBrowser.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_refresh);
        this.m_linearLRefresh = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowRefreshOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_sort);
        this.m_linearLSort = linearLayout2;
        linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.showDialog(1);
                IpmsgActivity.this.m_popupWindowBrowser.dismiss();
            }
        });
        setPopUpSubControl(viewInflate);
        this.m_linearLShowFiles.setOnClickListener(new PopUpWindowShowFilesOnClickListener());
    }

    private void setPopUpSubControl(View view) {
        this.m_linearLShowFiles = (LinearLayout) view.findViewById(R.id.linearl_showfiles);
        this.m_imageVSwitch = (ImageView) view.findViewById(R.id.imagev_show);
        this.m_textVSwitch = (TextView) view.findViewById(R.id.textv_show);
        if (Global.g_nBrowseMethod == 1) {
            this.m_imageVSwitch.setImageResource(R.drawable.allshow);
            this.m_textVSwitch.setText(R.string.allshow);
        } else if (Global.g_nBrowseMethod == 0) {
            this.m_imageVSwitch.setImageResource(R.drawable.catagolueshow);
            this.m_textVSwitch.setText(R.string.catagolueshow);
        }
    }

    private class PopUpWindowShowFilesOnClickListener implements View.OnClickListener {
        private PopUpWindowShowFilesOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.m_popupWindowBrowser.dismiss();
            if (Global.g_nCompressed != 1) {
                if (Global.g_nBrowseMethod == 0) {
                    IpmsgActivity.this.switchCatagolueBrowse();
                } else if (Global.g_nBrowseMethod == 1) {
                    IpmsgActivity.this.switchAllBrowse();
                }
                Public_Tools.fileCompare(IpmsgActivity.s_fileListView.getFileAdapter().getFileList());
                IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
                Global.g_filePath.clear();
                IpmsgActivity.m_popupWindowSend.dismiss();
                return;
            }
            IpmsgActivity.this.toast(R.string.no_switch);
        }
    }

    private class PopUpWindowRefreshOnClickListener implements View.OnClickListener {
        private PopUpWindowRefreshOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (Global.g_bInCategoryDir) {
                try {
                    IpmsgActivity.s_fileListView.moveToCategory(Catalogue.categoryStack.pop());
                    IpmsgActivity.this.toast(R.string.refresh_success);
                } catch (EmptyStackException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } else if (Global.g_nCompressed != 1) {
                IpmsgActivity.this.refreshCompress();
            }
            IpmsgActivity.this.m_popupWindowBrowser.dismiss();
            IpmsgActivity.m_popupWindowSend.dismiss();
            Global.g_bWaitPaste = 0;
            Global.g_pastePaths.clear();
            IpmsgActivity.this.m_popupWindowPaste.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchAllBrowse() {
        this.m_imageVSwitch.setImageResource(R.drawable.catagolueshow);
        this.m_textVSwitch.setText(R.string.catagolueshow);
        Global.g_nBrowseMethod = 0;
        try {
            if (Global.g_bInCategoryDir) {
                Global.g_bInCategoryDir = false;
                Catalogue.categoryStack.pop();
                this.m_btnDragbar.setText(Catalogue.categoryStack.peek());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Global.g_whatFolder != null) {
            int i = AnonymousClass24.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
            if (i == 1) {
                allBrowse(FileManager.m_apkFolderList, Category.apk, s_fileListView);
            } else if (i == 2) {
                allBrowse(FileManager.m_audioFolderList, Category.audio, s_fileListView);
            } else if (i == 3) {
                allBrowse(FileManager.m_documentFolderList, Category.document, s_fileListView);
            } else if (i == 4) {
                allBrowse(FileManager.m_imageFolderList, Category.image, s_fileListView);
            } else if (i == 5) {
                allBrowse(FileManager.m_videoFolderList, Category.video, s_fileListView);
            }
        }
        loadingImage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchCatagolueBrowse() {
        this.m_imageVSwitch.setImageResource(R.drawable.allshow);
        this.m_textVSwitch.setText(R.string.allshow);
        Global.g_nBrowseMethod = 1;
        if (Global.g_whatFolder != null) {
            int i = AnonymousClass24.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
            if (i == 1) {
                loadFolder(FileManager.m_apkFolderList, s_fileListView);
                return;
            }
            if (i == 2) {
                loadFolder(FileManager.m_audioFolderList, s_fileListView);
                return;
            }
            if (i == 3) {
                loadFolder(FileManager.m_documentFolderList, s_fileListView);
            } else if (i == 4) {
                loadFolder(FileManager.m_imageFolderList, s_fileListView);
            } else {
                if (i != 5) {
                    return;
                }
                loadFolder(FileManager.m_videoFolderList, s_fileListView);
            }
        }
    }

    private void initDomainPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupwindow2, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindowDomain = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup));
        this.m_popupWindowDomain.setFocusable(true);
        this.m_popupWindowDomain.setOutsideTouchable(true);
        this.m_popupWindowDomain.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_newbuild);
        this.m_linearLNewBuild = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.18
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.m_popupWindowDomain.dismiss();
                IpmsgActivity.this.newFolder();
            }
        });
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_sort);
        this.m_linearLSort2 = linearLayout2;
        linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.19
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.this.showDialog(1);
                IpmsgActivity.this.m_popupWindowDomain.dismiss();
            }
        });
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.linearl_backroot);
        this.m_linearLBackRoot = linearLayout3;
        linearLayout3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.20
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpmsgActivity.s_fileListView.backToot();
                IpmsgActivity.this.m_popupWindowDomain.dismiss();
            }
        });
    }

    private void initPastePopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popuppaste, (ViewGroup) null);
        int width = g_windowManager.getDefaultDisplay().getWidth();
        this.m_popupWindowPaste = new PopupWindow(viewInflate, -1, -2);
        Button button = (Button) viewInflate.findViewById(R.id.btn_paste);
        this.m_btnPaste = button;
        int i = width / 2;
        button.setWidth(i);
        this.m_btnPaste.setOnClickListener(new PasteClickListener());
        Button button2 = (Button) viewInflate.findViewById(R.id.btn_cancel);
        this.m_btnCancel = button2;
        button2.setWidth(i);
        this.m_btnCancel.setOnClickListener(new CancelClickListenr());
    }

    private void initSendPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupsend, (ViewGroup) null);
        m_popupWindowSend = new PopupWindow(viewInflate, -1, -2);
        Button button = (Button) viewInflate.findViewById(R.id.btn_send);
        this.m_btnPopupSend = button;
        button.setOnClickListener(new BtnSendOnClickListener());
        this.m_textVNumber = (TextView) viewInflate.findViewById(R.id.textv_number);
        Button button2 = (Button) viewInflate.findViewById(R.id.btn_print);
        this.m_btnPopupPrint = button2;
        button2.setOnClickListener(new BtnPrintOnClickListener());
        Button button3 = (Button) viewInflate.findViewById(R.id.btn_share);
        this.m_btnPopupShare = button3;
        button3.setOnClickListener(new BtnShareOnClickListener());
        Button button4 = (Button) viewInflate.findViewById(R.id.btn_showto);
        this.m_btnPopupShowTo = button4;
        button4.setOnClickListener(new BtnShowToOnClickListener());
        Button button5 = (Button) viewInflate.findViewById(R.id.btn_copy);
        this.m_btnPopupCopy = button5;
        button5.setOnClickListener(new BtnCopyOnClickListener());
        Button button6 = (Button) viewInflate.findViewById(R.id.btn_cut);
        this.m_btnPopupCut = button6;
        button6.setOnClickListener(new BtnCutOnClickListener());
        Button button7 = (Button) viewInflate.findViewById(R.id.btn_delete);
        this.m_btnPopupDelete = button7;
        button7.setOnClickListener(new BtnDeleteOnClickListener());
        Button button8 = (Button) viewInflate.findViewById(R.id.btn_rename);
        this.m_btnPopupRename = button8;
        button8.setOnClickListener(new BtnRenameOnClickListener());
        Button button9 = (Button) viewInflate.findViewById(R.id.btn_detail);
        this.m_btnPopupDetail = button9;
        button9.setOnClickListener(new BtnDetailOnClickListener());
        Button button10 = (Button) viewInflate.findViewById(R.id.btn_cancel);
        this.m_btnPopupCancel = button10;
        button10.setOnClickListener(new BtnCancelOnClickListener());
        this.m_horizontalScrollView = (HorizontalScrollView) viewInflate.findViewById(R.id.horizontalScrollView_popup);
    }

    public void showSendPopupWindow() {
        this.m_file = new File(Global.g_filePath.get(0));
        if (Global.g_whatFolder == Category.image || Global.g_whatFolder == Category.audio || Global.g_whatFolder == Category.video || Global.g_whatFolder == Category.document || Global.g_whatFolder == Category.apk) {
            if (!this.m_file.isFile()) {
                categoryAndFoler();
                return;
            } else {
                showChoicedPopup();
                return;
            }
        }
        showChoicedPopup();
    }

    private void showChoicedPopup() {
        if (Global.g_bMultipleChoice) {
            multipleChoice();
        } else if (Public_Tools.isImageFile(this.m_file.getName())) {
            singleImage();
        } else if (Public_Tools.isDocumentFile(this.m_file.getName())) {
            singleDocument();
        } else if (Public_Tools.isAudioFile(this.m_file.getName()) || Public_Tools.isVideoFile(this.m_file.getName())) {
            singleAudioAndVideo();
        } else if (this.m_file.isFile()) {
            singleChoiceOtherFile();
        } else {
            singleChoiceOtherFolder();
        }
        if (Catalogue.categoryStack.peek().equals("sdcard/")) {
            this.m_btnPopupCopy.setVisibility(8);
            this.m_btnPopupCut.setVisibility(8);
            this.m_btnPopupDelete.setVisibility(8);
            this.m_btnPopupRename.setVisibility(8);
            this.m_btnPopupDetail.setVisibility(8);
        }
    }

    private void categoryAndFoler() {
        this.m_btnPopupPrint.setVisibility(8);
        this.m_btnPopupShare.setVisibility(8);
        this.m_btnPopupCopy.setVisibility(8);
        this.m_btnPopupCut.setVisibility(8);
        this.m_btnPopupDelete.setVisibility(8);
        this.m_btnPopupRename.setVisibility(8);
        this.m_btnPopupDetail.setVisibility(8);
        this.m_btnPopupShowTo.setVisibility(8);
    }

    private void multipleChoice() {
        this.m_btnPopupPrint.setVisibility(8);
        this.m_btnPopupShare.setVisibility(8);
        this.m_btnPopupCopy.setVisibility(0);
        this.m_btnPopupCut.setVisibility(0);
        this.m_btnPopupDelete.setVisibility(0);
        this.m_btnPopupRename.setVisibility(8);
        this.m_btnPopupDetail.setVisibility(8);
        this.m_btnPopupShowTo.setVisibility(8);
    }

    private void singleChoiceOtherFile() {
        this.m_btnPopupPrint.setVisibility(8);
        this.m_btnPopupShare.setVisibility(0);
        this.m_btnPopupCopy.setVisibility(0);
        this.m_btnPopupCut.setVisibility(0);
        this.m_btnPopupDelete.setVisibility(0);
        this.m_btnPopupRename.setVisibility(0);
        this.m_btnPopupDetail.setVisibility(0);
        this.m_btnPopupShowTo.setVisibility(8);
    }

    private void singleChoiceOtherFolder() {
        this.m_btnPopupPrint.setVisibility(8);
        this.m_btnPopupShare.setVisibility(8);
        this.m_btnPopupCopy.setVisibility(0);
        this.m_btnPopupCut.setVisibility(0);
        this.m_btnPopupDelete.setVisibility(0);
        this.m_btnPopupRename.setVisibility(0);
        this.m_btnPopupDetail.setVisibility(0);
        this.m_btnPopupShowTo.setVisibility(8);
    }

    private void singleDocument() {
        this.m_btnPopupPrint.setVisibility(0);
        this.m_btnPopupShare.setVisibility(0);
        this.m_btnPopupCopy.setVisibility(0);
        this.m_btnPopupCut.setVisibility(0);
        this.m_btnPopupDelete.setVisibility(0);
        this.m_btnPopupRename.setVisibility(0);
        this.m_btnPopupDetail.setVisibility(0);
        this.m_btnPopupShowTo.setVisibility(8);
    }

    private void singleImage() {
        this.m_btnPopupPrint.setVisibility(0);
        this.m_btnPopupShare.setVisibility(0);
        this.m_btnPopupCopy.setVisibility(0);
        this.m_btnPopupCut.setVisibility(0);
        this.m_btnPopupDelete.setVisibility(0);
        this.m_btnPopupRename.setVisibility(0);
        this.m_btnPopupDetail.setVisibility(0);
        this.m_btnPopupShowTo.setVisibility(0);
    }

    private void singleAudioAndVideo() {
        this.m_btnPopupPrint.setVisibility(8);
        this.m_btnPopupShare.setVisibility(0);
        this.m_btnPopupCopy.setVisibility(0);
        this.m_btnPopupCut.setVisibility(0);
        this.m_btnPopupDelete.setVisibility(0);
        this.m_btnPopupRename.setVisibility(0);
        this.m_btnPopupDetail.setVisibility(0);
        this.m_btnPopupShowTo.setVisibility(0);
    }

    private class BtnSendOnClickListener implements View.OnClickListener {
        private BtnSendOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.send(null);
        }
    }

    private class BtnPrintOnClickListener implements View.OnClickListener {
        private BtnPrintOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.print(Global.g_filePath.get(0));
        }
    }

    private class BtnShareOnClickListener implements View.OnClickListener {
        private BtnShareOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Public_Tools.share(IpmsgActivity.this, Global.g_filePath.get(0));
        }
    }

    private class BtnShowToOnClickListener implements View.OnClickListener {
        private BtnShowToOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(IpmsgActivity.this, (Class<?>) ChoiceRemotePlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", Global.g_filePath.get(0));
            intent.putExtras(bundle);
            IpmsgActivity.this.startActivity(intent);
        }
    }

    private class BtnCopyOnClickListener implements View.OnClickListener {
        private BtnCopyOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.m_file = new File(Global.g_filePath.get(0));
            FileAccessAuth fileAccessAuthAuthority = Public_Tools.authority(IpmsgActivity.this.m_file);
            if (Global.g_bWaitPaste != 2) {
                IpmsgActivity.this.copy(fileAccessAuthAuthority);
                IpmsgActivity.m_popupWindowSend.dismiss();
            } else {
                IpmsgActivity.this.toast(R.string.pasting);
            }
        }
    }

    private class BtnCutOnClickListener implements View.OnClickListener {
        private BtnCutOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.m_file = new File(Global.g_filePath.get(0));
            FileAccessAuth fileAccessAuthAuthority = Public_Tools.authority(IpmsgActivity.this.m_file);
            if (Global.g_bWaitPaste != 2) {
                IpmsgActivity.this.cut(fileAccessAuthAuthority);
                IpmsgActivity.m_popupWindowSend.dismiss();
            } else {
                IpmsgActivity.this.toast(R.string.pasting);
            }
        }
    }

    private class BtnDeleteOnClickListener implements View.OnClickListener {
        private BtnDeleteOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.m_file = new File(Global.g_filePath.get(0));
            IpmsgActivity.this.delete(Global.g_filePath.get(0), Public_Tools.authority(IpmsgActivity.this.m_file));
        }
    }

    private class BtnRenameOnClickListener implements View.OnClickListener {
        private BtnRenameOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.m_file = new File(Global.g_filePath.get(0));
            IpmsgActivity.this.longClickRename(Global.g_filePath.get(0), Public_Tools.authority(IpmsgActivity.this.m_file));
        }
    }

    private class BtnDetailOnClickListener implements View.OnClickListener {
        private BtnDetailOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.this.m_file = new File(Global.g_filePath.get(0));
            IpmsgActivity ipmsgActivity = IpmsgActivity.this;
            IpmsgActivity ipmsgActivity2 = IpmsgActivity.this;
            ipmsgActivity.m_detailDialog = new DetailDialog(ipmsgActivity2, ipmsgActivity2.m_file);
            IpmsgActivity.this.m_detailDialog.show();
        }
    }

    private class BtnCancelOnClickListener implements View.OnClickListener {
        private BtnCancelOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IpmsgActivity.m_popupWindowSend.dismiss();
            IpmsgActivity.this.cacelChoiced();
        }
    }

    public void cacelChoiced() {
        for (int i = 0; i < s_fileListView.getFileAdapter().getFileList().size(); i++) {
            s_fileListView.getFileAdapter().getFileList().get(i).setCheck(false);
            Global.g_filePath.clear();
        }
        s_fileListView.getFileAdapter().notifyDataSetChanged();
    }

    private class PasteClickListener implements View.OnClickListener {
        private PasteClickListener() {
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [com.netfeige.display.ui.IpmsgActivity$PasteClickListener$1] */
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            new Thread() { // from class: com.netfeige.display.ui.IpmsgActivity.PasteClickListener.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    IpmsgActivity.this.pasteOperation();
                    super.run();
                }
            }.start();
            IpmsgActivity.this.cancelChoice();
            IpmsgActivity.this.m_popupWindowPaste.dismiss();
            IpmsgActivity.s_fileListView.goneText();
        }
    }

    private class CancelClickListenr implements View.OnClickListener {
        private CancelClickListenr() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Global.g_bWaitPaste = 0;
            Global.g_pastePaths.clear();
            IpmsgActivity.this.cancelChoice();
            IpmsgActivity.this.m_popupWindowPaste.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelChoice() {
        Global.g_filePath.clear();
        for (int i = 0; i < s_fileListView.getFileAdapter().getFileList().size(); i++) {
            s_fileListView.getFileAdapter().getFileList().get(i).setCheck(false);
        }
        s_fileListView.getFileAdapter().notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pasteOperation() {
        Global.g_bWaitPaste = 2;
        FileManager.m_strPrompt = "";
        Global.g_bIsRepeatName = false;
        String currentPath = getCurrentPath();
        int size = Global.g_pastePaths.size();
        if (size > 0) {
            if (FileManager.g_filePathForCopy != "") {
                pasteSelectedFile(currentPath, size, FileManager.g_filePathForCopy);
            } else if (FileManager.g_filePathForCut != "") {
                pasteSelectedFile(currentPath, size, FileManager.g_filePathForCut);
            }
        } else {
            pasteNoSelectedFile(currentPath);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isRepeat", Global.g_bIsRepeatName);
        handlerSendMsg(this.PasteHandler, 0, bundle);
    }

    private void pasteSelectedFile(String str, int i, String str2) {
        try {
            if (!s_fileListView.isInner(str2, Global.g_pastePaths)) {
                afterPaste(FileManager.getIFileManager(this).pasteFile(str2, str));
                return;
            }
            for (int i2 = 0; i2 < i; i2++) {
                afterPaste(FileManager.getIFileManager(this).pasteFile(Global.g_pastePaths.get(i2), str));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void handlerSendMsg(Handler handler, int i, Bundle bundle) {
        Message messageObtainMessage = handler.obtainMessage();
        messageObtainMessage.what = i;
        messageObtainMessage.setData(bundle);
        handler.sendMessage(messageObtainMessage);
    }

    private void pasteNoSelectedFile(String str) {
        if (FileManager.g_filePathForCopy != "") {
            afterPaste(FileManager.getIFileManager(this).pasteFile(FileManager.g_filePathForCopy, str));
        } else if (FileManager.g_filePathForCut != "") {
            afterPaste(FileManager.getIFileManager(this).pasteFile(FileManager.g_filePathForCut, str));
        }
    }

    private void afterPaste(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isPaste", z);
        handlerSendMsg(this.PasteHandler, 1, bundle);
    }

    private String getCurrentPath() {
        String strPeek = Catalogue.categoryStack.peek();
        if (strPeek.equals("feige/")) {
            return Public_Tools.getDefaultDownloadPath();
        }
        return strPeek.equals("sdcard/") ? Public_Tools.getSDCardPath() : strPeek;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void newFolder() {
        final NewDialog newDialog = new NewDialog(this);
        newDialog.getEditTFolderName().requestFocus();
        newDialog.getEditTFolderName().setFocusable(true);
        newDialog.getEditTFolderName().setFocusableInTouchMode(true);
        new Timer().schedule(new TimerTask() { // from class: com.netfeige.display.ui.IpmsgActivity.22
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                ((InputMethodManager) newDialog.getEditTFolderName().getContext().getSystemService("input_method")).showSoftInput(newDialog.getEditTFolderName(), 0);
            }
        }, 600L);
        newDialog.show();
    }

    public void keepDialog(boolean z, DialogInterface dialogInterface) {
        try {
            Field declaredField = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            declaredField.setAccessible(true);
            declaredField.set(dialogInterface, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSameName(String str) {
        int size = s_fileListView.getFileAdapter().getFileList().size();
        for (int i = 0; i < size; i++) {
            if (s_fileListView.getFileAdapter().getFileList().get(i).isFloder && str.equals(s_fileListView.getFileAdapter().getFileList().get(i).getName())) {
                return true;
            }
        }
        return false;
    }

    public void newBuild(String str) {
        String str2;
        String strPeek = Catalogue.categoryStack.peek();
        if (strPeek.equals("feige/")) {
            str2 = Public_Tools.getDefaultDownloadPath() + ServiceReference.DELIMITER + str;
        } else if (strPeek.equals("sdcard/")) {
            str2 = Public_Tools.getSDCardPath() + ServiceReference.DELIMITER + str;
        } else {
            str2 = strPeek + str;
        }
        if (FileManager.getIFileManager(this).createFolder(str2)) {
            s_fileListView.getFileAdapter().getFileList().add(Public_Tools.path2FileInfo(str2));
            s_fileListView.getFileAdapter().notifyDataSetChanged();
            Public_Tools.fileCompare(s_fileListView.getFileAdapter().getFileList());
            toast(R.string.new_sucess);
            return;
        }
        toast(R.string.nocread);
    }

    private void loadFolder(ArrayList<String> arrayList, FileListView fileListView) {
        fileListView.getFileAdapter().getFileList().clear();
        Public_Tools.backForward(fileListView.getFileAdapter().getFileList());
        for (int i = 0; i < arrayList.size(); i++) {
            Public_Tools.path2FileInfoForMian(arrayList.get(i), fileListView);
        }
    }

    public void allBrowse(ArrayList<String> arrayList, Category category, FileListView fileListView) {
        try {
            ImageAdapter.s_imageList.clear();
            IpmsgApplication.g_arrMusicList.clear();
            IpmsgApplication.g_arrVideoList.clear();
            s_fileListView.getFileAdapter().getFileList().clear();
            Public_Tools.backForward(s_fileListView.getFileAdapter().getFileList());
            for (int i = 0; i < arrayList.size(); i++) {
                File file = new File(arrayList.get(i));
                if (file.exists() && file.isDirectory()) {
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        specialFilling(category, fileArrListFiles, i2, fileListView);
                    }
                }
            }
            Public_Tools.imageCompare(ImageAdapter.s_imageList);
            Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
            Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void specialFilling(Category category, File[] fileArr, int i, FileListView fileListView) {
        if (fileArr[i].isHidden()) {
            return;
        }
        int i2 = AnonymousClass24.$SwitchMap$com$netfeige$common$Category[category.ordinal()];
        if (i2 == 1) {
            if (Public_Tools.isApkFile(fileArr[i].getName())) {
                addData(fileArr, i, fileListView);
                return;
            }
            return;
        }
        if (i2 == 2) {
            if (Public_Tools.isAudioFile(fileArr[i].getName())) {
                addData(fileArr, i, fileListView);
                IpmsgApplication.g_arrMusicList.add(new Music(0, fileArr[i].getName(), fileArr[i].getAbsolutePath(), -1L, fileArr[i].lastModified()));
                return;
            }
            return;
        }
        if (i2 == 3) {
            if (Public_Tools.isDocumentFile(fileArr[i].getName())) {
                addData(fileArr, i, fileListView);
            }
        } else {
            if (i2 == 4) {
                if (Public_Tools.isImageFile(fileArr[i].getName())) {
                    addData(fileArr, i, fileListView);
                    ImageAdapter.s_imageList.add(new ImagePreview(0, fileArr[i].getName(), fileArr[i].getAbsolutePath(), null, fileArr[i].lastModified()));
                    return;
                }
                return;
            }
            if (i2 == 5 && Public_Tools.isVideoFile(fileArr[i].getName())) {
                addData(fileArr, i, fileListView);
                IpmsgApplication.g_arrVideoList.add(new Music(0, fileArr[i].getName(), fileArr[i].getAbsolutePath(), -1L, fileArr[i].lastModified()));
            }
        }
    }

    private void addData(File[] fileArr, int i, FileListView fileListView) {
        fileListView.getFileAdapter().getFileList().add(Public_Tools.path2FileInfo(fileArr[i].getAbsolutePath()));
    }

    public void toast(int i) {
        Toast.makeText(this, i, 0).show();
    }

    public Button getBtnMore() {
        return this.m_btnMore;
    }

    public DragEnum getDragEnum() {
        return this.m_dragEnum;
    }

    public void setDragEnum(DragEnum dragEnum) {
        this.m_dragEnum = dragEnum;
    }

    public Button getBtnParentFolder() {
        return this.m_btnParentFolder;
    }

    public LinearLayout getLinearLDragBar() {
        return this.m_linearLDragBar;
    }

    public HashMap<String, Integer> getHashMapKeepPosition() {
        return this.m_hashMapKeepPosition;
    }

    public AlertDialog.Builder getM_compressBuilder() {
        return this.m_compressBuilder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPromptDialog(String str) {
        TextView textView = new TextView(this);
        textView.setText(str);
        this.m_dialogPrompt.setTitle(R.string.notify).setIcon(android.R.drawable.ic_dialog_info).setView(textView).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.netfeige.display.ui.IpmsgActivity.23
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public PopupWindow getPopupWindowPaste() {
        return this.m_popupWindowPaste;
    }

    public TextView getTextVContent() {
        return this.m_textVContent;
    }

    public DragProcess getDragProcess() {
        return this.m_dragProcess;
    }

    public TextView getTextVNumber() {
        return this.m_textVNumber;
    }

    public View getViewFramel() {
        return this.m_viewFramel;
    }

    public File getFile() {
        return this.m_file;
    }

    public void setNewName(String str) {
        this.m_strNewName = str;
    }

    public String getNewName() {
        return this.m_strNewName;
    }

    private int getStatusBarHeight() {
        try {
            Class<?> cls = Class.forName("com.android.internal.R$dimen");
            return getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height").get(cls.newInstance()).toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public HorizontalScrollView getHorizontalScrollView() {
        return this.m_horizontalScrollView;
    }

    @Override // com.netfeige.service.IpmsgService.EntryBroadcastEventHandler
    public void onEntryBroadcastEventHandler(boolean z) {
        this.m_btnRefresh.setEnabled(z);
    }

    @Override // com.netfeige.service.IpmsgService.DiscussInviteEventHandler
    public void onInvite() {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 2;
        messageObtain.arg1 = 2;
        messageObtain.sendToTarget();
    }

    @Override // com.netfeige.service.IpmsgService.DiscussExitEventHandler
    public void onDiscussExit() {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 2;
        messageObtain.arg1 = 2;
        messageObtain.sendToTarget();
    }

    public void discussUnreadPrompt() {
        this.mImgDiscussPrompt.setVisibility(8);
        for (int i = 0; i < this.m_ipmsgApp.ipmsgService.mListDiscuss.size(); i++) {
            if (this.m_ipmsgApp.ipmsgService.mListDiscuss.get(i).mBUnreadMsg) {
                this.mImgDiscussPrompt.setVisibility(0);
                return;
            }
        }
        if (this.m_ipmsgApp.ipmsgService.mListInviteDiscuss.size() > 0) {
            this.mImgDiscussPrompt.setVisibility(0);
        } else if (this.m_ipmsgApp.ipmsgService.mListExitDiscuss.size() > 0) {
            this.mImgDiscussPrompt.setVisibility(0);
        }
    }
}

