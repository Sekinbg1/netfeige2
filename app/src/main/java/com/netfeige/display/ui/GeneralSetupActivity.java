package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class GeneralSetupActivity extends Activity {
    private Button m_btnBack;
    private ImageView m_imageVAutoRecvFile;
    private ImageView m_imageVCheckUpdate;
    private ImageView m_imageVDelFilePrompt;
    private ImageView m_imageVMsgNotification;
    private ImageView m_imageVPromptAudio;
    private ImageView m_imageVSendAudio;
    public IpmsgApplication m_myApp;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.general_setup);
        Button button = (Button) findViewById(R.id.back_btn_general);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                GeneralSetupActivity.this.onBackPressed();
            }
        });
        this.m_imageVMsgNotification = (ImageView) findViewById(R.id.msg_notification_image_general);
        if (this.m_myApp.g_strMsgNotification.equals(ContentTree.ROOT_ID)) {
            this.m_imageVMsgNotification.setImageResource(R.drawable.btn_close);
        } else {
            this.m_imageVMsgNotification.setImageResource(R.drawable.btn_open);
        }
        this.m_imageVMsgNotification.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GeneralSetupActivity.this.m_myApp.g_strMsgNotification.equals(ContentTree.ROOT_ID)) {
                    GeneralSetupActivity.this.m_myApp.g_strMsgNotification = ContentTree.VIDEO_ID;
                    GeneralSetupActivity.this.m_imageVMsgNotification.setImageResource(R.drawable.btn_open);
                } else {
                    GeneralSetupActivity.this.m_myApp.g_strMsgNotification = ContentTree.ROOT_ID;
                    GeneralSetupActivity.this.m_imageVMsgNotification.setImageResource(R.drawable.btn_close);
                }
                DataConfig.getInstance(GeneralSetupActivity.this).Write(6, GeneralSetupActivity.this.m_myApp.g_strMsgNotification);
            }
        });
        this.m_imageVPromptAudio = (ImageView) findViewById(R.id.prompt_audio_image_general);
        if (this.m_myApp.g_strPromptAudio.equals(ContentTree.ROOT_ID)) {
            this.m_imageVPromptAudio.setImageResource(R.drawable.btn_close);
        } else {
            this.m_imageVPromptAudio.setImageResource(R.drawable.btn_open);
        }
        this.m_imageVPromptAudio.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GeneralSetupActivity.this.m_myApp.g_strPromptAudio.equals(ContentTree.ROOT_ID)) {
                    GeneralSetupActivity.this.m_myApp.g_strPromptAudio = ContentTree.VIDEO_ID;
                    GeneralSetupActivity.this.m_imageVPromptAudio.setImageResource(R.drawable.btn_open);
                } else {
                    GeneralSetupActivity.this.m_myApp.g_strPromptAudio = ContentTree.ROOT_ID;
                    GeneralSetupActivity.this.m_imageVPromptAudio.setImageResource(R.drawable.btn_close);
                }
                DataConfig.getInstance(GeneralSetupActivity.this).Write(5, GeneralSetupActivity.this.m_myApp.g_strPromptAudio);
            }
        });
        this.m_imageVSendAudio = (ImageView) findViewById(R.id.send_audio_image_general);
        if (this.m_myApp.g_strSendAudio.equals(ContentTree.ROOT_ID)) {
            this.m_imageVSendAudio.setImageResource(R.drawable.btn_close);
        } else {
            this.m_imageVSendAudio.setImageResource(R.drawable.btn_open);
        }
        this.m_imageVSendAudio.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GeneralSetupActivity.this.m_myApp.g_strSendAudio.equals(ContentTree.ROOT_ID)) {
                    GeneralSetupActivity.this.m_myApp.g_strSendAudio = ContentTree.VIDEO_ID;
                    GeneralSetupActivity.this.m_imageVSendAudio.setImageResource(R.drawable.btn_open);
                } else {
                    GeneralSetupActivity.this.m_myApp.g_strSendAudio = ContentTree.ROOT_ID;
                    GeneralSetupActivity.this.m_imageVSendAudio.setImageResource(R.drawable.btn_close);
                }
                DataConfig.getInstance(GeneralSetupActivity.this).Write(4, GeneralSetupActivity.this.m_myApp.g_strSendAudio);
            }
        });
        this.m_imageVAutoRecvFile = (ImageView) findViewById(R.id.auto_recv_file_image_general);
        if (this.m_myApp.g_strAutoRecvFile.equals(ContentTree.ROOT_ID)) {
            this.m_imageVAutoRecvFile.setImageResource(R.drawable.btn_close);
        } else {
            this.m_imageVAutoRecvFile.setImageResource(R.drawable.btn_open);
        }
        this.m_imageVAutoRecvFile.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GeneralSetupActivity.this.m_myApp.g_strAutoRecvFile.equals(ContentTree.ROOT_ID)) {
                    GeneralSetupActivity.this.m_myApp.g_strAutoRecvFile = ContentTree.VIDEO_ID;
                    GeneralSetupActivity.this.m_imageVAutoRecvFile.setImageResource(R.drawable.btn_open);
                } else {
                    GeneralSetupActivity.this.m_myApp.g_strAutoRecvFile = ContentTree.ROOT_ID;
                    GeneralSetupActivity.this.m_imageVAutoRecvFile.setImageResource(R.drawable.btn_close);
                }
                DataConfig.getInstance(GeneralSetupActivity.this).Write(2, GeneralSetupActivity.this.m_myApp.g_strAutoRecvFile);
            }
        });
        this.m_imageVDelFilePrompt = (ImageView) findViewById(R.id.del_file_prompt_image_general);
        if (this.m_myApp.g_strDelFilePrompt.equals(ContentTree.ROOT_ID)) {
            this.m_imageVDelFilePrompt.setImageResource(R.drawable.btn_close);
        } else {
            this.m_imageVDelFilePrompt.setImageResource(R.drawable.btn_open);
        }
        this.m_imageVDelFilePrompt.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GeneralSetupActivity.this.m_myApp.g_strDelFilePrompt.equals(ContentTree.ROOT_ID)) {
                    GeneralSetupActivity.this.m_myApp.g_strDelFilePrompt = ContentTree.VIDEO_ID;
                    GeneralSetupActivity.this.m_imageVDelFilePrompt.setImageResource(R.drawable.btn_open);
                } else {
                    GeneralSetupActivity.this.m_myApp.g_strDelFilePrompt = ContentTree.ROOT_ID;
                    GeneralSetupActivity.this.m_imageVDelFilePrompt.setImageResource(R.drawable.btn_close);
                }
                DataConfig.getInstance(GeneralSetupActivity.this).Write(7, GeneralSetupActivity.this.m_myApp.g_strDelFilePrompt);
            }
        });
        this.m_imageVCheckUpdate = (ImageView) findViewById(R.id.check_update_image_general);
        if (this.m_myApp.g_strCheckUpdate.equals(ContentTree.ROOT_ID)) {
            this.m_imageVCheckUpdate.setImageResource(R.drawable.btn_close);
        } else {
            this.m_imageVCheckUpdate.setImageResource(R.drawable.btn_open);
        }
        this.m_imageVCheckUpdate.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.GeneralSetupActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GeneralSetupActivity.this.m_myApp.g_strCheckUpdate.equals(ContentTree.ROOT_ID)) {
                    GeneralSetupActivity.this.m_myApp.g_strCheckUpdate = ContentTree.VIDEO_ID;
                    GeneralSetupActivity.this.m_imageVCheckUpdate.setImageResource(R.drawable.btn_open);
                } else {
                    GeneralSetupActivity.this.m_myApp.g_strCheckUpdate = ContentTree.ROOT_ID;
                    GeneralSetupActivity.this.m_imageVCheckUpdate.setImageResource(R.drawable.btn_close);
                }
                DataConfig.getInstance(GeneralSetupActivity.this).Write(3, GeneralSetupActivity.this.m_myApp.g_strCheckUpdate);
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

