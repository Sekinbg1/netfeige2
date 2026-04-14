package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.netfeige.common.DBHelper;
import com.netfeige.common.HostInformation;
import com.netfeige.common.LanSharedItem;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SharePassword;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.LanShareFileAdapter;
import com.netfeige.display.ui.LanShareListView;
import com.netfeige.display.ui.dialog.CheckLanSharePwdDialog;
import com.netfeige.display.ui.dialog.IsDownloadDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;
import com.netfeige.service.IpmsgService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/* JADX INFO: loaded from: classes.dex */
public class LanShareActivity extends Activity implements IpmsgService.LanShareEventHandler {
    private Button m_btnBack;
    private Button m_btnCancelPopupOperate;
    private Button m_btnDownloadPopupOperate;
    private FrameLayout m_fLayoutDownloadPopupOperate;
    private HorizontalScrollView m_hScrollView;
    private LanShareFileAdapter m_lanShareFileAdapter;
    private LanShareListView m_lanShareListView;
    private LinearLayout m_linearLPopupHome;
    private IpmsgApplication m_myApp;
    private int m_nPopX;
    private int m_nPopY;
    private PopupWindow m_popupWindow;
    private PopupWindow m_popupWindowOperate;
    private String m_strDestMac;
    private TextView m_textVNumberPopupOperate;
    private Button m_btnPopup = null;
    private LayoutInflater m_inflater = null;
    private ArrayList<LanSharedItem> m_lanShareFilesList = new ArrayList<>();
    private ArrayList<LanSharedItem> m_selectedFilesList = new ArrayList<>();
    private HashMap<String, ArrayList<LanSharedItem>> m_selectedFilesMap = new HashMap<>();
    private HashMap<String, ArrayList<LanSharedItem>> m_waitDLFilesMap = new HashMap<>();
    private final int m_nPopUpX = 5;
    private final int m_nPopUpY = 75;
    private long m_uQueryId = 0;
    private Stack<String> m_stackSubDir = new Stack<>();

    public Stack<String> getM_stackSubDir() {
        return this.m_stackSubDir;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.lanshare);
        this.m_myApp.ipmsgService.lanShareEHList.add(this);
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        Button button = (Button) findViewById(R.id.back_btn_lanshare);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.LanShareActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LanShareActivity.this.onBackPressed();
            }
        });
        this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
        this.m_nPopY = Public_Tools.dip2px(this, 75.0f);
        this.m_lanShareListView = (LanShareListView) findViewById(R.id.list_lanshare);
        LanShareFileAdapter lanShareFileAdapter = new LanShareFileAdapter(this, this.m_lanShareFilesList);
        this.m_lanShareFileAdapter = lanShareFileAdapter;
        this.m_lanShareListView.setAdapter((ListAdapter) lanShareFileAdapter);
        this.m_lanShareListView.setonRefreshListener(new LanShareListView.OnRefreshListener() { // from class: com.netfeige.display.ui.LanShareActivity.2
            /* JADX WARN: Type inference failed for: r0v4, types: [com.netfeige.display.ui.LanShareActivity$2$1] */
            @Override // com.netfeige.display.ui.LanShareListView.OnRefreshListener
            public void onRefresh() {
                LanShareActivity.this.m_stackSubDir.clear();
                LanShareActivity.this.cancelSelected();
                LanShareActivity.this.initData();
                new AsyncTask<Void, Void, Void>() { // from class: com.netfeige.display.ui.LanShareActivity.2.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... voidArr) {
                        try {
                            Thread.sleep(2000L);
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Void r1) {
                        LanShareActivity.this.m_lanShareFileAdapter.notifyDataSetChanged();
                        LanShareActivity.this.m_lanShareListView.onRefreshComplete();
                    }
                }.execute(null, null, null);
            }
        });
        this.m_lanShareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.LanShareActivity.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                String str;
                LanShareListView lanShareListView = (LanShareListView) adapterView;
                if (i - lanShareListView.getHeaderViewsCount() == 0) {
                    if (LanShareActivity.this.m_stackSubDir.isEmpty()) {
                        if (LanShareActivity.this.m_strDestMac != null) {
                            LanShareActivity.this.m_strDestMac = null;
                            LanShareActivity.this.cancelSelected();
                            LanShareActivity.this.initData();
                            return;
                        }
                        LanShareActivity.this.onBackPressed();
                        return;
                    }
                    if (((String) LanShareActivity.this.m_stackSubDir.peek()).equals(File.separator)) {
                        LanShareActivity.this.m_stackSubDir.pop();
                        LanShareActivity.this.cancelSelected();
                        LanShareActivity.this.initData();
                        return;
                    }
                    LanShareActivity.this.m_stackSubDir.pop();
                    LanSharedItem lanSharedItem = (LanSharedItem) lanShareListView.getAdapter().getItem(i);
                    HashMap map = new HashMap();
                    map.put("strMac", lanSharedItem.getM_strFromMac());
                    map.put("lanSharedItem", lanSharedItem);
                    map.put("strDirPath", LanShareActivity.this.m_stackSubDir.peek());
                    map.put("flag", Public_Def.ShareCheckFlag.SHARE_QUERY);
                    LanShareActivity.this.preSendSubFileListQuery(map);
                    return;
                }
                if (i - lanShareListView.getHeaderViewsCount() > 0) {
                    if (LanShareActivity.this.m_selectedFilesList.size() > 0) {
                        View viewFindViewById = view.findViewById(R.id.file_select_box_fileitem);
                        LanShareActivity.this.m_lanShareFileAdapter.checkFileOperate(viewFindViewById, Integer.parseInt(viewFindViewById.getTag().toString()));
                        LanShareActivity.this.showPopupOperate();
                        return;
                    }
                    LanSharedItem lanSharedItem2 = (LanSharedItem) lanShareListView.getAdapter().getItem(i);
                    if (lanSharedItem2.getM_iType() == 1) {
                        if (LanShareActivity.this.m_stackSubDir.isEmpty()) {
                            str = File.separator;
                        } else {
                            str = ((String) LanShareActivity.this.m_stackSubDir.peek()) + lanSharedItem2.getM_strName() + File.separator;
                        }
                        LanShareActivity.this.m_stackSubDir.push(str);
                        HashMap map2 = new HashMap();
                        map2.put("strMac", lanSharedItem2.getM_strFromMac());
                        map2.put("lanSharedItem", lanSharedItem2);
                        map2.put("strDirPath", str);
                        map2.put("flag", Public_Def.ShareCheckFlag.SHARE_QUERY);
                        LanShareActivity.this.preSendSubFileListQuery(map2);
                        return;
                    }
                    if (IsDownloadDialog.m_bIsCheck) {
                        LanShareActivity.this.preClickDLDispose(lanSharedItem2);
                        return;
                    }
                    HashMap map3 = new HashMap();
                    map3.put("lanSharedItem", lanSharedItem2);
                    new IsDownloadDialog(LanShareActivity.this, (HashMap<String, Object>) map3).show();
                }
            }
        });
        initData();
        initPopupWindowOperate();
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_myApp.currentActivity = this;
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
        cancelSelected();
        this.m_myApp.ipmsgService.lanShareEHList.remove(this);
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initData() {
        this.m_lanShareFilesList.clear();
        LanSharedItem lanSharedItem = new LanSharedItem();
        lanSharedItem.setM_strName("....0");
        this.m_lanShareFilesList.add(lanSharedItem);
        this.m_lanShareFileAdapter.notifyDataSetChanged();
        this.m_uQueryId = Public_Tools.getCurrentTimeMillis();
        if (this.m_strDestMac == null) {
            this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendFileListQuery(this.m_uQueryId);
            return;
        }
        HostInformation hostInfo = this.m_myApp.ipmsgService.getHostInfo(this.m_strDestMac);
        if (hostInfo == null) {
            Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
        } else {
            this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendFileListQuery(hostInfo, this.m_uQueryId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelSelected() {
        for (int i = 0; i < this.m_selectedFilesList.size(); i++) {
            this.m_selectedFilesList.get(i).setM_bIsCheck(false);
        }
        this.m_selectedFilesList.clear();
        if (this.m_popupWindowOperate.isShowing()) {
            this.m_popupWindowOperate.dismiss();
        }
    }

    private void initPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popup_lanshare, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup2));
        this.m_popupWindow.setFocusable(true);
        this.m_popupWindow.setOutsideTouchable(true);
        this.m_popupWindow.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.home_linearl_popup_lanshare);
        this.m_linearLPopupHome = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowHomeOnClickListener());
    }

    private class PopUpWindowHomeOnClickListener implements View.OnClickListener {
        private PopUpWindowHomeOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            LanShareActivity.this.m_stackSubDir.clear();
            LanShareActivity.this.initData();
            LanShareActivity.this.m_popupWindow.dismiss();
        }
    }

    private void initPopupWindowOperate() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupoperate, (ViewGroup) null);
        this.m_popupWindowOperate = new PopupWindow(viewInflate, -1, -2);
        this.m_fLayoutDownloadPopupOperate = (FrameLayout) viewInflate.findViewById(R.id.first_flayout_popupoperate);
        this.m_btnDownloadPopupOperate = (Button) viewInflate.findViewById(R.id.first_btn_popupoperate);
        this.m_fLayoutDownloadPopupOperate.setVisibility(0);
        this.m_btnDownloadPopupOperate.setText(getResources().getString(R.string.download));
        this.m_btnDownloadPopupOperate.setOnClickListener(new PopUpOperateDownloadOnClickListener());
        this.m_textVNumberPopupOperate = (TextView) viewInflate.findViewById(R.id.number_textv_popupoperate);
        Button button = (Button) viewInflate.findViewById(R.id.second_btn_popupoperate);
        this.m_btnCancelPopupOperate = button;
        button.setVisibility(0);
        this.m_btnCancelPopupOperate.setText(getResources().getString(R.string.cancel));
        this.m_btnCancelPopupOperate.setOnClickListener(new PopUpOperateCancelOnClickListener());
        this.m_hScrollView = (HorizontalScrollView) viewInflate.findViewById(R.id.hscrollview_popup_operate);
    }

    private class PopUpOperateDownloadOnClickListener implements View.OnClickListener {
        private PopUpOperateDownloadOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!LanShareActivity.this.m_stackSubDir.isEmpty()) {
                LanShareActivity.this.sendSubFileDLQuery(Public_Def.DownloadCmd.DOWNLOAD_ONLY, LanShareActivity.this.m_selectedFilesList);
            } else {
                LanShareActivity lanShareActivity = LanShareActivity.this;
                lanShareActivity.preDLRootFiles(lanShareActivity.m_selectedFilesList, Public_Def.DownloadCmd.DOWNLOAD_ONLY);
            }
        }
    }

    private class PopUpOperateCancelOnClickListener implements View.OnClickListener {
        private PopUpOperateCancelOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            LanShareActivity.this.cancelSelected();
            LanShareActivity.this.m_lanShareFileAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<LanSharedItem> getM_selectedFilesList() {
        return this.m_selectedFilesList;
    }

    public void showPopupOperate() {
        if (this.m_selectedFilesList.size() > 0) {
            if (!this.m_popupWindowOperate.isShowing()) {
                this.m_popupWindowOperate.showAtLocation(findViewById(R.id.llayout_lanshare), 80, 0, 0);
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

    public void locationDestHostSubQuery(String str) {
        this.m_strDestMac = str;
        this.m_stackSubDir.clear();
        cancelSelected();
        initData();
    }

    @Override // com.netfeige.service.IpmsgService.LanShareEventHandler
    public void onFileShareListAns(HashMap<String, Object> map) {
        if (map.containsKey("queryId") && Long.parseLong((String) map.get("queryId")) == this.m_uQueryId) {
            this.m_lanShareFilesList.addAll((ArrayList) map.get("lanShareList"));
            this.m_lanShareFileAdapter.notifyDataSetChanged();
        }
    }

    @Override // com.netfeige.service.IpmsgService.LanShareEventHandler
    public void onSubFileShareListAns(HashMap<String, Object> map) {
        if (map.containsKey("queryId") && Long.parseLong((String) map.get("queryId")) == this.m_uQueryId) {
            this.m_lanShareFilesList.addAll((ArrayList) map.get("lanShareList"));
            this.m_lanShareFileAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean preSendSubFileListQuery(HashMap<String, Object> map) {
        if (map.get("strMac") == null || map.get("lanSharedItem") == null || map.get("strDirPath") == null) {
            return false;
        }
        if (this.m_myApp.ipmsgService.getHostInfo((String) map.get("strMac")) == null) {
            Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
            return false;
        }
        if (checkPwd(map)) {
            sendSubFileListQuery((String) map.get("strMac"), ((LanSharedItem) map.get("lanSharedItem")).getM_iID(), (String) map.get("strDirPath"));
        }
        return true;
    }

    private boolean checkPwd(HashMap<String, Object> map) {
        SharePassword sharePassword;
        String str = (String) map.get("strMac");
        if (IpmsgApplication.g_mapUserSharePwd.containsKey(str) && IpmsgApplication.g_mapUserSharePwd.get(str).length() > 0) {
            new ArrayList();
            ArrayList<SharePassword> sharePasswordRecord = DBHelper.getInstance(this).getSharePasswordRecord("sMac = '" + str + "'");
            if (sharePasswordRecord.isEmpty()) {
                sharePassword = new SharePassword(-1, str, null);
            } else {
                sharePassword = sharePasswordRecord.get(0);
            }
            if (sharePassword.getM_iID() == -1 || !sharePassword.getM_strPassword().equals(IpmsgApplication.g_mapUserSharePwd.get(str))) {
                map.put("sharePwd", sharePassword);
                new CheckLanSharePwdDialog(this, map).show();
                return false;
            }
        }
        return true;
    }

    public boolean sendSubFileListQuery(String str, int i, String str2) {
        HostInformation hostInfo = this.m_myApp.ipmsgService.getHostInfo(str);
        if (hostInfo == null) {
            Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
            return false;
        }
        this.m_uQueryId = Public_Tools.getCurrentTimeMillis();
        cancelSelected();
        this.m_lanShareFilesList.clear();
        LanSharedItem lanSharedItem = new LanSharedItem();
        lanSharedItem.setM_strName("....0");
        lanSharedItem.setM_iID(i);
        lanSharedItem.setM_strFromMac(str);
        this.m_lanShareFilesList.add(lanSharedItem);
        this.m_lanShareFileAdapter.notifyDataSetChanged();
        return this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendSubFileListQuery(hostInfo, this.m_uQueryId, i, str2);
    }

    public void preClickDLDispose(LanSharedItem lanSharedItem) {
        ArrayList<LanSharedItem> arrayList = new ArrayList<>();
        arrayList.add(lanSharedItem);
        if (this.m_stackSubDir.isEmpty()) {
            HostInformation hostInfo = this.m_myApp.ipmsgService.getHostInfo(lanSharedItem.getM_strFromMac());
            if (hostInfo == null) {
                Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
                return;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("strMac", lanSharedItem.getM_strFromMac());
            map.put("flag", Public_Def.ShareCheckFlag.SHARE_DOWNLOAD_SINGLE);
            map.put("dlcmd", Public_Def.DownloadCmd.DOWNLOAD_ONLY);
            map.put("lanSharedItem", lanSharedItem);
            if (checkPwd(map)) {
                this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendRootFileDLQuery(hostInfo, Public_Def.DownloadCmd.DOWNLOAD_ONLY, arrayList);
                return;
            }
            return;
        }
        sendSubFileDLQuery(Public_Def.DownloadCmd.DOWNLOAD_ONLY, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preDLRootFiles(ArrayList<LanSharedItem> arrayList, Public_Def.DownloadCmd downloadCmd) {
        this.m_selectedFilesMap.clear();
        this.m_waitDLFilesMap.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if (this.m_selectedFilesMap.containsKey(arrayList.get(i).getM_strFromMac())) {
                this.m_selectedFilesMap.get(arrayList.get(i).getM_strFromMac()).add(arrayList.get(i));
            } else {
                ArrayList<LanSharedItem> arrayList2 = new ArrayList<>();
                arrayList2.add(arrayList.get(i));
                this.m_selectedFilesMap.put(arrayList.get(i).getM_strFromMac(), arrayList2);
            }
        }
        dlRootFiles(downloadCmd, null);
    }

    public void dlRootFiles(Public_Def.DownloadCmd downloadCmd, String str) {
        Iterator<Map.Entry<String, ArrayList<LanSharedItem>>> it = this.m_selectedFilesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<LanSharedItem>> next = it.next();
            if (this.m_myApp.ipmsgService.getHostInfo(next.getKey()) == null || (str != null && next.getKey().equals(str))) {
                it.remove();
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("strMac", next.getKey());
                map.put("flag", Public_Def.ShareCheckFlag.SHARE_DOWNLOAD);
                map.put("dlcmd", downloadCmd);
                map.put("lanSharedItem", next.getValue().get(0));
                if (!checkPwd(map)) {
                    return;
                }
                this.m_waitDLFilesMap.put(next.getKey(), next.getValue());
                it.remove();
            }
        }
        sendRootFileDLQuery(downloadCmd);
    }

    private void sendRootFileDLQuery(Public_Def.DownloadCmd downloadCmd) {
        for (Map.Entry<String, ArrayList<LanSharedItem>> entry : this.m_waitDLFilesMap.entrySet()) {
            HostInformation hostInfo = this.m_myApp.ipmsgService.getHostInfo(entry.getKey());
            if (hostInfo != null) {
                this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendRootFileDLQuery(hostInfo, downloadCmd, entry.getValue());
            }
        }
        cancelSelected();
        this.m_lanShareFileAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSubFileDLQuery(Public_Def.DownloadCmd downloadCmd, ArrayList<LanSharedItem> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        HostInformation hostInfo = this.m_myApp.ipmsgService.getHostInfo(arrayList.get(0).getM_strFromMac());
        if (hostInfo == null) {
            Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
            return;
        }
        this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendSubFileDLQuery(hostInfo, downloadCmd, arrayList, this.m_stackSubDir.peek());
        cancelSelected();
        this.m_lanShareFileAdapter.notifyDataSetChanged();
    }
}

