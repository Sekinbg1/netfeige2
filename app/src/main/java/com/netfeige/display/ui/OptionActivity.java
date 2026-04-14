package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.geniusgithub.mediarender.MediaRenderActivity;
import com.geniusgithub.mediarender.center.MediaRenderProxy;
import com.netfeige.R;
import com.netfeige.broadcast.NetStatusBroadcast;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OptionActivity extends Activity implements NetStatusBroadcast.EventHandler {
    private LinearLayout aboutLayout;
    private Button backBtn;
    private TextView m_editIpAddr;
    private TextView m_editTCurrentNetSector;
    private TextView m_editTCurrentNetWork;
    private EditText m_editTGroup;
    private EditText m_editTName;
    private ImageView m_imageVHead;
    private LinearLayout m_linearLCurrentNetSector;
    private LinearLayout m_linearLCurrentNetWork;
    private LinearLayout m_linearLDLNAPlayer;
    private LinearLayout m_linearLDimensional;
    private LinearLayout m_linearLFeedback;
    private LinearLayout m_linearLFeigeDownload;
    private LinearLayout m_linearLGeneralSetup;
    private LinearLayout m_linearLHead;
    private LinearLayout m_linearLHelp;
    private LinearLayout m_linearLIPAddr;
    private LinearLayout m_linearLShare;
    public IpmsgApplication m_myApp;
    private TextView m_textVFeigeDownload;
    private TextView m_textVMediaRenderState;
    private Button saveBtn;
    private LinearLayout welcomeLayout;
    private final int m_nWifiCategory = 0;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.OptionActivity.18
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                String defaultLocalHostIP = Public_Tools.getDefaultLocalHostIP();
                OptionActivity.this.m_editIpAddr.setText(defaultLocalHostIP);
                OptionActivity.this.m_editTCurrentNetSector.setText(defaultLocalHostIP.substring(0, defaultLocalHostIP.lastIndexOf(".")));
                String currNetName = Public_Tools.getCurrNetName();
                TextView textView = OptionActivity.this.m_editTCurrentNetWork;
                if (currNetName == null) {
                    currNetName = OptionActivity.this.getString(R.string.no_net_connect);
                }
                textView.setText(currNetName);
            }
            super.handleMessage(message);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.option2);
        this.backBtn = (Button) findViewById(R.id.back_btn_option);
        this.saveBtn = (Button) findViewById(R.id.save_btn_option);
        this.m_editTName = (EditText) findViewById(R.id.name_edit_option);
        this.m_textVFeigeDownload = (TextView) findViewById(R.id.current_download_text_option);
        this.m_textVMediaRenderState = (TextView) findViewById(R.id.current_openorclose_option);
        this.m_editTName.addTextChangedListener(new TextWatcher() { // from class: com.netfeige.display.ui.OptionActivity.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.toString().getBytes("gbk").length > 20) {
                        Public_Tools.showToast(OptionActivity.this, OptionActivity.this.getString(R.string.name_length_prompt), 0);
                        editable.delete(editable.length() - 1, editable.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EditText editText = (EditText) findViewById(R.id.group_edit_option);
        this.m_editTGroup = editText;
        editText.addTextChangedListener(new TextWatcher() { // from class: com.netfeige.display.ui.OptionActivity.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.toString().getBytes("gbk").length > 20) {
                        Public_Tools.showToast(OptionActivity.this, OptionActivity.this.getString(R.string.group_length_prompt), 0);
                        editable.delete(editable.length() - 1, editable.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.m_editIpAddr = (TextView) findViewById(R.id.ip_addr_edit_option);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ip_addr_layout_option);
        this.m_linearLIPAddr = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) NetAddrActivity.class));
            }
        });
        this.m_editTCurrentNetWork = (TextView) findViewById(R.id.current_network_text_option);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.current_network_layout_option);
        this.m_linearLCurrentNetWork = linearLayout2;
        linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Public_Tools.entryNetSetting();
            }
        });
        this.m_editTCurrentNetSector = (TextView) findViewById(R.id.current_netsector_text_option);
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.current_netsector_layout_option);
        this.m_linearLCurrentNetSector = linearLayout3;
        linearLayout3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) NetSectorActivity.class));
            }
        });
        LinearLayout linearLayout4 = (LinearLayout) findViewById(R.id.general_setup_layout_option);
        this.m_linearLGeneralSetup = linearLayout4;
        linearLayout4.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) GeneralSetupActivity.class));
            }
        });
        LinearLayout linearLayout5 = (LinearLayout) findViewById(R.id.feigedownload_layout_option);
        this.m_linearLFeigeDownload = linearLayout5;
        linearLayout5.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) SetFeigeDownloadActivity.class));
            }
        });
        LinearLayout linearLayout6 = (LinearLayout) findViewById(R.id.feedback_layout_option);
        this.m_linearLFeedback = linearLayout6;
        linearLayout6.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) FeedbackActivity.class));
            }
        });
        LinearLayout linearLayout7 = (LinearLayout) findViewById(R.id.share_layout_option);
        this.m_linearLShare = linearLayout7;
        linearLayout7.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Public_Tools.share(OptionActivity.this, null);
            }
        });
        LinearLayout linearLayout8 = (LinearLayout) findViewById(R.id.dlnaplayer_layout_option);
        this.m_linearLDLNAPlayer = linearLayout8;
        linearLayout8.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) MediaRenderActivity.class));
            }
        });
        LinearLayout linearLayout9 = (LinearLayout) findViewById(R.id.dimensional_layout_option);
        this.m_linearLDimensional = linearLayout9;
        linearLayout9.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) DimensionalActivity.class));
            }
        });
        LinearLayout linearLayout10 = (LinearLayout) findViewById(R.id.help_layout_option);
        this.m_linearLHelp = linearLayout10;
        linearLayout10.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) HelpActivity.class));
            }
        });
        LinearLayout linearLayout11 = (LinearLayout) findViewById(R.id.about_layout_option);
        this.aboutLayout = linearLayout11;
        linearLayout11.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) AboutActivity.class));
            }
        });
        LinearLayout linearLayout12 = (LinearLayout) findViewById(R.id.welcome_layout_option);
        this.welcomeLayout = linearLayout12;
        linearLayout12.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(OptionActivity.this, (Class<?>) GuideActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("activityType", 1);
                intent.putExtras(bundle2);
                OptionActivity.this.startActivity(intent);
            }
        });
        this.backBtn.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.onBackPressed();
            }
        });
        this.saveBtn.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String strRead;
                String strTrim = OptionActivity.this.m_editTName.getText().toString().trim();
                String strTrim2 = OptionActivity.this.m_editTGroup.getText().toString().trim();
                try {
                    strRead = DataConfig.getInstance(OptionActivity.this).Read(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (strTrim.length() > 0) {
                    if (!strTrim.equals(strRead)) {
                        DataConfig.getInstance(OptionActivity.this).Write(0, strTrim);
                        MediaRenderProxy.getInstance().restartEngine();
                    }
                    if (strTrim2.length() > 0) {
                        DataConfig.getInstance(OptionActivity.this).Write(1, strTrim2);
                        ArrayList<HostInformation> arrayList = new ArrayList<>(OptionActivity.this.m_myApp.ipmsgService.userList);
                        HashMap map = new HashMap();
                        if (OptionActivity.this.m_myApp.ipmsgService.fileMsgs.size() > 0) {
                            for (Map.Entry<String, ArrayList<MsgRecord>> entry : OptionActivity.this.m_myApp.ipmsgService.fileMsgs.entrySet()) {
                                Iterator<MsgRecord> it = entry.getValue().iterator();
                                while (true) {
                                    if (it.hasNext()) {
                                        if (it.next().getFileId() != -1) {
                                            map.put(entry.getKey(), "");
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                        int i = 0;
                        while (i < OptionActivity.this.m_myApp.ipmsgService.userList.size()) {
                            if (map.get(OptionActivity.this.m_myApp.ipmsgService.userList.get(i).strMacAddr) == null) {
                                OptionActivity.this.m_myApp.ipmsgService.userList.remove(i);
                                i--;
                            }
                            i++;
                        }
                        OptionActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.entryService(arrayList, true);
                        OptionActivity optionActivity = OptionActivity.this;
                        Public_Tools.showToast(optionActivity, optionActivity.getString(R.string.option_notify), 0);
                        OptionActivity.this.onBackPressed();
                        return;
                    }
                    Public_Tools.showToast(OptionActivity.this, OptionActivity.this.getString(R.string.group_prompt), 0);
                    return;
                }
                Public_Tools.showToast(OptionActivity.this, OptionActivity.this.getString(R.string.name_prompt), 0);
            }
        });
        this.m_editTName.setText(DataConfig.getInstance(this).Read(0));
        this.m_editTGroup.setText(DataConfig.getInstance(this).Read(1));
        String defaultLocalHostIP = Public_Tools.getDefaultLocalHostIP();
        this.m_editIpAddr.setText(defaultLocalHostIP);
        this.m_editTCurrentNetSector.setText(defaultLocalHostIP.substring(0, defaultLocalHostIP.lastIndexOf(".")));
        NetStatusBroadcast.ehList.add(this);
        LinearLayout linearLayout13 = (LinearLayout) findViewById(R.id.linearl_head);
        this.m_linearLHead = linearLayout13;
        linearLayout13.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.OptionActivity.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OptionActivity.this.startActivity(new Intent(OptionActivity.this, (Class<?>) HeadImageActivity.class));
            }
        });
        this.m_imageVHead = (ImageView) findViewById(R.id.imagev_headphoto);
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_imageVHead.setImageResource(this.m_myApp.g_headMap.get(DataConfig.getInstance(this).Read(36)).intValue());
        ((IpmsgApplication) getApplication()).currentActivity = this;
        if (this.m_myApp.g_bBackRuning) {
            this.m_myApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        String currNetName = Public_Tools.getCurrNetName();
        TextView textView = this.m_editTCurrentNetWork;
        if (currNetName == null) {
            currNetName = getString(R.string.no_net_connect);
        }
        textView.setText(currNetName);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        NetStatusBroadcast.ehList.remove(this);
        super.onDestroy();
    }

    @Override // com.netfeige.broadcast.NetStatusBroadcast.EventHandler
    public void wifiStatusNotification(boolean z) {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 0;
        messageObtain.arg1 = z ? 1 : 0;
        messageObtain.sendToTarget();
    }

    @Override // android.app.Activity
    protected void onStart() {
        String strRead = DataConfig.getInstance(this).Read(35);
        if (strRead.equals(ContentTree.ROOT_ID)) {
            this.m_textVFeigeDownload.setText("鎵嬫満鍐呭瓨");
        } else if (strRead.equals(ContentTree.VIDEO_ID)) {
            this.m_textVFeigeDownload.setText("SD卡");
        } else if (strRead.equals(ContentTree.AUDIO_ID)) {
            this.m_textVFeigeDownload.setText("SD卡");
        }
        if (this.m_myApp.g_bRemotePlayerStauts) {
            this.m_textVMediaRenderState.setText("寮€");
        } else {
            this.m_textVMediaRenderState.setText("关");
        }
        super.onStart();
    }
}

