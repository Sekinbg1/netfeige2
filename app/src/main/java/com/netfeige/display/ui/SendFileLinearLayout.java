package com.netfeige.display.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.Global;

/* JADX INFO: loaded from: classes.dex */
public class SendFileLinearLayout extends LinearLayout {
    private Button m_btnApk;
    private Button m_btnAudio;
    private Button m_btnDocument;
    private Button m_btnFeige;
    private Button m_btnImage;
    private Button m_btnMomery;
    private Button m_btnSdCard;
    private Button m_btnVideo;
    private Context m_context;

    public SendFileLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.m_context = context;
        LayoutInflater.from(context).inflate(R.layout.cataloguefileitem, (ViewGroup) this, true);
        initControl();
    }

    private void initControl() {
        BtnOnClincklistener btnOnClincklistener = new BtnOnClincklistener();
        Button button = (Button) findViewById(R.id.btn_filer_img);
        this.m_btnImage = button;
        button.setOnClickListener(btnOnClincklistener);
        Button button2 = (Button) findViewById(R.id.btn_filer_audio);
        this.m_btnAudio = button2;
        button2.setOnClickListener(btnOnClincklistener);
        Button button3 = (Button) findViewById(R.id.btn_filer_video);
        this.m_btnVideo = button3;
        button3.setOnClickListener(btnOnClincklistener);
        Button button4 = (Button) findViewById(R.id.btn_filer_document);
        this.m_btnDocument = button4;
        button4.setOnClickListener(btnOnClincklistener);
        Button button5 = (Button) findViewById(R.id.btn_filer_apk);
        this.m_btnApk = button5;
        button5.setOnClickListener(btnOnClincklistener);
        Button button6 = (Button) findViewById(R.id.btn_filer_memory);
        this.m_btnFeige = button6;
        button6.setOnClickListener(btnOnClincklistener);
        Button button7 = (Button) findViewById(R.id.btn_filer_feige);
        this.m_btnMomery = button7;
        button7.setOnClickListener(btnOnClincklistener);
        Button button8 = (Button) findViewById(R.id.btn_filer_sdcard);
        this.m_btnSdCard = button8;
        button8.setOnClickListener(btnOnClincklistener);
    }

    private class BtnOnClincklistener implements View.OnClickListener {
        private BtnOnClincklistener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (((Button) view).getId()) {
                case R.id.btn_filer_apk /* 2131165267 */:
                    SendFileLinearLayout.this.skip(Category.apk, ChatActivity.ip);
                    break;
                case R.id.btn_filer_audio /* 2131165268 */:
                    SendFileLinearLayout.this.skip(Category.audio, ChatActivity.ip);
                    break;
                case R.id.btn_filer_document /* 2131165269 */:
                    SendFileLinearLayout.this.skip(Category.document, ChatActivity.ip);
                    break;
                case R.id.btn_filer_feige /* 2131165270 */:
                    SendFileLinearLayout.this.skip(Category.feige, ChatActivity.ip);
                    break;
                case R.id.btn_filer_img /* 2131165271 */:
                    SendFileLinearLayout.this.skip(Category.image, ChatActivity.ip);
                    break;
                case R.id.btn_filer_memory /* 2131165272 */:
                    SendFileLinearLayout.this.skip(Category.memory, ChatActivity.ip);
                    break;
                case R.id.btn_filer_sdcard /* 2131165275 */:
                    SendFileLinearLayout.this.skip(Category.sdcard, ChatActivity.ip);
                    break;
                case R.id.btn_filer_video /* 2131165276 */:
                    SendFileLinearLayout.this.skip(Category.video, ChatActivity.ip);
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void skip(Category category, String str) {
        FileActivity.s_whatFolder = category;
        Intent intent = new Intent(this.m_context, (Class<?>) FileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ip", str);
        intent.putExtras(bundle);
        this.m_context.startActivity(intent);
        Global.g_isInFileActivity = true;
    }
}

