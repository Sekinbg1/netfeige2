package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.DBHelper;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.ShareFiles;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MyShareFileAdapter;
import com.netfeige.display.ui.dialog.SetMySharePwdDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import java.util.ArrayList;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class MyShareActivity extends Activity {
    private Button m_btnBack;
    private Button m_btnCancelPopupOperate;
    private Button m_btnShareAreaPopupOperate;
    private FrameLayout m_fLayoutShareAreaPopupOperate;
    private HorizontalScrollView m_hScrollView;
    private LinearLayout m_linearLPopupAdd;
    private LinearLayout m_linearLPopupPwd;
    private IpmsgApplication m_myApp;
    private MyShareFileAdapter m_myShareFileAdapter;
    private MyShareListView m_myShareListView;
    private int m_nPopX;
    private int m_nPopY;
    private PopupWindow m_popupWindow;
    private PopupWindow m_popupWindowOperate;
    private TextView m_textVNumberPopupOperate;
    private Button m_btnPopup = null;
    private LayoutInflater m_inflater = null;
    private ArrayList<ShareFiles> m_myShareFilesList = new ArrayList<>();
    private ArrayList<ShareFiles> m_selectedFilesList = new ArrayList<>();
    public ShareFiles m_waitModifyShareFile = null;
    private final int m_nPopUpX = 5;
    private final int m_nPopUpY = 75;
    private final int REQUEST_CODE = 1;
    private String m_strMacs = null;
    private Handler m_handler = new Handler() { // from class: com.netfeige.display.ui.MyShareActivity.4
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            MyShareActivity.this.initData();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.myshare);
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        Button button = (Button) findViewById(R.id.back_btn_myshare);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.MyShareActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MyShareActivity.this.onBackPressed();
            }
        });
        this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
        this.m_nPopY = Public_Tools.dip2px(this, 75.0f);
        Button button2 = (Button) findViewById(R.id.popup_btn_myshare);
        this.m_btnPopup = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.MyShareActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MyShareActivity.this.m_popupWindow.showAtLocation(MyShareActivity.this.findViewById(R.id.top_navigation_layout_myshare), 53, MyShareActivity.this.m_nPopX, MyShareActivity.this.m_nPopY);
            }
        });
        this.m_myShareListView = (MyShareListView) findViewById(R.id.list_myshare);
        MyShareFileAdapter myShareFileAdapter = new MyShareFileAdapter(this, this.m_myShareFilesList);
        this.m_myShareFileAdapter = myShareFileAdapter;
        this.m_myShareListView.setAdapter((ListAdapter) myShareFileAdapter);
        this.m_myShareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.MyShareActivity.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (MyShareActivity.this.m_selectedFilesList.size() > 0) {
                    View viewFindViewById = view.findViewById(R.id.file_select_box_fileitem);
                    MyShareActivity.this.m_myShareFileAdapter.checkFileOperate(viewFindViewById, Integer.parseInt(viewFindViewById.getTag().toString()));
                    MyShareActivity.this.showPopupOperate();
                }
            }
        });
        initData();
        initPopupWindow();
        initPopupWindowOperate();
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_myApp.currentActivity = this;
        addMyShareFile();
        if (this.m_myApp.g_bBackRuning) {
            this.m_myApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
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
        if (this.m_myApp.currentActivity == this) {
            this.m_myApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_myApp, getString(R.string.app_name), DataConfig.getInstance(this).Read(0), R.drawable.ic_launcher);
        }
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.m_popupWindowOperate.dismiss();
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && i2 == -1) {
            try {
                this.m_strMacs = intent.getExtras().getString("strMacs");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initData() {
        this.m_myShareFilesList.clear();
        this.m_myShareFilesList.addAll(DBHelper.getInstance(this).getShareFilesRecord(null, null));
        this.m_myShareFileAdapter.notifyDataSetChanged();
    }

    private void initPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popup_myshare, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup2));
        this.m_popupWindow.setFocusable(true);
        this.m_popupWindow.setOutsideTouchable(true);
        this.m_popupWindow.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.add_linearl_popup_myshare);
        this.m_linearLPopupAdd = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowAddOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.pwd_linearl_popup_myshare);
        this.m_linearLPopupPwd = linearLayout2;
        linearLayout2.setOnClickListener(new PopUpWindowPwdOnClickListener());
    }

    private class PopUpWindowAddOnClickListener implements View.OnClickListener {
        private PopUpWindowAddOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!FileManager.externalMemoryAvailable()) {
                FileActivity.s_whatFolder = Category.feige;
            } else {
                FileActivity.s_whatFolder = Category.sdcard;
            }
            if (MyShareActivity.this.m_selectedFilesList.size() > 0) {
                for (int i = 0; i < MyShareActivity.this.m_selectedFilesList.size(); i++) {
                    ((ShareFiles) MyShareActivity.this.m_selectedFilesList.get(i)).setM_bIsCheck(false);
                }
                MyShareActivity.this.m_selectedFilesList.clear();
                MyShareActivity.this.m_popupWindowOperate.dismiss();
                MyShareActivity.this.m_myShareFileAdapter.notifyDataSetChanged();
            }
            Intent intent = new Intent(MyShareActivity.this, (Class<?>) FileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("flag", "myshare");
            intent.putExtras(bundle);
            MyShareActivity.this.startActivity(intent);
            Global.g_isInFileActivity = true;
            MyShareActivity.this.m_popupWindow.dismiss();
        }
    }

    private class PopUpWindowPwdOnClickListener implements View.OnClickListener {
        private PopUpWindowPwdOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            new SetMySharePwdDialog(MyShareActivity.this).show();
            MyShareActivity.this.m_popupWindow.dismiss();
        }
    }

    private void initPopupWindowOperate() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupoperate, (ViewGroup) null);
        this.m_popupWindowOperate = new PopupWindow(viewInflate, -1, -2);
        this.m_fLayoutShareAreaPopupOperate = (FrameLayout) viewInflate.findViewById(R.id.first_flayout_popupoperate);
        this.m_btnShareAreaPopupOperate = (Button) viewInflate.findViewById(R.id.first_btn_popupoperate);
        this.m_fLayoutShareAreaPopupOperate.setVisibility(0);
        this.m_btnShareAreaPopupOperate.setText(getResources().getString(R.string.sharearea));
        this.m_btnShareAreaPopupOperate.setOnClickListener(new PopUpOperateShareAreaOnClickListener());
        this.m_textVNumberPopupOperate = (TextView) viewInflate.findViewById(R.id.number_textv_popupoperate);
        Button button = (Button) viewInflate.findViewById(R.id.second_btn_popupoperate);
        this.m_btnCancelPopupOperate = button;
        button.setVisibility(0);
        this.m_btnCancelPopupOperate.setText(getResources().getString(R.string.cancelshare));
        this.m_btnCancelPopupOperate.setOnClickListener(new PopUpOperateCancelOnClickListener());
        this.m_hScrollView = (HorizontalScrollView) viewInflate.findViewById(R.id.hscrollview_popup_operate);
    }

    private class PopUpOperateShareAreaOnClickListener implements View.OnClickListener {
        private PopUpOperateShareAreaOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MyShareActivity.this.shareAreaDispose();
        }
    }

    public void shareAreaDispose() {
        String m_strMACList;
        Intent intent = new Intent(this, (Class<?>) ChoiceUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", null);
        bundle.putString("activity", "MyShareActivity");
        ShareFiles shareFiles = this.m_waitModifyShareFile;
        if (shareFiles != null) {
            m_strMACList = shareFiles.getM_strMACList();
            for (int i = 0; i < this.m_selectedFilesList.size(); i++) {
                this.m_selectedFilesList.get(i).setM_bIsCheck(false);
            }
            this.m_selectedFilesList.clear();
            this.m_popupWindowOperate.dismiss();
            this.m_myShareFileAdapter.notifyDataSetChanged();
        } else {
            m_strMACList = "";
        }
        if (this.m_selectedFilesList.size() == 1) {
            m_strMACList = this.m_selectedFilesList.get(0).getM_strMACList();
        }
        bundle.putString("strMacList", m_strMACList);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    private class PopUpOperateCancelOnClickListener implements View.OnClickListener {
        private PopUpOperateCancelOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            for (int i = 0; i < MyShareActivity.this.m_selectedFilesList.size(); i++) {
                DBHelper.getInstance(MyShareActivity.this).deleteShareFilesRecord("ID = ?", new String[]{String.valueOf(((ShareFiles) MyShareActivity.this.m_selectedFilesList.get(i)).getM_iID())});
                MyShareActivity.this.m_myShareFilesList.remove(MyShareActivity.this.m_selectedFilesList.get(i));
            }
            MyShareActivity.this.m_selectedFilesList.clear();
            MyShareActivity.this.m_popupWindowOperate.dismiss();
            MyShareActivity.this.m_myShareFileAdapter.notifyDataSetChanged();
        }
    }

    private void addMyShareFile() {
        String str;
        if (this.m_myApp.waitSendfiles.size() > 0 && this.m_strMacs == null) {
            Intent intent = new Intent(this, (Class<?>) ChoiceUserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", null);
            bundle.putString("activity", "MyShareActivity");
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        } else {
            ShareFiles shareFiles = this.m_waitModifyShareFile;
            if (shareFiles != null && (str = this.m_strMacs) != null) {
                shareFiles.setM_strMACList(str);
                this.m_waitModifyShareFile.setM_bIsCheck(false);
                ContentValues contentValues = new ContentValues();
                contentValues.put("MAClist", this.m_strMacs);
                DBHelper.getInstance(this).updateShareFilesRecord(contentValues, "ID = ?", new String[]{String.valueOf(this.m_waitModifyShareFile.getM_iID())});
                this.m_myShareFileAdapter.notifyDataSetChanged();
                this.m_strMacs = null;
            } else if (this.m_selectedFilesList.size() > 0 && this.m_strMacs != null) {
                for (int i = 0; i < this.m_selectedFilesList.size(); i++) {
                    this.m_selectedFilesList.get(i).setM_strMACList(this.m_strMacs);
                    this.m_selectedFilesList.get(i).setM_bIsCheck(false);
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("MAClist", this.m_strMacs);
                    DBHelper.getInstance(this).updateShareFilesRecord(contentValues2, "ID = ?", new String[]{String.valueOf(this.m_selectedFilesList.get(i).getM_iID())});
                }
                this.m_selectedFilesList.clear();
                this.m_popupWindowOperate.dismiss();
                this.m_myShareFileAdapter.notifyDataSetChanged();
                this.m_strMacs = null;
            } else if (this.m_myApp.waitSendfiles.size() > 0) {
                FileManager.getIFileManager(this).addMyShareRecord(this.m_strMacs, this, new Vector<>(this.m_myApp.waitSendfiles));
                this.m_strMacs = null;
                this.m_myApp.waitSendfiles.clear();
            }
        }
        this.m_waitModifyShareFile = null;
    }

    public Handler getM_handler() {
        return this.m_handler;
    }

    public ArrayList<ShareFiles> getM_selectedFilesList() {
        return this.m_selectedFilesList;
    }

    public void showPopupOperate() {
        if (this.m_selectedFilesList.size() > 0) {
            if (!this.m_popupWindowOperate.isShowing()) {
                this.m_popupWindowOperate.showAtLocation(findViewById(R.id.llayout_myshare), 80, 0, 0);
                HorizontalScrollView horizontalScrollView = this.m_hScrollView;
                horizontalScrollView.scrollTo(horizontalScrollView.getRight(), 0);
                HorizontalScrollView horizontalScrollView2 = this.m_hScrollView;
                horizontalScrollView2.smoothScrollTo(0, horizontalScrollView2.getHeight());
            }
            this.m_textVNumberPopupOperate.setText(String.valueOf(this.m_selectedFilesList.size()));
            return;
        }
        if (this.m_popupWindowOperate.isShowing()) {
            this.m_popupWindowOperate.dismiss();
        }
    }
}

