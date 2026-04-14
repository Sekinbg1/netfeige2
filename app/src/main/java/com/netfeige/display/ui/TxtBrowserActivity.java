package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.dialog.ShowTxtDeleteDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.filemanager.FileManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes.dex */
public class TxtBrowserActivity extends Activity {
    private static final String DEFULTECODE = "GB2312";
    private static final String GB2312 = "GB2312";
    private Button m_btnBack;
    private IpmsgApplication m_ipmsgApp;
    private LinearLayout m_linearLDelete;
    private LinearLayout m_linearLDelete2;
    private LinearLayout m_linearLPopupSend;
    private LinearLayout m_linearLPopupSend2;
    private LinearLayout m_linearLPrint;
    private LinearLayout m_linearLShare;
    private int m_nPopX;
    private int m_nPopY;
    private PopupWindow m_popupWindow;
    private PopupWindow m_popupWindow2;
    private ShowTxtDeleteDialog m_showTxtDeleteDialog;
    private String m_strFilePath;
    private TextView m_textVContent;
    private TextView m_textVFilename;
    private Button m_btnPopup = null;
    private LayoutInflater m_inflater = null;
    private final int m_nPopUpX = 5;
    private final int m_nPopUpY = 75;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.txtbrowser);
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        this.m_strFilePath = getIntent().getExtras().getString("filePath");
        Button button = (Button) findViewById(R.id.back_txtbrowser_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.TxtBrowserActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TxtBrowserActivity.this.onBackPressed();
            }
        });
        this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
        this.m_nPopY = Public_Tools.dip2px(this, 75.0f);
        Button button2 = (Button) findViewById(R.id.popup_btn);
        this.m_btnPopup = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.TxtBrowserActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                View viewFindViewById = TxtBrowserActivity.this.findViewById(R.id.top_txtbrowser_layout);
                if (Global.g_isInFileActivity) {
                    TxtBrowserActivity.this.m_popupWindow2.showAtLocation(viewFindViewById, 53, TxtBrowserActivity.this.m_nPopX, TxtBrowserActivity.this.m_nPopY);
                } else {
                    TxtBrowserActivity.this.m_popupWindow.showAtLocation(viewFindViewById, 53, TxtBrowserActivity.this.m_nPopX, TxtBrowserActivity.this.m_nPopY);
                }
            }
        });
        initPopupWindow();
        initPopupWindow2();
        this.m_showTxtDeleteDialog = new ShowTxtDeleteDialog(this);
        this.m_textVFilename = (TextView) findViewById(R.id.filename_txtbrowser_textv);
        this.m_textVContent = (TextView) findViewById(R.id.view_contents);
        refreshGUI("GB2312");
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    private void refreshGUI(String str) {
        this.m_textVContent.setText(getStringFromFile(str));
    }

    private String getStringFromFile(String str) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.m_strFilePath), str));
            File file = new File(this.m_strFilePath);
            if (!file.exists()) {
                bufferedReader.close();
                return null;
            }
            this.m_textVFilename.setText(file.getName());
            while (bufferedReader.ready()) {
                stringBuffer.append(bufferedReader.readLine() + "\n");
            }
            bufferedReader.close();
            return stringBuffer.toString();
        } catch (Exception unused) {
            return null;
        }
    }

    private void initPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupimagepreview, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup3));
        this.m_popupWindow.setFocusable(true);
        this.m_popupWindow.setOutsideTouchable(true);
        this.m_popupWindow.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_send);
        this.m_linearLPopupSend = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowSendOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_print);
        this.m_linearLPrint = linearLayout2;
        linearLayout2.setOnClickListener(new PopUpWindowPrintOnClickListener());
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.linearl_share);
        this.m_linearLShare = linearLayout3;
        linearLayout3.setOnClickListener(new PopUpWindowShareOnClickListener());
        ((LinearLayout) viewInflate.findViewById(R.id.linearl_screeninteract)).setVisibility(8);
        LinearLayout linearLayout4 = (LinearLayout) viewInflate.findViewById(R.id.linearl_delete);
        this.m_linearLDelete = linearLayout4;
        linearLayout4.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.TxtBrowserActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((IpmsgApplication) TxtBrowserActivity.this.getApplication()).g_strDelFilePrompt.equals(ContentTree.VIDEO_ID)) {
                    TxtBrowserActivity.this.m_showTxtDeleteDialog.show();
                } else {
                    TxtBrowserActivity.this.deleteTxtFile();
                }
                TxtBrowserActivity.this.m_popupWindow.dismiss();
            }
        });
    }

    private class PopUpWindowSendOnClickListener implements View.OnClickListener {
        private PopUpWindowSendOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(TxtBrowserActivity.this, (Class<?>) ChoiceUserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", TxtBrowserActivity.this.m_strFilePath);
            bundle.putString("activity", "TxtBrowserActivity");
            intent.putExtras(bundle);
            TxtBrowserActivity.this.startActivity(intent);
            TxtBrowserActivity.this.m_popupWindow.dismiss();
        }
    }

    private class PopUpWindowPrintOnClickListener implements View.OnClickListener {
        private PopUpWindowPrintOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TxtBrowserActivity txtBrowserActivity = TxtBrowserActivity.this;
            txtBrowserActivity.print(txtBrowserActivity.m_strFilePath);
            TxtBrowserActivity.this.m_popupWindow.dismiss();
        }
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

    private class PopUpWindowShareOnClickListener implements View.OnClickListener {
        private PopUpWindowShareOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TxtBrowserActivity txtBrowserActivity = TxtBrowserActivity.this;
            Public_Tools.share(txtBrowserActivity, txtBrowserActivity.m_strFilePath);
            TxtBrowserActivity.this.m_popupWindow.dismiss();
        }
    }

    public void deleteTxtFile() {
        int positionOnPath;
        try {
            if (FileManager.getIFileManager(this).deleteFile(new File(this.m_strFilePath))) {
                if (Global.g_isInFileActivity) {
                    positionOnPath = Public_Tools.getPositionOnPath(FileActivity.s_choiceListView.getFileAdapter().getFileList(), this.m_strFilePath);
                } else {
                    positionOnPath = Public_Tools.getPositionOnPath(IpmsgActivity.s_fileListView.getFileAdapter().getFileList(), this.m_strFilePath);
                }
                if (positionOnPath != -1) {
                    if (Global.g_isInFileActivity) {
                        FileActivity.s_choiceListView.getFileAdapter().getFileList().remove(positionOnPath);
                        FileActivity.s_choiceListView.getFileAdapter().notifyDataSetChanged();
                    } else {
                        IpmsgActivity.s_fileListView.getFileAdapter().getFileList().remove(positionOnPath);
                        IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
                    }
                }
                onBackPressed();
                return;
            }
            Toast.makeText(this, R.string.not_delete_notify, 0).show();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void initPopupWindow2() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupimagepreview2, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow2 = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup2));
        this.m_popupWindow2.setFocusable(true);
        this.m_popupWindow2.setOutsideTouchable(true);
        this.m_popupWindow2.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_delete);
        this.m_linearLDelete2 = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.TxtBrowserActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((IpmsgApplication) TxtBrowserActivity.this.getApplication()).g_strDelFilePrompt.equals(ContentTree.VIDEO_ID)) {
                    TxtBrowserActivity.this.m_showTxtDeleteDialog.show();
                } else {
                    TxtBrowserActivity.this.deleteTxtFile();
                }
                TxtBrowserActivity.this.m_popupWindow2.dismiss();
            }
        });
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_send);
        this.m_linearLPopupSend2 = linearLayout2;
        linearLayout2.setOnClickListener(new LinearSendClickListener());
        ((LinearLayout) viewInflate.findViewById(R.id.linearl_screeninteraction2)).setVisibility(8);
    }

    private class LinearSendClickListener implements View.OnClickListener {
        private LinearSendClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                TxtBrowserActivity.this.m_ipmsgApp.waitSendfiles.clear();
                TxtBrowserActivity.this.m_ipmsgApp.waitSendfiles.add(TxtBrowserActivity.this.m_strFilePath);
                TxtBrowserActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, TxtBrowserActivity.this.m_ipmsgApp.waitSendfiles, IpmsgApplication.g_currentHostInfo, TxtBrowserActivity.this.m_ipmsgApp.ipmsgService, null, 0);
                Toast.makeText(TxtBrowserActivity.this, R.string.notifysendout, 0).show();
                TxtBrowserActivity.this.m_popupWindow2.dismiss();
                TxtBrowserActivity.this.m_ipmsgApp.waitSendfiles.clear();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}

