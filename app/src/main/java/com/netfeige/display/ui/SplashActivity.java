package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.netfeige.R;
import com.netfeige.common.Global;

/* JADX INFO: loaded from: classes.dex */
public class SplashActivity extends Activity {
    private static final int GO_GUIDE = 1001;
    private static final int GO_HOME = 1000;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    private static final long SPLASH_DELAY_MILLIS = 1000;
    private boolean m_bIsFirstIn = false;
    private Handler m_handler = new Handler() { // from class: com.netfeige.display.ui.SplashActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1000) {
                SplashActivity.this.goHome();
            } else if (i == 1001) {
                SplashActivity.this.goGuide();
            }
            super.handleMessage(message);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash);
        init();
    }

    private void init() {
        boolean z = getSharedPreferences(SHAREDPREFERENCES_NAME, 0).getBoolean("isFirstIn", true);
        this.m_bIsFirstIn = z;
        if (!z) {
            this.m_handler.sendEmptyMessageDelayed(1000, SPLASH_DELAY_MILLIS);
        } else {
            this.m_handler.sendEmptyMessageDelayed(1001, SPLASH_DELAY_MILLIS);
        }
        if (Global.g_filePath != null) {
            Global.g_filePath.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goHome() {
        startActivity(new Intent(this, (Class<?>) IpmsgActivity.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goGuide() {
        Intent intent = new Intent(this, (Class<?>) GuideActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("activityType", 0);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}

