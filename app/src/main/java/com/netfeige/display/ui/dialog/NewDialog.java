package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.netfeige.R;
import com.netfeige.display.ui.IpmsgActivity;

/* JADX INFO: loaded from: classes.dex */
public class NewDialog extends Dialog {
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private EditText m_editTFolderName;
    private LayoutInflater m_inflater;
    private IpmsgActivity m_ipmsgActivity;
    private View m_viewParent;

    public NewDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_ipmsgActivity = (IpmsgActivity) context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.newdialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl(context, this.m_viewParent);
    }

    private void initControl(Context context, View view) {
        this.m_editTFolderName = (EditText) view.findViewById(R.id.edittname);
        Button button = (Button) view.findViewById(R.id.btn_deleteconfirm);
        this.m_btnConfirm = button;
        button.setOnClickListener(new ComfirmClickListener());
        Button button2 = (Button) view.findViewById(R.id.btn_deletecancel);
        this.m_btnCancel = button2;
        button2.setOnClickListener(new CancelClickListener());
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            String string = NewDialog.this.m_editTFolderName.getText().toString();
            boolean zIsSameName = NewDialog.this.m_ipmsgActivity.isSameName(string);
            if (string.equals("")) {
                NewDialog.this.m_ipmsgActivity.toast(R.string.writefoldername);
                NewDialog.this.m_ipmsgActivity.keepDialog(false, NewDialog.this);
            } else if (zIsSameName) {
                NewDialog.this.m_ipmsgActivity.toast(R.string.hassamename);
                NewDialog.this.m_ipmsgActivity.keepDialog(false, NewDialog.this);
            } else {
                NewDialog.this.m_ipmsgActivity.newBuild(string);
                NewDialog.this.m_ipmsgActivity.keepDialog(true, NewDialog.this);
                NewDialog.this.dismiss();
            }
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            NewDialog.this.dismiss();
            NewDialog.this.m_ipmsgActivity.keepDialog(true, NewDialog.this);
        }
    }

    public EditText getEditTFolderName() {
        return this.m_editTFolderName;
    }
}

