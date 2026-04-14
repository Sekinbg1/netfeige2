package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes.dex */
public class FeedbackActivity extends Activity {
    private Button m_btnBack;
    private Button m_btnSend;
    private Context m_context;
    private EditText m_editTContent;
    private InputMethodManager m_inputManager = null;
    public IpmsgApplication m_myApp;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m_context = this;
        requestWindowFeature(1);
        this.m_myApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.feedback);
        Button button = (Button) findViewById(R.id.back_btn_feedback);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.FeedbackActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FeedbackActivity.this.m_inputManager.isActive()) {
                    FeedbackActivity.this.m_inputManager.hideSoftInputFromWindow(((Activity) FeedbackActivity.this.m_context).getCurrentFocus().getWindowToken(), 0);
                }
                FeedbackActivity.this.onBackPressed();
            }
        });
        EditText editText = (EditText) findViewById(R.id.content_edit_feedback);
        this.m_editTContent = editText;
        this.m_inputManager = (InputMethodManager) editText.getContext().getSystemService("input_method");
        Button button2 = (Button) findViewById(R.id.send_feedback);
        this.m_btnSend = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.FeedbackActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String string = FeedbackActivity.this.m_editTContent.getText().toString();
                if (string.length() != 0) {
                    FeedbackActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendFeedback(string);
                    FeedbackActivity feedbackActivity = FeedbackActivity.this;
                    Public_Tools.showToast(feedbackActivity, feedbackActivity.getResources().getString(R.string.feedback_success_prompt), 0);
                } else {
                    FeedbackActivity feedbackActivity2 = FeedbackActivity.this;
                    Public_Tools.showToast(feedbackActivity2, feedbackActivity2.getResources().getString(R.string.send_feedback_prompt), 0);
                }
                FeedbackActivity.this.m_editTContent.setText("");
                FeedbackActivity.this.m_editTContent.clearFocus();
            }
        });
    }

    @Override // android.app.Activity
    protected void onStart() {
        this.m_editTContent.setFocusable(true);
        this.m_editTContent.setFocusableInTouchMode(true);
        this.m_editTContent.requestFocus();
        new Timer().schedule(new TimerTask() { // from class: com.netfeige.display.ui.FeedbackActivity.3
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                FeedbackActivity.this.m_inputManager.showSoftInput(FeedbackActivity.this.m_editTContent, 0);
            }
        }, 588L);
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }
}

