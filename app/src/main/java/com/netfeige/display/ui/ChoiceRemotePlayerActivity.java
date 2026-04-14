package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.broadcast.WTBroadcast;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.ChoiceRemotePlayerAdpter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.dlna.DlnaHelper;
import com.netfeige.dlna.MediaServer;
import java.util.Timer;
import java.util.TimerTask;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceType;

/* JADX INFO: loaded from: classes.dex */
public class ChoiceRemotePlayerActivity extends Activity implements WTBroadcast.EventHandler {
    public static Handler g_handler = new Handler() { // from class: com.netfeige.display.ui.ChoiceRemotePlayerActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                if (IpmsgApplication.g_arrayLDevices.size() > 0) {
                    if (ChoiceRemotePlayerActivity.m_textVShowNotify.isShown() && !ChoiceRemotePlayerActivity.m_bSearching) {
                        ChoiceRemotePlayerActivity.m_textVShowNotify.setVisibility(8);
                    }
                } else if (!ChoiceRemotePlayerActivity.m_textVShowNotify.isShown()) {
                    ChoiceRemotePlayerActivity.m_textVShowNotify.setVisibility(0);
                    ChoiceRemotePlayerActivity.m_textVShowNotify.setText(R.string.nodevicenotify);
                    ChoiceRemotePlayerActivity.m_textVShowNotify.setGravity(16);
                }
                ChoiceRemotePlayerActivity.m_choiceRemotePlayerAdpter.notifyDataSetChanged();
                return;
            }
            if (i != 1) {
                return;
            }
            boolean unused = ChoiceRemotePlayerActivity.m_bSearching = false;
            ChoiceRemotePlayerActivity.m_btnSearch.setEnabled(true);
            if (IpmsgApplication.g_arrayLDevices != null) {
                if (IpmsgApplication.g_arrayLDevices.size() > 0) {
                    ChoiceRemotePlayerActivity.m_textVShowNotify.setVisibility(8);
                    return;
                }
                ChoiceRemotePlayerActivity.m_textVShowNotify.setVisibility(0);
                ChoiceRemotePlayerActivity.m_textVShowNotify.setText(R.string.nodevicenotify);
                ChoiceRemotePlayerActivity.m_textVShowNotify.setGravity(16);
            }
        }
    };
    public static Service g_serviceAVTransport = null;
    private static boolean m_bSearching = false;
    private static Button m_btnSearch;
    private static ChoiceRemotePlayerAdpter m_choiceRemotePlayerAdpter;
    private static ListView m_listVRemotePlay;
    private static TextView m_textVShowNotify;
    private Button m_btnBack;
    private IpmsgApplication m_ipmsgApp;
    private String m_strFilePath;
    private MediaServer mediaServer;

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void handleConnectChange() {
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void scanResultsAvailable() {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.choiceremoteplayerlist);
        this.m_strFilePath = getIntent().getExtras().getString("filePath");
        WTBroadcast.ehList.add(this);
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        initControl();
        if (this.mediaServer != null || Public_Tools.getLocalIpAddress() == null) {
            return;
        }
        try {
            this.mediaServer = new MediaServer(Public_Tools.getLocalIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_ipmsgApp.currentActivity = this;
        Global.g_bInChoiceRemote = true;
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.btn_back);
        this.m_btnBack = button;
        button.setOnClickListener(new BtnBackOnClickListener());
        Button button2 = (Button) findViewById(R.id.btn_search);
        m_btnSearch = button2;
        button2.setOnClickListener(new SearchOnClickListener());
        m_listVRemotePlay = (ListView) findViewById(R.id.listv_choiceremoteplayer);
        ChoiceRemotePlayerAdpter choiceRemotePlayerAdpter = new ChoiceRemotePlayerAdpter(this, R.layout.choiceremoteplayeritem, IpmsgApplication.g_arrayLDevices);
        m_choiceRemotePlayerAdpter = choiceRemotePlayerAdpter;
        m_listVRemotePlay.setAdapter((ListAdapter) choiceRemotePlayerAdpter);
        m_listVRemotePlay.setOnItemClickListener(new ListVRemotePlayerItemClickListener());
        m_textVShowNotify = (TextView) findViewById(R.id.textv_showcontect);
        if (IpmsgApplication.g_arrayLDevices.size() > 0) {
            m_textVShowNotify.setVisibility(8);
            return;
        }
        m_textVShowNotify.setVisibility(0);
        m_textVShowNotify.setText(R.string.nodevicenotify);
        m_textVShowNotify.setGravity(16);
    }

    private class SearchOnClickListener implements View.OnClickListener {
        private SearchOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ChoiceRemotePlayerActivity.this.m_ipmsgApp.getAndroidUpnpService() == null || ChoiceRemotePlayerActivity.m_bSearching) {
                return;
            }
            boolean unused = ChoiceRemotePlayerActivity.m_bSearching = true;
            IpmsgApplication.g_arrayLDevices.clear();
            ChoiceRemotePlayerActivity.m_choiceRemotePlayerAdpter.notifyDataSetChanged();
            ChoiceRemotePlayerActivity.this.m_ipmsgApp.getAndroidUpnpService().getRegistry().removeAllRemoteDevices();
            ChoiceRemotePlayerActivity.this.m_ipmsgApp.getAndroidUpnpService().getControlPoint().search(20);
            new Timer(true).schedule(new TimerTask() { // from class: com.netfeige.display.ui.ChoiceRemotePlayerActivity.SearchOnClickListener.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ChoiceRemotePlayerActivity.g_handler.sendEmptyMessage(1);
                }
            }, 20000L);
            ChoiceRemotePlayerActivity.m_textVShowNotify.setVisibility(0);
            ChoiceRemotePlayerActivity.m_textVShowNotify.setText(R.string.dpsearch);
            ChoiceRemotePlayerActivity.m_textVShowNotify.setGravity(17);
            ChoiceRemotePlayerActivity.m_btnSearch.setEnabled(false);
        }
    }

    private class ListVRemotePlayerItemClickListener implements AdapterView.OnItemClickListener {
        private ListVRemotePlayerItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            ChoiceRemotePlayerActivity.g_serviceAVTransport = IpmsgApplication.g_arrayLDevices.get(i).findService(new UDAServiceType("AVTransport"));
            if (ChoiceRemotePlayerActivity.g_serviceAVTransport != null) {
                if (!Public_Tools.isImageFile(ChoiceRemotePlayerActivity.this.m_strFilePath)) {
                    if (Public_Tools.isAudioFile(ChoiceRemotePlayerActivity.this.m_strFilePath)) {
                        if (DlnaHelper.getHttpUri(ChoiceRemotePlayerActivity.this.m_strFilePath) == null || ChoiceRemotePlayerActivity.g_serviceAVTransport == null) {
                            return;
                        }
                        Intent intent = new Intent(ChoiceRemotePlayerActivity.this, (Class<?>) AudioControlActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("filePath", ChoiceRemotePlayerActivity.this.m_strFilePath);
                        intent.putExtras(bundle);
                        ChoiceRemotePlayerActivity.this.startActivity(intent);
                        return;
                    }
                    if (!Public_Tools.isVideoFile(ChoiceRemotePlayerActivity.this.m_strFilePath) || DlnaHelper.getHttpUri(ChoiceRemotePlayerActivity.this.m_strFilePath) == null || ChoiceRemotePlayerActivity.g_serviceAVTransport == null) {
                        return;
                    }
                    Intent intent2 = new Intent(ChoiceRemotePlayerActivity.this, (Class<?>) VideoControlActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("filePath", ChoiceRemotePlayerActivity.this.m_strFilePath);
                    intent2.putExtras(bundle2);
                    ChoiceRemotePlayerActivity.this.startActivity(intent2);
                    return;
                }
                Intent intent3 = new Intent(ChoiceRemotePlayerActivity.this, (Class<?>) ImagePreviewActivity.class);
                Bundle bundle3 = new Bundle();
                bundle3.putString("filePath", ChoiceRemotePlayerActivity.this.m_strFilePath);
                bundle3.putBoolean("AVTransport", true);
                intent3.putExtras(bundle3);
                ChoiceRemotePlayerActivity.this.startActivity(intent3);
                Global.g_bInImageFromRemote = false;
                return;
            }
            System.out.println("service 为空");
        }
    }

    private class BtnBackOnClickListener implements View.OnClickListener {
        private BtnBackOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ChoiceRemotePlayerActivity.this.mediaServer != null) {
                ChoiceRemotePlayerActivity.this.mediaServer.getHttpServer().stop();
                ChoiceRemotePlayerActivity.this.mediaServer = null;
            }
            ChoiceRemotePlayerActivity.this.onBackPressed();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        MediaServer mediaServer = this.mediaServer;
        if (mediaServer != null) {
            mediaServer.getHttpServer().stop();
            this.mediaServer = null;
        }
        if (g_serviceAVTransport != null) {
            g_serviceAVTransport = null;
        }
        super.onDestroy();
    }

    @Override // com.netfeige.broadcast.WTBroadcast.EventHandler
    public void wifiStatusNotification(Intent intent) {
        if (this.mediaServer != null || Public_Tools.getLocalIpAddress() == null) {
            return;
        }
        try {
            this.mediaServer = new MediaServer(Public_Tools.getLocalIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        MediaServer mediaServer;
        if (keyEvent.getKeyCode() == 4 && (mediaServer = this.mediaServer) != null) {
            mediaServer.getHttpServer().stop();
            this.mediaServer = null;
        }
        return super.onKeyDown(i, keyEvent);
    }
}

