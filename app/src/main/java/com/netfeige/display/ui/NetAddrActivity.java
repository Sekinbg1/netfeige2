package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.broadcast.NetStatusBroadcast;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class NetAddrActivity extends Activity implements NetStatusBroadcast.EventHandler {
    private Button m_btnBack;
    private TextView m_editIpAddr;
    private TextView m_editMacAddr;
    public IpmsgApplication m_myApp;
    private final int m_nWifiCategory = 0;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.NetAddrActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                NetAddrActivity.this.m_editIpAddr.setText(Public_Tools.getDefaultLocalHostIP());
            }
            super.handleMessage(message);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.net_address);
        Button button = (Button) findViewById(R.id.back_btn_net);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.NetAddrActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NetAddrActivity.this.onBackPressed();
            }
        });
        TextView textView = (TextView) findViewById(R.id.ip_addr_edit_option);
        this.m_editIpAddr = textView;
        textView.setText(Public_Tools.getDefaultLocalHostIP());
        TextView textView2 = (TextView) findViewById(R.id.mac_addr_edit_option);
        this.m_editMacAddr = textView2;
        textView2.setText(Public_Tools.getLocalMacAddress());
        NetStatusBroadcast.ehList.add(this);
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
}

