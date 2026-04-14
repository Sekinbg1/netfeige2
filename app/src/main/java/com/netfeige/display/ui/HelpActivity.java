package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class HelpActivity extends Activity {
    private Button m_btnBack;
    public IpmsgApplication m_myApp;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.help);
        Button button = (Button) findViewById(R.id.back_btn_help);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.HelpActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HelpActivity.this.onBackPressed();
            }
        });
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
}

