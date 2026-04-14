package com.netfeige.display.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.dialog.UpdateDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class AboutActivity extends Activity {
    private static final int m_nUpdateDialog = 2;
    private Button m_btnBack;
    private IpmsgApplication m_myApp;
    private TextView m_textVCheckUpdate;
    private TextView m_textVVersionCode;
    private UpdateDialog m_updateDialog;

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int i) {
        if (i != 2) {
            return null;
        }
        return this.m_updateDialog;
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int i, Dialog dialog) {
        if (i == 2) {
            TextView textVContent = this.m_updateDialog.getTextVContent();
            StringBuilder sb = new StringBuilder();
            sb.append((Object) Html.fromHtml(getString(R.string.version_code_colon) + this.m_myApp.newVersion + "<br/>" + getString(R.string.new_version_changed_colon)));
            sb.append(this.m_myApp.newVersionChanged);
            textVContent.setText(sb.toString());
        }
        super.onPrepareDialog(i, dialog);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.about);
        Button button = (Button) findViewById(R.id.back_btn_about);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.AboutActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AboutActivity.this.onBackPressed();
            }
        });
        TextView textView = (TextView) findViewById(R.id.version_code_text_about);
        this.m_textVVersionCode = textView;
        textView.setText(Public_Tools.getVersion());
        TextView textView2 = (TextView) findViewById(R.id.check_update_text_about);
        this.m_textVCheckUpdate = textView2;
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.AboutActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i = AboutActivity.this.m_myApp.updateStatus;
                if (i == 0) {
                    Public_Tools.showToast(AboutActivity.this, AboutActivity.this.getString(R.string.no_need_update) + Public_Tools.getVersion(), 1);
                    return;
                }
                if (i != 1) {
                    if (i != 2) {
                        return;
                    }
                } else if (AboutActivity.this.m_myApp.g_strCheckUpdate.equals(ContentTree.ROOT_ID)) {
                    return;
                } else {
                    AboutActivity.this.showDialog(2);
                }
                AboutActivity.this.showDialog(2);
            }
        });
        this.m_updateDialog = new UpdateDialog(this, this.m_myApp);
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
}

