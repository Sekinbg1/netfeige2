package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.broadcast.NetStatusBroadcast;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.data.NetSectorAdapter;
import com.netfeige.display.ui.dialog.AddNetSectorDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class NetSectorActivity extends Activity implements NetStatusBroadcast.EventHandler {
    private Button m_btnAddSector;
    private Button m_btnBack;
    private TextView m_editTCurrentNetSector;
    private ListView m_listVNetSectors;
    private IpmsgApplication m_myApp;
    private final int m_nWifiCategory = 0;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.NetSectorActivity.3
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                String defaultLocalHostIP = Public_Tools.getDefaultLocalHostIP();
                NetSectorActivity.this.m_editTCurrentNetSector.setText(defaultLocalHostIP.substring(0, defaultLocalHostIP.lastIndexOf(".")));
            }
            super.handleMessage(message);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.net_sectors);
        initBasic();
        initControl();
        initData();
    }

    private void initBasic() {
        this.m_myApp = (IpmsgApplication) getApplication();
    }

    private void initControl() {
        this.m_editTCurrentNetSector = (TextView) findViewById(R.id.current_netsector_text_net_sector);
        Button button = (Button) findViewById(R.id.back_btn_net_sector);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.NetSectorActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i = 0;
                String str = "";
                for (int i2 = 0; i2 < NetSectorActivity.this.m_myApp.g_listNetSectors.size(); i2++) {
                    str = str + NetSectorActivity.this.m_myApp.g_listNetSectors.get(i2);
                    if (i2 < NetSectorActivity.this.m_myApp.g_listNetSectors.size() - 1) {
                        str = str + ",";
                    }
                }
                if (!str.equals(DataConfig.getInstance(NetSectorActivity.this.getApplicationContext()).Read(37))) {
                    DataConfig.getInstance(NetSectorActivity.this.getApplicationContext()).Write(37, str);
                    ArrayList<HostInformation> arrayList = new ArrayList<>(NetSectorActivity.this.m_myApp.ipmsgService.userList);
                    HashMap map = new HashMap();
                    if (NetSectorActivity.this.m_myApp.ipmsgService.fileMsgs.size() > 0) {
                        for (Map.Entry<String, ArrayList<MsgRecord>> entry : NetSectorActivity.this.m_myApp.ipmsgService.fileMsgs.entrySet()) {
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
                    while (i < NetSectorActivity.this.m_myApp.ipmsgService.userList.size()) {
                        if (map.get(NetSectorActivity.this.m_myApp.ipmsgService.userList.get(i).strMacAddr) == null) {
                            NetSectorActivity.this.m_myApp.ipmsgService.userList.remove(i);
                            i--;
                        }
                        i++;
                    }
                    NetSectorActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.entryService(arrayList, true);
                }
                NetSectorActivity.this.onBackPressed();
            }
        });
        Button button2 = (Button) findViewById(R.id.add_btn_net_sector);
        this.m_btnAddSector = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.NetSectorActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AddNetSectorDialog(NetSectorActivity.this).show();
            }
        });
        this.m_listVNetSectors = (ListView) findViewById(R.id.netsectors_list_net_sector);
    }

    private void initData() {
        String defaultLocalHostIP = Public_Tools.getDefaultLocalHostIP();
        this.m_editTCurrentNetSector.setText(defaultLocalHostIP.substring(0, defaultLocalHostIP.lastIndexOf(".")));
        this.m_listVNetSectors.setAdapter((ListAdapter) new NetSectorAdapter(this, R.layout.netsectoritem, R.id.name_text_netsectoritem, this.m_myApp.g_listNetSectors));
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_myApp.currentActivity = this;
        ((NetSectorAdapter) this.m_listVNetSectors.getAdapter()).notifyDataSetChanged();
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

    public void notifyDataSetChanged() {
        ((NetSectorAdapter) this.m_listVNetSectors.getAdapter()).notifyDataSetChanged();
    }

    @Override // com.netfeige.broadcast.NetStatusBroadcast.EventHandler
    public void wifiStatusNotification(boolean z) {
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 0;
        messageObtain.arg1 = z ? 1 : 0;
        messageObtain.sendToTarget();
    }
}

