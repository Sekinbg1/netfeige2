package com.geniusgithub.mediarender;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.geniusgithub.mediarender.DeviceUpdateBrocastFactory;
import com.geniusgithub.mediarender.center.MediaRenderProxy;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.R;
import com.netfeige.display.data.IpmsgApplication;

/* JADX INFO: loaded from: classes.dex */
public class MediaRenderActivity extends Activity implements View.OnClickListener, DeviceUpdateBrocastFactory.IDevUpdateListener {
    private static final CommonLog log = LogFactory.createLog();
    private IpmsgApplication mApplication;
    private DeviceUpdateBrocastFactory mBrocastFactory;
    private MediaRenderProxy mRenderProxy;
    private Button m_btnBack;
    private ImageView m_imageVStartOrStop;
    private TextView m_textVMediaRenderName;

    @Override // com.geniusgithub.mediarender.DeviceUpdateBrocastFactory.IDevUpdateListener
    public void onUpdate() {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.newmediarender);
        this.mApplication = IpmsgApplication.getInstance();
        setupView();
        initData();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        unInitData();
        super.onDestroy();
    }

    private void setupView() {
        Button button = (Button) findViewById(R.id.back_btn_mediarender);
        this.m_btnBack = button;
        button.setOnClickListener(this);
        this.m_textVMediaRenderName = (TextView) findViewById(R.id.textv_mediarendername);
        this.m_imageVStartOrStop = (ImageView) findViewById(R.id.imagev_playerservice);
        if (this.mApplication.g_bRemotePlayerStauts) {
            this.m_imageVStartOrStop.setImageResource(R.drawable.btn_open);
        } else {
            this.m_imageVStartOrStop.setImageResource(R.drawable.btn_close);
        }
        try {
            if (Integer.parseInt(Build.VERSION.SDK) < 9) {
                this.m_imageVStartOrStop.setEnabled(false);
            } else {
                this.m_imageVStartOrStop.setOnClickListener(new View.OnClickListener() { // from class: com.geniusgithub.mediarender.MediaRenderActivity.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (MediaRenderActivity.this.mApplication.g_bRemotePlayerStauts) {
                            MediaRenderActivity.this.mApplication.g_bRemotePlayerStauts = false;
                            MediaRenderActivity.this.m_imageVStartOrStop.setImageResource(R.drawable.btn_close);
                            MediaRenderActivity.this.stop();
                        } else {
                            MediaRenderActivity.this.mApplication.g_bRemotePlayerStauts = true;
                            MediaRenderActivity.this.m_imageVStartOrStop.setImageResource(R.drawable.btn_open);
                            MediaRenderActivity.this.start();
                        }
                    }
                });
            }
        } catch (Exception unused) {
            this.m_imageVStartOrStop.setEnabled(false);
        }
    }

    private void initData() {
        this.mRenderProxy = MediaRenderProxy.getInstance();
        this.mBrocastFactory = new DeviceUpdateBrocastFactory(this);
        this.m_textVMediaRenderName.setText(DlnaUtils.getDevName(this));
        this.mBrocastFactory.register(this);
    }

    private void unInitData() {
        this.mBrocastFactory.unregister();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.back_btn_mediarender) {
            return;
        }
        back();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void start() {
        this.mRenderProxy.startEngine();
    }

    private void back() {
        onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stop() {
        this.mRenderProxy.stopEngine();
    }
}

