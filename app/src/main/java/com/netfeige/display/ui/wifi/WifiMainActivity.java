package com.netfeige.display.ui.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.broadcast.WTBroadcast;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.data.WTOperateEnum;
import com.netfeige.display.data.WiFiAdpter;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.service.IpmsgService;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WifiMainActivity extends Activity implements WTBroadcast.EventHandler, IpmsgService.EventHandler {
    private static final int m_nCreateWiFiFail = 2;
    private static final int m_nCreateWiFiSuccess = 0;
    private static final int m_nCreateWiFiTimeOut = 1;
    private static final int m_nSearchWiFiTimeOut = 5;
    private static final int m_nWTConnectResult = 4;
    private static final int m_nWTScanResult = 3;
    private static final String m_strNoLink = "<u>鏃犺繛鎺?/u>";
    private static final String m_strPassword = "Feige6688";
    private LinearLayout m_LinearLDialog;
    private Button m_btnCancelDialog;
    private Button m_btnConfirmDialog;
    private Button m_btnCurrentWiFi;
    private Button m_btnLinkedWiFi;
    private Context m_context;
    private CreateWiFiAPProcess m_createWiFiAPProcess;
    private IWiFiSupervise m_iWiFiSupervise;
    private IpmsgApplication m_ipmsgApp;
    private LinearLayout m_linearLCreatefail;
    private LinearLayout m_linearLCreateinitial;
    private LinearLayout m_linearLCreatesuccess;
    private LinearLayout m_linearLCreating;
    private LinearLayout m_linearLSearchFail;
    private LinearLayout m_linearLSearchSuccess;
    private LinearLayout m_linearLSearching;
    private ListView m_listVWT;
    private SearchWiFiAPProcess m_searchWiFiAPProcess;
    private String m_strSSID;
    private TextView m_textVAPName;
    private TextView m_textVContentDialog;
    private TextView m_textVNumber;
    private WiFiAdpter m_wTAdapter;
    private WifiLinearLayout m_wifiLinearLayout;
    private final int m_nWTConnected = 6;
    private final int m_nUserResult = 7;
    private ArrayList<ScanResult> m_listWifi = new ArrayList<>();
    private String m_strSargetSSID = "";
    private WTOperateEnum wTOperateEnum = WTOperateEnum.NOTHING;
    private String m_strNumber = "鐑偣鍒涘缓鎴愬姛锛佸凡鏈?<font color=\"#270cf7\">";
    private String m_strNumber2 = " </font>人连接";
    private String m_strAPName = "鐑偣鍚嶏細";
    private boolean m_bIsClickSearchBtn = false;
    private String m_stru = "<u>";
    private String m_stritalicu = "</u>";
    public Handler WiFiAPHandler = new Handler() { // from class: com.netfeige.display.ui.wifi.WifiMainActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                WifiMainActivity.this.m_linearLCreating.setVisibility(8);
                WifiMainActivity.this.m_linearLCreatesuccess.setVisibility(0);
                TextView textView = WifiMainActivity.this.m_textVNumber;
                StringBuilder sb = new StringBuilder();
                sb.append(WifiMainActivity.this.m_strNumber);
                sb.append(WifiMainActivity.this.m_ipmsgApp.ipmsgService.userList.size() > 1 ? WifiMainActivity.this.m_ipmsgApp.ipmsgService.userList.size() - 1 : 0);
                sb.append(WifiMainActivity.this.m_strNumber2);
                textView.setText(Html.fromHtml(sb.toString()));
                WifiMainActivity.this.m_textVAPName.setText(WifiMainActivity.this.m_strAPName + WifiMainActivity.this.m_iWiFiSupervise.getApSSID());
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiBackgroundResource(R.drawable.x_wifi);
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiEnabled(true);
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiText(R.string.closewifiap);
                WifiMainActivity.this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createdsuccess);
                WifiMainActivity.this.m_createWiFiAPProcess.stop();
                Global.g_bWiFiAPWorking = true;
                return;
            }
            if (i == 1) {
                WifiMainActivity.this.m_linearLCreating.setVisibility(8);
                WifiMainActivity.this.m_linearLCreatefail.setVisibility(0);
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiBackgroundResource(R.drawable.x_wifi);
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiEnabled(true);
                WifiMainActivity.this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createdfail);
                WifiMainActivity.this.m_createWiFiAPProcess.stop();
                Global.g_bWiFiAPWorking = false;
                return;
            }
            if (i == 2) {
                WifiMainActivity.this.m_linearLCreating.setVisibility(8);
                WifiMainActivity.this.m_linearLCreatefail.setVisibility(0);
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiBackgroundResource(R.drawable.x_wifi);
                WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiEnabled(true);
                WifiMainActivity.this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createdfail);
                WifiMainActivity.this.m_createWiFiAPProcess.stop();
                Global.g_bWiFiAPWorking = false;
                return;
            }
            if (i == 3) {
                if (WifiMainActivity.this.m_bIsClickSearchBtn) {
                    WifiMainActivity.this.showWiFi();
                    return;
                }
                return;
            }
            String str = WifiMainActivity.m_strNoLink;
            if (i == 4) {
                Button button = WifiMainActivity.this.m_btnLinkedWiFi;
                if (WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() != null) {
                    str = WifiMainActivity.this.m_stru + WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() + WifiMainActivity.this.m_stritalicu;
                }
                button.setText(Html.fromHtml(str));
                WifiMainActivity.this.m_wTAdapter.notifyDataSetChanged();
                return;
            }
            if (i != 5) {
                if (i != 7) {
                    return;
                }
                if ((WifiMainActivity.this.m_iWiFiSupervise.getWifiAPState() == 3 || WifiMainActivity.this.m_iWiFiSupervise.getWifiAPState() == 13) && WifiMainActivity.this.m_iWiFiSupervise.getApSSID().startsWith(WifiMainActivity.this.m_ipmsgApp.g_strAPPref)) {
                    TextView textView2 = WifiMainActivity.this.m_textVNumber;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(WifiMainActivity.this.m_strNumber);
                    sb2.append(WifiMainActivity.this.m_ipmsgApp.ipmsgService.userList.size() > 1 ? WifiMainActivity.this.m_ipmsgApp.ipmsgService.userList.size() - 1 : 0);
                    sb2.append(WifiMainActivity.this.m_strNumber2);
                    textView2.setText(Html.fromHtml(sb2.toString()));
                    return;
                }
                return;
            }
            WifiMainActivity.this.m_linearLSearching.setVisibility(8);
            WifiMainActivity.this.m_linearLSearchFail.setVisibility(0);
            Button button2 = WifiMainActivity.this.m_btnCurrentWiFi;
            if (WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() != null) {
                str = WifiMainActivity.this.m_stru + WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() + WifiMainActivity.this.m_stritalicu;
            }
            button2.setText(Html.fromHtml(str));
            WifiMainActivity.this.m_ipmsgApp.setSearchWiFiAPState(WiFiState.searchedfail);
            WifiMainActivity.this.m_wifiLinearLayout.setSearchWiFiText(R.string.searchwifiap);
            WifiMainActivity.this.m_searchWiFiAPProcess.stop();
        }
    };

    public int getWTConnected() {
        return 6;
    }

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
        setContentView(R.layout.wifimain);
        initBasic();
        initControl();
        initData();
    }

    private void initData() {
        String str;
        goneCreateWiFiAP();
        goneSearchWiFiAP();
        if (this.m_ipmsgApp.getSearchWiFiAPState() == WiFiState.searchedsuccess) {
            if (this.m_searchWiFiAPProcess.running) {
                this.m_searchWiFiAPProcess.stop();
            }
            this.m_iWiFiSupervise.searchWifiAp();
            this.m_searchWiFiAPProcess.start();
            this.m_linearLSearchSuccess.setVisibility(0);
            this.m_bIsClickSearchBtn = true;
            return;
        }
        if ((this.m_iWiFiSupervise.getWifiAPState() == 3 || this.m_iWiFiSupervise.getWifiAPState() == 13) && this.m_iWiFiSupervise.getApSSID().startsWith(this.m_ipmsgApp.g_strAPPref)) {
            this.m_linearLCreatesuccess.setVisibility(0);
            TextView textView = this.m_textVNumber;
            StringBuilder sb = new StringBuilder();
            sb.append(this.m_strNumber);
            sb.append(this.m_ipmsgApp.ipmsgService.userList.size() > 1 ? this.m_ipmsgApp.ipmsgService.userList.size() - 1 : 0);
            sb.append(this.m_strNumber2);
            textView.setText(Html.fromHtml(sb.toString()));
            this.m_textVAPName.setText(this.m_strAPName + this.m_iWiFiSupervise.getApSSID());
            this.m_wifiLinearLayout.setCreateWiFiText(R.string.closewifiap);
            this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createdsuccess);
            return;
        }
        this.m_linearLCreateinitial.setVisibility(0);
        this.m_bIsClickSearchBtn = false;
        Button button = this.m_btnLinkedWiFi;
        if (this.m_iWiFiSupervise.getWifiInfo().getSSID() == null) {
            str = m_strNoLink;
        } else {
            str = this.m_stru + this.m_iWiFiSupervise.getWifiInfo().getSSID() + this.m_stritalicu;
        }
        button.setText(Html.fromHtml(str));
    }

    private void initBasic() {
        try {
            this.m_context = this;
            this.m_ipmsgApp = (IpmsgApplication) getApplication();
            this.m_iWiFiSupervise = WiFiSupervise.getInstance(this.m_context);
            this.m_strSSID = this.m_ipmsgApp.g_strAPPref + Public_Tools.getLocalHostName();
            this.m_createWiFiAPProcess = new CreateWiFiAPProcess();
            this.m_searchWiFiAPProcess = new SearchWiFiAPProcess();
            WTBroadcast.ehList.add(this);
            this.m_ipmsgApp.ipmsgService.ehList.add(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void initControl() {
        WifiLinearLayout wifiLinearLayout = (WifiLinearLayout) findViewById(R.id.linearl_wifi);
        this.m_wifiLinearLayout = wifiLinearLayout;
        wifiLinearLayout.getCreateWiFi().setOnClickListener(new BtnCreateWiFiOnClickListener());
        this.m_wifiLinearLayout.getSearchWiFi().setOnClickListener(new BtnSearchWiFiOnClickListener());
        this.m_wifiLinearLayout.getBtnBack().setOnClickListener(new BtnBackOnClickListener());
        Button button = (Button) findViewById(R.id.btn_linkedwifi);
        this.m_btnLinkedWiFi = button;
        button.setOnClickListener(new BtnCurrentWiFiOnClickListener());
        this.m_linearLCreating = (LinearLayout) findViewById(R.id.linearl_creating);
        this.m_linearLCreatefail = (LinearLayout) findViewById(R.id.linearl_createfail);
        this.m_linearLCreatesuccess = (LinearLayout) findViewById(R.id.linearl_createsuccess);
        this.m_linearLSearching = (LinearLayout) findViewById(R.id.linearl_searchwifiap);
        this.m_linearLSearchFail = (LinearLayout) findViewById(R.id.linearl_searchfail);
        this.m_linearLSearchSuccess = (LinearLayout) findViewById(R.id.linearl_showwifiap);
        this.m_listVWT = (ListView) findViewById(R.id.wt_list_wt_main);
        WiFiAdpter wiFiAdpter = new WiFiAdpter(this, R.layout.wtitem, this.m_listWifi);
        this.m_wTAdapter = wiFiAdpter;
        this.m_listVWT.setAdapter((ListAdapter) wiFiAdpter);
        this.m_listVWT.setOnItemClickListener(new WIFIListViewItemClickListener());
        this.m_LinearLDialog = (LinearLayout) findViewById(R.id.dialog_layout_wt_main1);
        this.m_textVContentDialog = (TextView) findViewById(R.id.content_text_wtdialog);
        Button button2 = (Button) findViewById(R.id.confirm_btn_wtdialog);
        this.m_btnConfirmDialog = button2;
        button2.setOnClickListener(new BtnConfirmDialogOnClickListener());
        Button button3 = (Button) findViewById(R.id.cancel_btn_wtdialog);
        this.m_btnCancelDialog = button3;
        button3.setOnClickListener(new BtnCancelDialogOnClickListener());
        this.m_textVNumber = (TextView) findViewById(R.id.textv_number);
        this.m_textVAPName = (TextView) findViewById(R.id.textv_apname);
        Button button4 = (Button) findViewById(R.id.btn_currentwifi);
        this.m_btnCurrentWiFi = button4;
        button4.setOnClickListener(new BtnCurrentWiFiOnClickListener());
        this.m_linearLCreateinitial = (LinearLayout) findViewById(R.id.linearl_createinitial);
    }

    private class BtnCurrentWiFiOnClickListener implements View.OnClickListener {
        private BtnCurrentWiFiOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WifiMainActivity.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
        }
    }

    private class BtnCancelDialogOnClickListener implements View.OnClickListener {
        private BtnCancelDialogOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WifiMainActivity.this.m_LinearLDialog.setVisibility(8);
        }
    }

    private class BtnConfirmDialogOnClickListener implements View.OnClickListener {
        private BtnConfirmDialogOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WifiMainActivity.this.m_LinearLDialog.setVisibility(8);
            int i = AnonymousClass2.$SwitchMap$com$netfeige$display$data$WTOperateEnum[WifiMainActivity.this.wTOperateEnum.ordinal()];
            if (i == 1) {
                WifiMainActivity.this.createWiFiAPBasic();
                return;
            }
            if (i != 2) {
                if (i != 3) {
                    return;
                }
                WifiMainActivity.this.m_iWiFiSupervise.closeWifiAp();
                Global.g_bWiFiAPWorking = false;
                WifiMainActivity.this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createinitial);
                return;
            }
            if (WifiMainActivity.this.m_createWiFiAPProcess.running) {
                WifiMainActivity.this.m_createWiFiAPProcess.stop();
            }
            WifiMainActivity.this.m_iWiFiSupervise.closeWifiAp();
            Global.g_bWiFiAPWorking = false;
            WifiMainActivity.this.m_bIsClickSearchBtn = true;
            WifiMainActivity.this.m_iWiFiSupervise.openWiFi();
            WifiMainActivity.this.m_searchWiFiAPProcess.start();
            WifiMainActivity.this.goneCreateWiFiAP();
            WifiMainActivity.this.m_linearLSearching.setVisibility(0);
            WifiMainActivity.this.m_ipmsgApp.setSearchWiFiAPState(WiFiState.searching);
            WifiMainActivity.this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createinitial);
            WifiMainActivity.this.m_wifiLinearLayout.setSearchWiFiText(R.string.stopsearchwifiap);
            WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiText(R.string.createwifiap);
        }
    }

    private class WIFIListViewItemClickListener implements AdapterView.OnItemClickListener {
        private WIFIListViewItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (((ScanResult) WifiMainActivity.this.m_listWifi.get(i)).SSID.equals(WifiMainActivity.this.m_strSargetSSID) || (WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() != null && ((ScanResult) WifiMainActivity.this.m_listWifi.get(i)).SSID.equals(WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID()))) {
                if (WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo() == null || WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() == null || !((ScanResult) WifiMainActivity.this.m_listWifi.get(i)).SSID.equals(WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID())) {
                    return;
                }
                WifiMainActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.exitService();
                WifiMainActivity.this.m_iWiFiSupervise.disconnectWifi(WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getNetworkId());
                return;
            }
            WifiMainActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.exitService();
            WifiMainActivity.this.m_iWiFiSupervise.addNetwork(WifiMainActivity.this.m_iWiFiSupervise.createWifiInfo(((ScanResult) WifiMainActivity.this.m_listWifi.get(i)).SSID, WifiMainActivity.m_strPassword, 3, "wt"));
            ((WiFiAdpter.WiFiViewHolder) view.getTag()).textConnect.setVisibility(8);
            ((WiFiAdpter.WiFiViewHolder) view.getTag()).progressBConnecting.setVisibility(0);
            ((WiFiAdpter.WiFiViewHolder) view.getTag()).linearLConnectOk.setVisibility(8);
            WifiMainActivity wifiMainActivity = WifiMainActivity.this;
            wifiMainActivity.m_strSargetSSID = ((ScanResult) wifiMainActivity.m_listWifi.get(i)).SSID;
        }
    }

    private class BtnCreateWiFiOnClickListener implements View.OnClickListener {
        private BtnCreateWiFiOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            String str;
            if (WifiMainActivity.this.m_searchWiFiAPProcess.running) {
                Toast.makeText(WifiMainActivity.this.m_context, R.string.notcreatenotify, 0).show();
                return;
            }
            int i = AnonymousClass2.$SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WifiMainActivity.this.m_ipmsgApp.getCreateWiFiAPState().ordinal()];
            if (i == 1) {
                WifiMainActivity.this.createWiFiAP();
                return;
            }
            if (i != 2) {
                if (i != 3) {
                    return;
                }
                WifiMainActivity.this.m_linearLCreatefail.setVisibility(8);
                WifiMainActivity.this.createWiFiAP();
                return;
            }
            WifiMainActivity.this.m_iWiFiSupervise.closeWifiAp();
            Global.g_bWiFiAPWorking = false;
            WifiMainActivity.this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createinitial);
            WifiMainActivity.this.m_wifiLinearLayout.setCreateWiFiText(R.string.createwifiap);
            WifiMainActivity.this.m_linearLCreatesuccess.setVisibility(8);
            WifiMainActivity.this.m_linearLCreateinitial.setVisibility(0);
            WifiMainActivity.this.m_bIsClickSearchBtn = false;
            Button button = WifiMainActivity.this.m_btnLinkedWiFi;
            if (WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() == null) {
                str = WifiMainActivity.m_strNoLink;
            } else {
                str = WifiMainActivity.this.m_stru + WifiMainActivity.this.m_iWiFiSupervise.getWifiInfo().getSSID() + WifiMainActivity.this.m_stritalicu;
            }
            button.setText(Html.fromHtml(str));
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.wifi.WifiMainActivity$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$display$data$WTOperateEnum;
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$display$ui$wifi$WiFiState;

        static {
            int[] iArr = new int[WiFiState.values().length];
            $SwitchMap$com$netfeige$display$ui$wifi$WiFiState = iArr;
            try {
                iArr[WiFiState.createinitial.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WiFiState.createdsuccess.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WiFiState.createdfail.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WiFiState.searchinitial.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WiFiState.searching.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WiFiState.searchedsuccess.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WiFiState.searchedfail.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            int[] iArr2 = new int[WTOperateEnum.values().length];
            $SwitchMap$com$netfeige$display$data$WTOperateEnum = iArr2;
            try {
                iArr2[WTOperateEnum.CREATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$netfeige$display$data$WTOperateEnum[WTOperateEnum.SEARCH.ordinal()] = 2;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$netfeige$display$data$WTOperateEnum[WTOperateEnum.CLOSE.ordinal()] = 3;
            } catch (NoSuchFieldError unused10) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createWiFiAP() {
        if (this.m_iWiFiSupervise.getWifiAPState() == 4) {
            Toast.makeText(this.m_context, R.string.not_create_ap, 0).show();
            return;
        }
        if (this.m_iWiFiSupervise.wifiEnable()) {
            this.wTOperateEnum = WTOperateEnum.CREATE;
            this.m_LinearLDialog.setVisibility(0);
            this.m_textVContentDialog.setText(R.string.close_wifi_prompt);
            return;
        }
        if ((this.m_iWiFiSupervise.getWifiAPState() == 3 || this.m_iWiFiSupervise.getWifiAPState() == 13) && !this.m_iWiFiSupervise.getApSSID().startsWith(this.m_ipmsgApp.g_strAPPref)) {
            this.wTOperateEnum = WTOperateEnum.CREATE;
            this.m_LinearLDialog.setVisibility(0);
            this.m_textVContentDialog.setText(R.string.ap_used);
        } else {
            if ((this.m_iWiFiSupervise.getWifiAPState() == 3 || this.m_iWiFiSupervise.getWifiAPState() == 13) && this.m_iWiFiSupervise.getApSSID().startsWith(this.m_ipmsgApp.g_strAPPref)) {
                this.wTOperateEnum = WTOperateEnum.CLOSE;
                this.m_LinearLDialog.setVisibility(0);
                this.m_textVContentDialog.setText(R.string.close_ap_prompt);
                return;
            }
            createWiFiAPBasic();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createWiFiAPBasic() {
        goneSearchWiFiAP();
        if (this.m_searchWiFiAPProcess.running) {
            this.m_searchWiFiAPProcess.stop();
        }
        this.m_iWiFiSupervise.closeWiFi();
        this.m_iWiFiSupervise.createWifiAp(this.m_iWiFiSupervise.createWifiInfo(this.m_strSSID, m_strPassword, 3, "ap"), true);
        this.m_createWiFiAPProcess.start();
        this.m_listWifi.clear();
        this.m_ipmsgApp.ipmsgService.userList.clear();
        this.m_wTAdapter.notifyDataSetChanged();
        this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.creating);
        this.m_ipmsgApp.setSearchWiFiAPState(WiFiState.searchinitial);
        this.m_linearLCreateinitial.setVisibility(8);
        this.m_linearLCreating.setVisibility(0);
        this.m_wifiLinearLayout.setCreateWiFiBackgroundResource(R.drawable.wifi_disable);
        this.m_wifiLinearLayout.setCreateWiFiEnabled(false);
    }

    private class BtnSearchWiFiOnClickListener implements View.OnClickListener {
        private BtnSearchWiFiOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (WifiMainActivity.this.m_createWiFiAPProcess.running) {
                Toast.makeText(WifiMainActivity.this.m_context, R.string.notsearchnotify, 0).show();
                return;
            }
            int i = AnonymousClass2.$SwitchMap$com$netfeige$display$ui$wifi$WiFiState[WifiMainActivity.this.m_ipmsgApp.getSearchWiFiAPState().ordinal()];
            if (i == 4) {
                WifiMainActivity.this.searchWiFiAPBasic();
                return;
            }
            if (i == 5) {
                WifiMainActivity.this.m_searchWiFiAPProcess.stop();
                WifiMainActivity.this.m_wifiLinearLayout.setSearchWiFiText(R.string.searchwifiap);
                WifiMainActivity.this.m_linearLSearching.setVisibility(8);
                WifiMainActivity.this.m_ipmsgApp.setSearchWiFiAPState(WiFiState.searchinitial);
                return;
            }
            if (i == 6) {
                WifiMainActivity.this.searchWiFiAPBasic();
            } else {
                if (i != 7) {
                    return;
                }
                WifiMainActivity.this.m_linearLSearchFail.setVisibility(8);
                WifiMainActivity.this.searchWiFiAPBasic();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchWiFiAPBasic() {
        if (this.m_searchWiFiAPProcess.running) {
            this.m_searchWiFiAPProcess.stop();
            return;
        }
        if (!this.m_iWiFiSupervise.wifiEnable()) {
            this.wTOperateEnum = WTOperateEnum.SEARCH;
            this.m_LinearLDialog.setVisibility(0);
            if (this.m_iWiFiSupervise.getWifiAPState() == 3 || this.m_iWiFiSupervise.getWifiAPState() == 13) {
                this.m_textVContentDialog.setText(R.string.opened_ap_prompt);
                return;
            } else {
                this.m_textVContentDialog.setText(R.string.closed_wifi_prompt);
                return;
            }
        }
        startScanWiFiAP();
    }

    private void startScanWiFiAP() {
        this.m_bIsClickSearchBtn = true;
        this.m_ipmsgApp.setSearchWiFiAPState(WiFiState.searching);
        goneCreateWiFiAP();
        this.m_listWifi.clear();
        this.m_wTAdapter.notifyDataSetChanged();
        this.m_iWiFiSupervise.searchWifiAp();
        this.m_searchWiFiAPProcess.start();
        this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createinitial);
        this.m_linearLSearching.setVisibility(0);
        this.m_linearLSearchSuccess.setVisibility(0);
        this.m_linearLSearchFail.setVisibility(8);
        this.m_wifiLinearLayout.setSearchWiFiText(R.string.stopsearchwifiap);
    }

    private class BtnBackOnClickListener implements View.OnClickListener {
        private BtnBackOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                WifiMainActivity.this.onBackPressed();
                WifiMainActivity.this.m_ipmsgApp.ipmsgService.userList.clear();
                IpmsgActivity.m_UserAdapter.notifyDataSetChanged();
                WifiMainActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.entryService(null, true);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWiFi() {
        try {
            this.m_listWifi.clear();
            if (this.m_iWiFiSupervise.getWifiManager().getScanResults() != null) {
                for (int i = 0; i < this.m_iWiFiSupervise.getWifiManager().getScanResults().size(); i++) {
                    if (this.m_iWiFiSupervise.getWifiManager().getScanResults().get(i).SSID.startsWith("Feige_")) {
                        this.m_listWifi.add(this.m_iWiFiSupervise.getWifiManager().getScanResults().get(i));
                    }
                }
            }
            if (this.m_listWifi.isEmpty() && !this.m_searchWiFiAPProcess.running) {
                this.m_linearLCreateinitial.setVisibility(8);
                this.m_linearLSearchSuccess.setVisibility(8);
                this.m_linearLSearchFail.setVisibility(0);
                this.m_btnCurrentWiFi.setText(Html.fromHtml(this.m_iWiFiSupervise.getWifiInfo().getSSID() == null ? m_strNoLink : this.m_stru + this.m_iWiFiSupervise.getWifiInfo().getSSID() + this.m_stritalicu));
            } else if (!this.m_listWifi.isEmpty()) {
                this.m_linearLSearching.setVisibility(8);
                this.m_ipmsgApp.setSearchWiFiAPState(WiFiState.searchedsuccess);
                this.m_wifiLinearLayout.setSearchWiFiText(R.string.searchwifiap);
                if (this.m_searchWiFiAPProcess.running) {
                    this.m_searchWiFiAPProcess.stop();
                }
            }
            this.m_wTAdapter.notifyDataSetChanged();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    class CreateWiFiAPProcess implements Runnable {
        private Thread thread = null;
        public boolean running = false;
        private long startTime = 0;

        CreateWiFiAPProcess() {
        }

        public synchronized void start() {
            this.thread = new Thread(this);
            this.running = true;
            this.startTime = System.currentTimeMillis();
            this.thread.start();
        }

        public synchronized void stop() {
            this.running = false;
            this.thread = null;
            this.startTime = 0L;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (this.running) {
                if (WifiMainActivity.this.m_iWiFiSupervise.getWifiAPState() == 3 || WifiMainActivity.this.m_iWiFiSupervise.getWifiAPState() == 13) {
                    Message messageObtain = Message.obtain(WifiMainActivity.this.WiFiAPHandler);
                    messageObtain.what = 0;
                    messageObtain.sendToTarget();
                } else if (System.currentTimeMillis() - this.startTime >= 30000) {
                    Message messageObtain2 = Message.obtain(WifiMainActivity.this.WiFiAPHandler);
                    messageObtain2.what = 1;
                    messageObtain2.sendToTarget();
                }
                try {
                    Thread.sleep(5L);
                } catch (Exception unused) {
                }
            }
        }
    }

    class SearchWiFiAPProcess implements Runnable {
        private Thread thread = null;
        public boolean running = false;
        private long startTime = 0;

        SearchWiFiAPProcess() {
        }

        public synchronized void start() {
            this.thread = new Thread(this);
            this.running = true;
            this.startTime = System.currentTimeMillis();
            this.thread.start();
        }

        public synchronized void stop() {
            this.running = false;
            this.thread = null;
            this.startTime = 0L;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (this.running) {
                if (System.currentTimeMillis() - this.startTime >= 10000) {
                    Message messageObtain = Message.obtain(WifiMainActivity.this.WiFiAPHandler);
                    messageObtain.what = 5;
                    messageObtain.sendToTarget();
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception unused) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goneCreateWiFiAP() {
        this.m_linearLCreateinitial.setVisibility(8);
        this.m_linearLCreating.setVisibility(8);
        this.m_linearLCreatefail.setVisibility(8);
        this.m_linearLCreatesuccess.setVisibility(8);
    }

    private void goneSearchWiFiAP() {
        this.m_linearLSearching.setVisibility(8);
        this.m_linearLSearchFail.setVisibility(8);
        this.m_linearLSearchSuccess.setVisibility(8);
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void wifiStatusNotification(Intent intent) {
        this.m_iWiFiSupervise.getWifiManager();
        int intExtra = intent.getIntExtra("wifi_state", 0);
        if ((2 == intExtra || 3 == intExtra) && Global.g_bWiFiAPWorking) {
            this.m_iWiFiSupervise.closeWifiAp();
            this.m_linearLCreatesuccess.setVisibility(8);
            this.m_linearLCreateinitial.setVisibility(0);
            this.m_ipmsgApp.setCreateWiFiAPState(WiFiState.createinitial);
            this.m_bIsClickSearchBtn = false;
            Global.g_bWiFiAPWorking = false;
        }
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void scanResultsAvailable() {
        Message messageObtain = Message.obtain(this.WiFiAPHandler);
        messageObtain.what = 3;
        messageObtain.sendToTarget();
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void handleConnectChange() {
        Message messageObtain = Message.obtain(this.WiFiAPHandler);
        messageObtain.what = 4;
        messageObtain.sendToTarget();
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
        Message messageObtain = Message.obtain(this.WiFiAPHandler);
        messageObtain.what = 7;
        messageObtain.sendToTarget();
    }

    public String getSargetSSID() {
        return this.m_strSargetSSID;
    }

    public void setSargetSSID(String str) {
        this.m_strSargetSSID = str;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            this.m_ipmsgApp.ipmsgService.userList.clear();
            IpmsgActivity.m_UserAdapter.notifyDataSetChanged();
            this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.entryService(null, true);
        }
        return super.onKeyDown(i, keyEvent);
    }
}

