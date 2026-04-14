package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;

/* JADX INFO: loaded from: classes.dex */
public class ExitDialog extends Dialog {
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private Context m_context;
    private IpmsgApplication m_ipmsgApp;

    public ExitDialog(Context context, IpmsgApplication ipmsgApplication) {
        super(context, R.style.sort_dialog);
        setContentView(R.layout.exitdialog);
        this.m_context = context;
        this.m_ipmsgApp = ipmsgApplication;
        initControl();
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.confirm_btn_update);
        this.m_btnConfirm = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.ExitDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Public_Tools.exitApp(ExitDialog.this.m_ipmsgApp);
                ExitDialog.this.dismiss();
            }
        });
        Button button2 = (Button) findViewById(R.id.cancel_btn_update);
        this.m_btnCancel = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.ExitDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ExitDialog.this.dismiss();
            }
        });
    }
}

