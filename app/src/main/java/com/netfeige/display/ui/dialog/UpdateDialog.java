package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class UpdateDialog extends Dialog {
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private CheckBox m_checkBNotNotify;
    private Context m_context;
    private IpmsgApplication m_ipmsgApp;
    private TextView m_textVContent;

    public UpdateDialog(Context context, IpmsgApplication ipmsgApplication) {
        super(context, R.style.sort_dialog);
        setContentView(R.layout.updatedialog);
        this.m_context = context;
        this.m_ipmsgApp = ipmsgApplication;
        initControl();
    }

    private void initControl() {
        this.m_textVContent = (TextView) findViewById(R.id.content_text_update);
        Button button = (Button) findViewById(R.id.confirm_btn_update);
        this.m_btnConfirm = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.UpdateDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(UpdateDialog.this.m_ipmsgApp.updatePath));
                intent.setFlags(268435456);
                UpdateDialog.this.getContext().startActivity(intent);
                UpdateDialog.this.dismiss();
            }
        });
        Button button2 = (Button) findViewById(R.id.cancel_btn_update);
        this.m_btnCancel = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.UpdateDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateDialog.this.dismiss();
            }
        });
        this.m_checkBNotNotify = (CheckBox) findViewById(R.id.checkbox_notnotify);
        if (ContentTree.ROOT_ID.equals(this.m_ipmsgApp.g_strCheckUpdate)) {
            this.m_checkBNotNotify.setChecked(true);
        } else {
            this.m_checkBNotNotify.setChecked(false);
        }
        this.m_checkBNotNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.netfeige.display.ui.dialog.UpdateDialog.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    UpdateDialog.this.m_ipmsgApp.g_strCheckUpdate = ContentTree.ROOT_ID;
                } else {
                    UpdateDialog.this.m_ipmsgApp.g_strCheckUpdate = ContentTree.VIDEO_ID;
                }
                DataConfig.getInstance(UpdateDialog.this.m_context.getApplicationContext()).Write(3, UpdateDialog.this.m_ipmsgApp.g_strCheckUpdate);
            }
        });
    }

    public TextView getTextVContent() {
        return this.m_textVContent;
    }
}

