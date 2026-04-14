package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.broadcast.WTBroadcast;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.data.WTAdapter;
import com.netfeige.display.data.WTOperateEnum;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.service.IpmsgService;
import com.netfeige.wt.WifiAdmin;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WTActivity extends Activity implements WTBroadcast.EventHandler, IpmsgService.EventHandler {
    private WTSearchAnimationFrameLayout m_FrameLWTSearchAnimation;
    private LinearLayout m_LinearLDialog;
    private LinearLayout m_LinearLIntroduction;
    private Button m_btnBack;
    private Button m_btnCancelDialog;
    private Button m_btnConfirmDialog;
    private Button m_btnCreateWT;
    private Button m_btnSearchWT;
    private CreateAPProcess m_createAPProcess;
    private GifView m_gifRadar;
    private LinearLayout m_linearLCreateAP;
    private ListView m_listVWT;
    private IpmsgApplication m_myApp;
    private ProgressBar m_progBarCreatingAP;
    private TextView m_textVContentDialog;
    private TextView m_textVPromptAP;
    private TextView m_textVWTPrompt;
    private WTAdapter m_wTAdapter;
    private WifiAdmin m_wiFiAdmin;
    private WTSearchProcess m_wtSearchProcess;
    public final int m_nWTSearchTimeOut = 0;
    public final int m_nWTScanResult = 1;
    public final int m_nWTConnectResult = 2;
    public final int m_nCreateAPResult = 3;
    public final int m_nUserResult = 4;
    public final int m_nWTConnected = 5;
    private String m_strFrist = null;
    ArrayList<ScanResult> m_listWifi = new ArrayList<>();
    public String m_strSargetSSID = "";
    private WTOperateEnum wTOperateEnum = WTOperateEnum.NOTHING;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.WTActivity.8
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                WTActivity.this.m_wtSearchProcess.stop();
                WTActivity.this.m_FrameLWTSearchAnimation.reset();
                WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
            } else {
                if (i != 1) {
                    if (i == 2) {
                        WTActivity.this.m_wTAdapter.notifyDataSetChanged();
                    } else if (i == 3) {
                        WTActivity.this.m_btnCreateWT.setVisibility(0);
                        if (WTActivity.this.m_wiFiAdmin.getWifiApState() == 3 || WTActivity.this.m_wiFiAdmin.getWifiApState() == 13) {
                            WTActivity.this.m_gifRadar.setVisibility(0);
                            WTActivity.this.m_progBarCreatingAP.setVisibility(8);
                            WTActivity.this.m_btnCreateWT.setBackgroundResource(R.drawable.x_ap_close);
                            WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                            TextView textView = WTActivity.this.m_textVPromptAP;
                            StringBuilder sb = new StringBuilder();
                            sb.append(WTActivity.this.getString(R.string.pre_wt_connect_ok));
                            sb.append(WTActivity.this.m_myApp.ipmsgService.userList.size() > 1 ? WTActivity.this.m_myApp.ipmsgService.userList.size() - 1 : 0);
                            sb.append(WTActivity.this.getString(R.string.middle_wt_connect_ok));
                            sb.append(WTActivity.this.m_wiFiAdmin.getApSSID());
                            sb.append(WTActivity.this.getString(R.string.suf_wt_connect_ok));
                            textView.setText(Html.fromHtml(sb.toString()));
                        } else {
                            WTActivity.this.m_progBarCreatingAP.setVisibility(8);
                            WTActivity.this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
                            WTActivity.this.m_textVPromptAP.setText(WTActivity.this.getString(R.string.create_ap_fail));
                        }
                        WTActivity.this.m_createAPProcess.stop();
                    } else if (i != 4) {
                        if (i == 5) {
                            WTActivity.this.onBackPressed();
                        }
                    } else if ((WTActivity.this.m_wiFiAdmin.getWifiApState() == 3 || WTActivity.this.m_wiFiAdmin.getWifiApState() == 13) && WTActivity.this.m_wiFiAdmin.getApSSID().startsWith(WTActivity.this.m_myApp.g_strAPPref)) {
                        WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                        TextView textView2 = WTActivity.this.m_textVPromptAP;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(WTActivity.this.getString(R.string.pre_wt_connect_ok));
                        sb2.append(WTActivity.this.m_myApp.ipmsgService.userList.size() > 1 ? WTActivity.this.m_myApp.ipmsgService.userList.size() - 1 : 0);
                        sb2.append(WTActivity.this.getString(R.string.middle_wt_connect_ok));
                        sb2.append(WTActivity.this.m_wiFiAdmin.getApSSID());
                        sb2.append(WTActivity.this.getString(R.string.suf_wt_connect_ok));
                        textView2.setText(Html.fromHtml(sb2.toString()));
                    }
                }
                super.handleMessage(message);
            }
            WTActivity.this.m_listWifi.clear();
            if (WTActivity.this.m_wiFiAdmin.mWifiManager.getScanResults() != null) {
                for (int i2 = 0; i2 < WTActivity.this.m_wiFiAdmin.mWifiManager.getScanResults().size(); i2++) {
                    if (WTActivity.this.m_wiFiAdmin.mWifiManager.getScanResults().get(i2).SSID.startsWith("Feige_")) {
                        WTActivity.this.m_listWifi.add(WTActivity.this.m_wiFiAdmin.mWifiManager.getScanResults().get(i2));
                    }
                }
            }
            for (int i3 = 0; i3 < WTActivity.this.m_listWifi.size(); i3++) {
                Log.e("wifi", WTActivity.this.m_listWifi.get(i3).BSSID);
            }
            if (WTActivity.this.m_listWifi.isEmpty() && !WTActivity.this.m_wtSearchProcess.running) {
                WTActivity.this.m_textVWTPrompt.setVisibility(0);
                WTActivity.this.m_textVWTPrompt.setText(R.string.wt_list_empty);
            } else if (!WTActivity.this.m_listWifi.isEmpty()) {
                WTActivity.this.m_textVWTPrompt.setVisibility(8);
                if (WTActivity.this.m_wtSearchProcess.running) {
                    WTActivity.this.m_wtSearchProcess.stop();
                    WTActivity.this.m_FrameLWTSearchAnimation.reset();
                    WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                }
            }
            WTActivity.this.m_wTAdapter.notifyDataSetChanged();
            super.handleMessage(message);
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
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.wt_main);
        this.m_wtSearchProcess = new WTSearchProcess();
        this.m_createAPProcess = new CreateAPProcess();
        this.m_wiFiAdmin = WifiAdmin.getInstance(this);
        this.m_LinearLIntroduction = (LinearLayout) findViewById(R.id.introduction_layout_wt_main);
        final IDataConfig dataConfig = DataConfig.getInstance(this);
        this.m_LinearLIntroduction.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.WTActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WTActivity.this.m_LinearLIntroduction.setVisibility(8);
                dataConfig.Write(32, ContentTree.VIDEO_ID);
                WTActivity.this.init();
            }
        });
        Button button = (Button) findViewById(R.id.back_btn_wt_main);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.WTActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WTActivity.this.onBackPressed();
                WTActivity.this.m_myApp.ipmsgService.userList.clear();
                IpmsgActivity.m_UserAdapter.notifyDataSetChanged();
                WTActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.entryService(null, true);
            }
        });
        this.m_linearLCreateAP = (LinearLayout) findViewById(R.id.create_ap_llayout_wt_main);
        this.m_progBarCreatingAP = (ProgressBar) findViewById(R.id.creating_progressBar_wt_main);
        this.m_textVPromptAP = (TextView) findViewById(R.id.prompt_ap_text_wt_main);
        Button button2 = (Button) findViewById(R.id.search_btn_wt_main);
        this.m_btnSearchWT = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.WTActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (WTActivity.this.m_wtSearchProcess.running) {
                    WTActivity.this.m_wtSearchProcess.stop();
                    WTActivity.this.m_FrameLWTSearchAnimation.reset();
                    WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                    return;
                }
                if (!WTActivity.this.m_wiFiAdmin.mWifiManager.isWifiEnabled()) {
                    WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_stop_wt);
                    WTActivity.this.wTOperateEnum = WTOperateEnum.SEARCH;
                    WTActivity.this.m_LinearLDialog.setVisibility(0);
                    if (WTActivity.this.m_wiFiAdmin.getWifiApState() == 3 || WTActivity.this.m_wiFiAdmin.getWifiApState() == 13) {
                        WTActivity.this.m_textVContentDialog.setText(R.string.opened_ap_prompt);
                        return;
                    } else {
                        WTActivity.this.m_textVContentDialog.setText(R.string.closed_wifi_prompt);
                        return;
                    }
                }
                WTActivity.this.m_listWifi.clear();
                WTActivity.this.m_wTAdapter.notifyDataSetChanged();
                WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_stop_wt);
                WTActivity.this.m_textVWTPrompt.setVisibility(0);
                WTActivity.this.m_textVWTPrompt.setText(R.string.wt_searching);
                WTActivity.this.m_linearLCreateAP.setVisibility(8);
                WTActivity.this.m_gifRadar.setVisibility(8);
                WTActivity.this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
                WTActivity.this.m_wiFiAdmin.startScan();
                WTActivity.this.m_wtSearchProcess.start();
                WTActivity.this.m_FrameLWTSearchAnimation.startRadarAnimation();
            }
        });
        Button button3 = (Button) findViewById(R.id.create_btn_wt_main);
        this.m_btnCreateWT = button3;
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.WTActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (WTActivity.this.m_wiFiAdmin.getWifiApState() != 4) {
                    if (!WTActivity.this.m_wiFiAdmin.mWifiManager.isWifiEnabled()) {
                        if ((WTActivity.this.m_wiFiAdmin.getWifiApState() != 3 && WTActivity.this.m_wiFiAdmin.getWifiApState() != 13) || WTActivity.this.m_wiFiAdmin.getApSSID().startsWith(WTActivity.this.m_myApp.g_strAPPref)) {
                            if ((WTActivity.this.m_wiFiAdmin.getWifiApState() != 3 && WTActivity.this.m_wiFiAdmin.getWifiApState() != 13) || !WTActivity.this.m_wiFiAdmin.getApSSID().startsWith(WTActivity.this.m_myApp.g_strAPPref)) {
                                if (WTActivity.this.m_wtSearchProcess.running) {
                                    WTActivity.this.m_wtSearchProcess.stop();
                                    WTActivity.this.m_FrameLWTSearchAnimation.reset();
                                }
                                WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                                WTActivity.this.m_wiFiAdmin.closeWifi();
                                WTActivity.this.m_wiFiAdmin.createWiFiAP(WTActivity.this.m_wiFiAdmin.createWifiInfo(WTActivity.this.m_myApp.g_strAPPref + Public_Tools.getLocalHostName(), "Feige6688", 3, "ap"), true);
                                WTActivity.this.m_createAPProcess.start();
                                WTActivity.this.m_listWifi.clear();
                                WTActivity.this.m_wTAdapter.notifyDataSetChanged();
                                WTActivity.this.m_linearLCreateAP.setVisibility(0);
                                WTActivity.this.m_progBarCreatingAP.setVisibility(0);
                                WTActivity.this.m_btnCreateWT.setVisibility(8);
                                WTActivity.this.m_textVWTPrompt.setVisibility(8);
                                WTActivity.this.m_textVPromptAP.setText(WTActivity.this.getString(R.string.creating_ap));
                                return;
                            }
                            WTActivity.this.wTOperateEnum = WTOperateEnum.CLOSE;
                            WTActivity.this.m_LinearLDialog.setVisibility(0);
                            WTActivity.this.m_textVContentDialog.setText(R.string.close_ap_prompt);
                            return;
                        }
                        WTActivity.this.wTOperateEnum = WTOperateEnum.CREATE;
                        WTActivity.this.m_LinearLDialog.setVisibility(0);
                        WTActivity.this.m_textVContentDialog.setText(R.string.ap_used);
                        return;
                    }
                    WTActivity.this.wTOperateEnum = WTOperateEnum.CREATE;
                    WTActivity.this.m_LinearLDialog.setVisibility(0);
                    WTActivity.this.m_textVContentDialog.setText(R.string.close_wifi_prompt);
                    return;
                }
                WTActivity wTActivity = WTActivity.this;
                Public_Tools.showToast(wTActivity, wTActivity.getString(R.string.not_create_ap), 0);
            }
        });
        this.m_FrameLWTSearchAnimation = (WTSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main);
        this.m_listVWT = (ListView) findViewById(R.id.wt_list_wt_main);
        WTAdapter wTAdapter = new WTAdapter(this, R.layout.wtitem, this.m_listWifi);
        this.m_wTAdapter = wTAdapter;
        this.m_listVWT.setAdapter((ListAdapter) wTAdapter);
        this.m_listVWT.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.WTActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (WTActivity.this.m_listWifi.get(i).SSID.equals(WTActivity.this.m_strSargetSSID) || (WTActivity.this.m_wiFiAdmin.getWifiInfo().getSSID() != null && WTActivity.this.m_listWifi.get(i).SSID.equals(WTActivity.this.m_wiFiAdmin.getWifiInfo().getSSID()))) {
                    if (WTActivity.this.m_wiFiAdmin.getWifiInfo() == null || WTActivity.this.m_wiFiAdmin.getWifiInfo().getSSID() == null || !WTActivity.this.m_listWifi.get(i).SSID.equals(WTActivity.this.m_wiFiAdmin.getWifiInfo().getSSID())) {
                        return;
                    }
                    WTActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.exitService();
                    WTActivity.this.m_wiFiAdmin.disconnectWifi(WTActivity.this.m_wiFiAdmin.getWifiInfo().getNetworkId());
                    return;
                }
                WTActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.exitService();
                WTActivity.this.m_wiFiAdmin.addNetwork(WTActivity.this.m_wiFiAdmin.createWifiInfo(WTActivity.this.m_listWifi.get(i).SSID, "Feige6688", 3, "wt"));
                ((WTAdapter.ViewHolder) view.getTag()).textConnect.setVisibility(8);
                ((WTAdapter.ViewHolder) view.getTag()).progressBConnecting.setVisibility(0);
                ((WTAdapter.ViewHolder) view.getTag()).linearLConnectOk.setVisibility(8);
                WTActivity wTActivity = WTActivity.this;
                wTActivity.m_strSargetSSID = wTActivity.m_listWifi.get(i).SSID;
            }
        });
        this.m_textVWTPrompt = (TextView) findViewById(R.id.wt_prompt_wt_main);
        this.m_gifRadar = (GifView) findViewById(R.id.radar_gif_wt_main);
        this.m_LinearLDialog = (LinearLayout) findViewById(R.id.dialog_layout_wt_main);
        this.m_textVContentDialog = (TextView) findViewById(R.id.content_text_wtdialog);
        Button button4 = (Button) findViewById(R.id.confirm_btn_wtdialog);
        this.m_btnConfirmDialog = button4;
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.WTActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WTActivity.this.m_LinearLDialog.setVisibility(8);
                int i = AnonymousClass9.$SwitchMap$com$netfeige$display$data$WTOperateEnum[WTActivity.this.wTOperateEnum.ordinal()];
                if (i == 1) {
                    if (WTActivity.this.m_wtSearchProcess.running) {
                        WTActivity.this.m_wtSearchProcess.stop();
                        WTActivity.this.m_FrameLWTSearchAnimation.reset();
                    }
                    WTActivity.this.m_wiFiAdmin.closeWifi();
                    WTActivity.this.m_wiFiAdmin.createWiFiAP(WTActivity.this.m_wiFiAdmin.createWifiInfo(WTActivity.this.m_myApp.g_strAPPref + Public_Tools.getLocalHostName(), "Feige6688", 3, "ap"), true);
                    WTActivity.this.m_createAPProcess.start();
                    WTActivity.this.m_listWifi.clear();
                    WTActivity.this.m_wTAdapter.notifyDataSetChanged();
                    WTActivity.this.m_linearLCreateAP.setVisibility(0);
                    WTActivity.this.m_progBarCreatingAP.setVisibility(0);
                    WTActivity.this.m_btnCreateWT.setVisibility(8);
                    WTActivity.this.m_textVWTPrompt.setVisibility(8);
                    WTActivity.this.m_textVPromptAP.setText(WTActivity.this.getString(R.string.creating_ap));
                    WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                    return;
                }
                if (i != 2) {
                    if (i != 3) {
                        return;
                    }
                    WTActivity.this.m_textVWTPrompt.setVisibility(0);
                    WTActivity.this.m_textVWTPrompt.setText("");
                    WTActivity.this.m_linearLCreateAP.setVisibility(8);
                    WTActivity.this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
                    WTActivity.this.m_gifRadar.setVisibility(8);
                    WTActivity.this.m_wiFiAdmin.createWiFiAP(WTActivity.this.m_wiFiAdmin.createWifiInfo(WTActivity.this.m_wiFiAdmin.getApSSID(), "81028066", 3, "ap"), false);
                    return;
                }
                WTActivity.this.m_textVWTPrompt.setVisibility(0);
                WTActivity.this.m_textVWTPrompt.setText(R.string.wt_searching);
                WTActivity.this.m_linearLCreateAP.setVisibility(8);
                WTActivity.this.m_btnCreateWT.setVisibility(0);
                WTActivity.this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
                WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_stop_wt);
                WTActivity.this.m_gifRadar.setVisibility(8);
                if (WTActivity.this.m_createAPProcess.running) {
                    WTActivity.this.m_createAPProcess.stop();
                }
                WTActivity.this.m_wiFiAdmin.createWiFiAP(WTActivity.this.m_wiFiAdmin.createWifiInfo(WTActivity.this.m_wiFiAdmin.getApSSID(), "Feige6688", 3, "ap"), false);
                WTActivity.this.m_wiFiAdmin.OpenWifi();
                WTActivity.this.m_wtSearchProcess.start();
                WTActivity.this.m_FrameLWTSearchAnimation.startRadarAnimation();
            }
        });
        Button button5 = (Button) findViewById(R.id.cancel_btn_wtdialog);
        this.m_btnCancelDialog = button5;
        button5.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.WTActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WTActivity.this.m_LinearLDialog.setVisibility(8);
                if (WTActivity.this.m_wtSearchProcess.running) {
                    WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_stop_wt);
                } else {
                    WTActivity.this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                }
            }
        });
        WTBroadcast.ehList.add(this);
        this.m_myApp.ipmsgService.ehList.add(this);
        String strRead = dataConfig.Read(32);
        this.m_strFrist = strRead;
        if (strRead.equals(ContentTree.ROOT_ID)) {
            this.m_LinearLIntroduction.setVisibility(0);
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.WTActivity$9, reason: invalid class name */
    static /* synthetic */ class AnonymousClass9 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$display$data$WTOperateEnum;

        static {
            int[] iArr = new int[WTOperateEnum.values().length];
            $SwitchMap$com$netfeige$display$data$WTOperateEnum = iArr;
            try {
                iArr[WTOperateEnum.CREATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$display$data$WTOperateEnum[WTOperateEnum.SEARCH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$display$data$WTOperateEnum[WTOperateEnum.CLOSE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        ((IpmsgApplication) getApplication()).currentActivity = this;
        if (this.m_myApp.g_bBackRuning) {
            this.m_myApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        if (!this.m_strFrist.equals(ContentTree.ROOT_ID)) {
            init();
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

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            this.m_myApp.ipmsgService.userList.clear();
            IpmsgActivity.m_UserAdapter.notifyDataSetChanged();
            this.m_myApp.ipmsgService.m_DataSource.m_Protocol.entryService(null, true);
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        WTBroadcast.ehList.remove(this);
        this.m_myApp.ipmsgService.ehList.remove(this);
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        if (this.m_wtSearchProcess.running || this.m_createAPProcess.running) {
            return;
        }
        this.m_listWifi.clear();
        if (this.m_wiFiAdmin.mWifiManager.getScanResults() != null) {
            for (int i = 0; i < this.m_wiFiAdmin.mWifiManager.getScanResults().size(); i++) {
                if (this.m_wiFiAdmin.mWifiManager.getScanResults().get(i).SSID.startsWith("Feige_")) {
                    this.m_listWifi.add(this.m_wiFiAdmin.mWifiManager.getScanResults().get(i));
                }
            }
        }
        this.m_gifRadar.setVisibility(8);
        this.m_wTAdapter.notifyDataSetChanged();
        if (Public_Tools.isWifiConnect() && !Public_Tools.getWifiApState()) {
            this.m_wiFiAdmin.startScan();
            this.m_wtSearchProcess.start();
            this.m_FrameLWTSearchAnimation.startRadarAnimation();
            this.m_textVWTPrompt.setVisibility(0);
            this.m_textVWTPrompt.setText(R.string.wt_searching);
            this.m_linearLCreateAP.setVisibility(8);
            this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
            return;
        }
        if (!Public_Tools.isWifiConnect()) {
            this.m_wiFiAdmin.OpenWifi();
            this.m_wtSearchProcess.start();
            this.m_FrameLWTSearchAnimation.startRadarAnimation();
            this.m_textVWTPrompt.setVisibility(0);
            this.m_textVWTPrompt.setText(R.string.wt_searching);
            this.m_linearLCreateAP.setVisibility(8);
            this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
            return;
        }
        if (Public_Tools.getWifiApState()) {
            if (this.m_wiFiAdmin.getApSSID().startsWith(this.m_myApp.g_strAPPref)) {
                this.m_btnSearchWT.setBackgroundResource(R.drawable.x_search_wt);
                this.m_textVWTPrompt.setVisibility(8);
                this.m_linearLCreateAP.setVisibility(0);
                this.m_progBarCreatingAP.setVisibility(8);
                this.m_btnCreateWT.setBackgroundResource(R.drawable.x_ap_close);
                this.m_gifRadar.setVisibility(0);
                TextView textView = this.m_textVPromptAP;
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.pre_wt_connect_ok));
                sb.append(this.m_myApp.ipmsgService.userList.size() > 1 ? this.m_myApp.ipmsgService.userList.size() - 1 : 0);
                sb.append(getString(R.string.middle_wt_connect_ok));
                sb.append(this.m_wiFiAdmin.getApSSID());
                sb.append(getString(R.string.suf_wt_connect_ok));
                textView.setText(Html.fromHtml(sb.toString()));
                return;
            }
            this.m_textVWTPrompt.setVisibility(8);
            this.m_linearLCreateAP.setVisibility(8);
            this.m_progBarCreatingAP.setVisibility(0);
            this.m_btnCreateWT.setBackgroundResource(R.drawable.x_wt_create);
            this.m_textVPromptAP.setText("");
        }
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void wifiStatusNotification(Intent intent) {
        this.m_wiFiAdmin.mWifiManager.getWifiState();
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void scanResultsAvailable() {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 1;
        messageObtain.sendToTarget();
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void handleConnectChange() {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 2;
        messageObtain.sendToTarget();
    }

    class WTSearchProcess implements Runnable {
        private Thread thread = null;
        public boolean running = false;
        private long startTime = 0;

        WTSearchProcess() {
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
                if (System.currentTimeMillis() - this.startTime >= 30000) {
                    Message messageObtain = Message.obtain(WTActivity.this.handler);
                    messageObtain.what = 0;
                    messageObtain.sendToTarget();
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception unused) {
                }
            }
        }
    }

    class CreateAPProcess implements Runnable {
        private Thread thread = null;
        public boolean running = false;
        private long startTime = 0;

        CreateAPProcess() {
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
                if (WTActivity.this.m_wiFiAdmin.getWifiApState() == 3 || WTActivity.this.m_wiFiAdmin.getWifiApState() == 13 || System.currentTimeMillis() - this.startTime >= 30000) {
                    Message messageObtain = Message.obtain(WTActivity.this.handler);
                    messageObtain.what = 3;
                    messageObtain.sendToTarget();
                }
                try {
                    Thread.sleep(5L);
                } catch (Exception unused) {
                }
            }
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 4;
        messageObtain.sendToTarget();
    }
}

