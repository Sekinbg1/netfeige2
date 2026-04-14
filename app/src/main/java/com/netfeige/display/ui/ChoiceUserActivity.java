package com.netfeige.display.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.display.data.ChoiceUserAdpter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.ui.dialog.ExitDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.service.IpmsgService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class ChoiceUserActivity extends Activity implements IpmsgService.EventHandler {
    private ArrayList<HostInformation> m_arrayLHosts;
    private Button m_btnBack;
    private Button m_btnSelectPopupOperate;
    private Button m_btnSend;
    private Button m_btnSwitchPopupOperate;
    private ChoiceUserAdpter m_choiceUserAdpter;
    private ExitDialog m_exitDialog;
    private FrameLayout m_fLayoutSelectPopupOperate;
    private HorizontalScrollView m_hScrollView;
    private IpmsgApplication m_ipmsgApp;
    private ListView m_listVUser;
    private PopupWindow m_popupWindowOperate;
    private String m_strActivity;
    private String m_strFilePath;
    private TextView m_textVNumberPopupOperate;
    private Vector<String> m_vecWaitSendfiles;
    private LayoutInflater m_inflater = null;
    private boolean m_bIsShare = false;
    private String m_strPath = null;
    private ServiceConnection Conn = new ServiceConnection() { // from class: com.netfeige.display.ui.ChoiceUserActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ChoiceUserActivity.this.m_ipmsgApp.ipmsgService = ((IpmsgService.LocalBinder) iBinder).getService();
            ChoiceUserActivity.this.initSomeData();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            ChoiceUserActivity.this.m_ipmsgApp.ipmsgService = null;
        }
    };

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str) {
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.choiceuserlist);
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        this.m_exitDialog = new ExitDialog(this, this.m_ipmsgApp);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.m_strFilePath = extras.getString("filePath");
            this.m_strActivity = extras.getString("activity");
        }
        initControl();
        String str = this.m_strActivity;
        if (str != null && str.equals("MyShareActivity")) {
            initPopupWindowOperate();
        }
        recIntent();
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

    private void recIntent() {
        Intent intent = getIntent();
        if (intent != null && "android.intent.action.SEND".equals(intent.getAction())) {
            this.m_bIsShare = true;
            Uri uri = (Uri) intent.getExtras().get("android.intent.extra.STREAM");
            if (uri != null) {
                this.m_strPath = getFileNameByUri(uri);
            }
            if (Global.g_bWorking) {
                initSomeData();
                return;
            } else {
                startService();
                return;
            }
        }
        initData();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initSomeData() {
        this.m_ipmsgApp.ipmsgService.ehList.add(this);
        ChoiceUserAdpter choiceUserAdpter = new ChoiceUserAdpter(this, R.layout.choiceuseritem, this.m_ipmsgApp.ipmsgService.userList);
        this.m_choiceUserAdpter = choiceUserAdpter;
        this.m_listVUser.setAdapter((ListAdapter) choiceUserAdpter);
        Vector<String> vector = new Vector<>();
        this.m_vecWaitSendfiles = vector;
        String str = this.m_strPath;
        if (str != null) {
            vector.add(str);
        }
        this.m_arrayLHosts = new ArrayList<>();
        this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.entryService(new ArrayList<>(this.m_ipmsgApp.ipmsgService.userList), false);
    }

    private void startService() {
        if (this.m_ipmsgApp.ipmsgService == null) {
            Intent intent = new Intent(this, (Class<?>) IpmsgService.class);
            startService(intent);
            bindService(intent, this.Conn, 1);
        }
    }

    private void initPopupWindowOperate() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupoperate, (ViewGroup) null);
        this.m_popupWindowOperate = new PopupWindow(viewInflate, -1, -2);
        this.m_fLayoutSelectPopupOperate = (FrameLayout) viewInflate.findViewById(R.id.first_flayout_popupoperate);
        this.m_btnSelectPopupOperate = (Button) viewInflate.findViewById(R.id.first_btn_popupoperate);
        this.m_fLayoutSelectPopupOperate.setVisibility(0);
        this.m_btnSelectPopupOperate.setText(getResources().getString(R.string.selectall));
        this.m_btnSelectPopupOperate.setOnClickListener(new PopUpOperateSelectOnClickListener());
        TextView textView = (TextView) viewInflate.findViewById(R.id.number_textv_popupoperate);
        this.m_textVNumberPopupOperate = textView;
        textView.setText(ContentTree.ROOT_ID);
        Button button = (Button) viewInflate.findViewById(R.id.second_btn_popupoperate);
        this.m_btnSwitchPopupOperate = button;
        button.setVisibility(0);
        this.m_btnSwitchPopupOperate.setText(getResources().getString(R.string.alluser));
        this.m_btnSwitchPopupOperate.setOnClickListener(new PopUpOperateSwitchOnClickListener());
        this.m_hScrollView = (HorizontalScrollView) viewInflate.findViewById(R.id.hscrollview_popup_operate);
    }

    private class PopUpOperateSelectOnClickListener implements View.OnClickListener {
        private PopUpOperateSelectOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Button button = (Button) view;
            if (button.getText().toString().equals(ChoiceUserActivity.this.getResources().getString(R.string.selectall))) {
                button.setText(ChoiceUserActivity.this.getResources().getString(R.string.selectcancel));
                for (int i = 0; i < ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.size(); i++) {
                    ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i).isChoiced = true;
                    if (!Global.g_sendUserList.contains(ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i).strMacAddr)) {
                        Global.g_sendUserList.add(ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i).strMacAddr);
                    }
                }
            } else {
                button.setText(ChoiceUserActivity.this.getResources().getString(R.string.selectall));
                for (int i2 = 0; i2 < ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.size(); i2++) {
                    ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i2).isChoiced = false;
                    Global.g_sendUserList.clear();
                }
            }
            ChoiceUserActivity.this.m_textVNumberPopupOperate.setText(String.valueOf(Global.g_sendUserList.size()));
            ChoiceUserActivity.this.m_choiceUserAdpter.notifyDataSetChanged();
        }
    }

    private class PopUpOperateSwitchOnClickListener implements View.OnClickListener {
        private PopUpOperateSwitchOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Button button = (Button) view;
            if (button.getText().toString().equals(ChoiceUserActivity.this.getResources().getString(R.string.alluser))) {
                button.setText(ChoiceUserActivity.this.getResources().getString(R.string.specifyuser));
                ChoiceUserActivity.this.m_btnSelectPopupOperate.setEnabled(false);
            } else {
                button.setText(ChoiceUserActivity.this.getResources().getString(R.string.alluser));
                ChoiceUserActivity.this.m_btnSelectPopupOperate.setEnabled(true);
            }
            ChoiceUserActivity.this.m_listVUser.setEnabled(ChoiceUserActivity.this.m_btnSelectPopupOperate.isEnabled());
            for (int i = 0; i < ChoiceUserActivity.this.m_listVUser.getChildCount(); i++) {
                ((CheckBox) ChoiceUserActivity.this.m_listVUser.getChildAt(i).findViewById(R.id.checkb_choiceuseritem)).setEnabled(ChoiceUserActivity.this.m_btnSelectPopupOperate.isEnabled());
            }
        }
    }

    private String uri2FilePath(Uri uri) {
        try {
            Cursor cursorManagedQuery = managedQuery(uri, new String[]{"_data"}, null, null, null);
            int columnIndexOrThrow = cursorManagedQuery.getColumnIndexOrThrow("_data");
            cursorManagedQuery.moveToFirst();
            String string = cursorManagedQuery.getString(columnIndexOrThrow);
            cursorManagedQuery.close();
            return string;
        } catch (Exception unused) {
            return null;
        }
    }

    private String getFileNameByUri(Uri uri) {
        String string = null;
        try {
            if (uri.getScheme().toString().compareTo("content") == 0) {
                string = uri2FilePath(uri);
            } else if (uri.getScheme().compareTo("file") == 0) {
                string = uri.getPath().toString();
            }
        } catch (Exception unused) {
        }
        return string;
    }

    private void initData() {
        try {
            if (this.m_ipmsgApp.ipmsgService != null) {
                this.m_ipmsgApp.ipmsgService.ehList.add(this);
                ChoiceUserAdpter choiceUserAdpter = new ChoiceUserAdpter(this, R.layout.choiceuseritem, this.m_ipmsgApp.ipmsgService.userList);
                this.m_choiceUserAdpter = choiceUserAdpter;
                this.m_listVUser.setAdapter((ListAdapter) choiceUserAdpter);
                this.m_vecWaitSendfiles = new Vector<>();
                this.m_arrayLHosts = new ArrayList<>();
                for (int i = 0; i < this.m_ipmsgApp.ipmsgService.userList.size(); i++) {
                    this.m_ipmsgApp.ipmsgService.userList.get(i).isChoiced = false;
                }
                Global.g_sendUserList.clear();
                if (this.m_strActivity != null && this.m_strActivity.equals("MyShareActivity") && getIntent().getExtras().getString("strMacList") != null) {
                    for (int i2 = 0; i2 < this.m_ipmsgApp.ipmsgService.userList.size(); i2++) {
                        if (getIntent().getExtras().getString("strMacList").contains(this.m_ipmsgApp.ipmsgService.userList.get(i2).strMacAddr)) {
                            this.m_ipmsgApp.ipmsgService.userList.get(i2).isChoiced = true;
                            if (!Global.g_sendUserList.contains(this.m_ipmsgApp.ipmsgService.userList.get(i2).strMacAddr)) {
                                Global.g_sendUserList.add(this.m_ipmsgApp.ipmsgService.userList.get(i2).strMacAddr);
                            }
                        }
                    }
                }
                if (this.m_textVNumberPopupOperate != null) {
                    this.m_textVNumberPopupOperate.setText(String.valueOf(Global.g_sendUserList.size()));
                }
                this.m_choiceUserAdpter.notifyDataSetChanged();
                getSendFiles();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void getSendFiles() {
        if (this.m_strActivity.equals("ImagePreviewActivity") || this.m_strActivity.equals("TxtBrowserActivity")) {
            this.m_vecWaitSendfiles.add(this.m_strFilePath);
            return;
        }
        if (this.m_strActivity.equals("PlayMediaActivity")) {
            this.m_vecWaitSendfiles.add(this.m_strFilePath);
            return;
        }
        int i = 0;
        if (this.m_strActivity.equals("MyShareActivity")) {
            this.m_btnSend.setText("瀹屾垚");
            while (i < this.m_ipmsgApp.waitSendfiles.size()) {
                this.m_vecWaitSendfiles.add(this.m_ipmsgApp.waitSendfiles.get(i));
                i++;
            }
            return;
        }
        if (this.m_strActivity.equals("NewDiscussActivity")) {
            this.m_btnSend.setText(getResources().getString(R.string.confirm));
        } else if (this.m_strFilePath == null) {
            while (i < Global.g_filePath.size()) {
                this.m_vecWaitSendfiles.add(Global.g_filePath.get(i));
                i++;
            }
        }
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.btn_back_choiceuser);
        this.m_btnBack = button;
        button.setOnClickListener(new BtnBackOnClickListener());
        Button button2 = (Button) findViewById(R.id.btn_sendfile_choiceuser);
        this.m_btnSend = button2;
        button2.setOnClickListener(new BtnSendOnClickListener());
        ListView listView = (ListView) findViewById(R.id.listv_choiceuser);
        this.m_listVUser = listView;
        listView.setOnItemClickListener(new ListVUserItemClickListener());
        this.m_listVUser.getViewTreeObserver().addOnPreDrawListener(new ViewTreeOnPreDrawListener());
    }

    private class ViewTreeOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private ViewTreeOnPreDrawListener() {
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            try {
                if (ChoiceUserActivity.this.m_strActivity == null || !ChoiceUserActivity.this.m_strActivity.equals("MyShareActivity")) {
                    return true;
                }
                ChoiceUserActivity.this.m_popupWindowOperate.showAtLocation(ChoiceUserActivity.this.findViewById(R.id.llayout_choiceuser), 80, 0, 0);
                ChoiceUserActivity.this.m_hScrollView.scrollTo(ChoiceUserActivity.this.m_hScrollView.getRight(), 0);
                ChoiceUserActivity.this.m_hScrollView.smoothScrollTo(0, ChoiceUserActivity.this.m_hScrollView.getHeight());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
    }

    private class ListVUserItemClickListener implements AdapterView.OnItemClickListener {
        private ListVUserItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkb_choiceuseritem);
            HostInformation hostInformation = ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i);
            if (hostInformation.isChoiced) {
                checkBox.setChecked(false);
                hostInformation.isChoiced = false;
                if (Global.g_sendUserList.contains(hostInformation.strMacAddr)) {
                    Global.g_sendUserList.remove(hostInformation.strMacAddr);
                }
            } else {
                checkBox.setChecked(true);
                hostInformation.isChoiced = true;
                if (!Global.g_sendUserList.contains(hostInformation.strMacAddr)) {
                    Global.g_sendUserList.add(hostInformation.strMacAddr);
                }
            }
            if (ChoiceUserActivity.this.m_textVNumberPopupOperate != null) {
                ChoiceUserActivity.this.m_textVNumberPopupOperate.setText(String.valueOf(Global.g_sendUserList.size()));
            }
            ChoiceUserActivity.this.m_choiceUserAdpter.notifyDataSetChanged();
        }
    }

    private class BtnBackOnClickListener implements View.OnClickListener {
        private BtnBackOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ChoiceUserActivity.this.onBackPressed();
        }
    }

    private class BtnSendOnClickListener implements View.OnClickListener {
        private BtnSendOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ChoiceUserActivity.this.m_vecWaitSendfiles.size() != 0 || !ChoiceUserActivity.this.m_bIsShare) {
                ChoiceUserActivity.this.getReceiveHosts();
                String string = ChoiceUserActivity.this.getResources().getString(R.string.alluser);
                if (!ChoiceUserActivity.this.m_arrayLHosts.isEmpty() || (ChoiceUserActivity.this.m_btnSwitchPopupOperate != null && (ChoiceUserActivity.this.m_btnSwitchPopupOperate == null || !ChoiceUserActivity.this.m_btnSwitchPopupOperate.getText().toString().equals(string)))) {
                    if (ChoiceUserActivity.this.m_strActivity == null || !ChoiceUserActivity.this.m_strActivity.equals("MyShareActivity")) {
                        if (ChoiceUserActivity.this.m_strActivity == null || !ChoiceUserActivity.this.m_strActivity.equals("NewDiscussActivity")) {
                            if (ChoiceUserActivity.this.m_arrayLHosts.size() > 0) {
                                if ((Global.g_whatFolder != Category.image && Global.g_whatFolder != Category.audio && Global.g_whatFolder != Category.video && Global.g_whatFolder != Category.document && Global.g_whatFolder != Category.apk) || 1 != Global.g_nBrowseMethod) {
                                    ChoiceUserActivity.this.sendFiles();
                                } else {
                                    int i = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
                                    if (i == 1) {
                                        ChoiceUserActivity.this.sendFiles(FileListView.m_vecLimitedApk);
                                    } else if (i == 2) {
                                        ChoiceUserActivity.this.sendFiles(FileListView.m_vecLimitedAudio);
                                    } else if (i == 3) {
                                        ChoiceUserActivity.this.sendFiles(FileListView.m_vecLimitedDocument);
                                    } else if (i == 4) {
                                        ChoiceUserActivity.this.sendFiles(FileListView.m_vecLimitedImage);
                                    } else if (i == 5) {
                                        ChoiceUserActivity.this.sendFiles(FileListView.m_vecLimitedVideo);
                                    }
                                }
                                ChoiceUserActivity.this.afterSend();
                                return;
                            }
                            Toast.makeText(ChoiceUserActivity.this, R.string.notifychoiceuser, 1).show();
                            return;
                        }
                        ChoiceUserActivity.this.m_arrayLHosts.clear();
                        for (int i2 = 0; i2 < ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.size(); i2++) {
                            ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i2).isChoiced = false;
                        }
                        ChoiceUserActivity.this.setResult(-1, new Intent());
                        ChoiceUserActivity.this.finish();
                        return;
                    }
                    String str = "";
                    if (ChoiceUserActivity.this.m_btnSwitchPopupOperate.getText().toString().equals(string)) {
                        for (int i3 = 0; i3 < ChoiceUserActivity.this.m_arrayLHosts.size(); i3++) {
                            str = str + ((HostInformation) ChoiceUserActivity.this.m_arrayLHosts.get(i3)).strMacAddr;
                            if (i3 < ChoiceUserActivity.this.m_arrayLHosts.size() - 1) {
                                str = str + ",";
                            }
                        }
                    }
                    ChoiceUserActivity.this.m_arrayLHosts.clear();
                    for (int i4 = 0; i4 < ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.size(); i4++) {
                        ChoiceUserActivity.this.m_ipmsgApp.ipmsgService.userList.get(i4).isChoiced = false;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("strMacs", str);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    ChoiceUserActivity.this.setResult(-1, intent);
                    ChoiceUserActivity.this.finish();
                    return;
                }
                Toast.makeText(ChoiceUserActivity.this, "璇峰厛閫夋嫨鐢ㄦ埛", 1).show();
                return;
            }
            Toast.makeText(ChoiceUserActivity.this, "无法获取要发送的文件路径，发送失败", 1).show();
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.ChoiceUserActivity$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Category;

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
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void afterSend() {
        Global.g_sendUserList.clear();
        this.m_arrayLHosts.clear();
        for (int i = 0; i < this.m_ipmsgApp.ipmsgService.userList.size(); i++) {
            this.m_ipmsgApp.ipmsgService.userList.get(i).isChoiced = false;
        }
        this.m_choiceUserAdpter.notifyDataSetChanged();
        if (this.m_bIsShare) {
            Toast.makeText(this, "文件已发送", 1).show();
            return;
        }
        Global.g_filePath.clear();
        for (int i2 = 0; i2 < IpmsgActivity.s_fileListView.getFileAdapter().getFileList().size(); i2++) {
            IpmsgActivity.s_fileListView.getFileAdapter().getFileList().get(i2).setCheck(false);
        }
        IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
        IpmsgActivity.m_popupWindowSend.dismiss();
        onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFiles() {
        for (int i = 0; i < this.m_arrayLHosts.size(); i++) {
            this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, this.m_vecWaitSendfiles, this.m_arrayLHosts.get(i), this.m_ipmsgApp.ipmsgService, null, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFiles(Vector<String> vector) {
        for (int i = 0; i < this.m_arrayLHosts.size(); i++) {
            this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, this.m_vecWaitSendfiles, this.m_arrayLHosts.get(i), this.m_ipmsgApp.ipmsgService, vector, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getReceiveHosts() {
        for (int i = 0; i < Global.g_sendUserList.size(); i++) {
            for (int i2 = 0; i2 < this.m_ipmsgApp.ipmsgService.userList.size(); i2++) {
                if (this.m_ipmsgApp.ipmsgService.userList.get(i2).strMacAddr.equals(Global.g_sendUserList.get(i))) {
                    this.m_arrayLHosts.add(this.m_ipmsgApp.ipmsgService.userList.get(i2));
                }
            }
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.m_strActivity != null && this.m_strActivity.equals("MyShareActivity")) {
            this.m_ipmsgApp.waitSendfiles.clear();
            Bundle bundle = new Bundle();
            bundle.putString("strMacs", null);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(-1, intent);
            finish();
            return;
        }
        if (backDispose()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean backDispose() {
        if (!this.m_bIsShare || Global.g_bWorking || this.m_ipmsgApp.ipmsgService.fileMsgs.size() <= 0) {
            return false;
        }
        Iterator<Map.Entry<String, ArrayList<MsgRecord>>> it = this.m_ipmsgApp.ipmsgService.fileMsgs.entrySet().iterator();
        boolean z = false;
        while (it.hasNext()) {
            ArrayList<MsgRecord> value = it.next().getValue();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).getFileId() != -1) {
                    this.m_exitDialog.show();
                    z = true;
                }
            }
        }
        return z;
    }

    public void onBack() {
        super.onBackPressed();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        PopupWindow popupWindow = this.m_popupWindowOperate;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.m_popupWindowOperate.dismiss();
        }
        if (this.m_ipmsgApp.ipmsgService != null && this.m_ipmsgApp.ipmsgService.ehList.contains(this)) {
            this.m_ipmsgApp.ipmsgService.ehList.remove(this);
        }
        if (this.m_bIsShare && !Global.g_bWorking && this.m_ipmsgApp.ipmsgService != null) {
            this.m_ipmsgApp.ipmsgService.stopSelf();
            this.m_ipmsgApp.ipmsgService = null;
        }
        super.onDestroy();
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
        this.m_choiceUserAdpter.notifyDataSetChanged();
    }
}

