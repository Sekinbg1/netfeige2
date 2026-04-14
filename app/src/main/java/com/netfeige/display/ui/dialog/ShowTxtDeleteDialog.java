package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.netfeige.R;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.TxtBrowserActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class ShowTxtDeleteDialog extends Dialog {
    private boolean m_bIsCheck;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private CheckBox m_checkBnotify;
    private LayoutInflater m_inflater;
    private TxtBrowserActivity m_txtBrowserActivity;
    private View m_viewParent;

    public ShowTxtDeleteDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_bIsCheck = false;
        this.m_txtBrowserActivity = (TxtBrowserActivity) context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.deletedialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl(context, this.m_viewParent);
    }

    private void initControl(Context context, View view) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_delete);
        this.m_checkBnotify = checkBox;
        checkBox.setOnCheckedChangeListener(new CheckBNotifyOnCheckedChangeListener());
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
            ShowTxtDeleteDialog.this.dismiss();
            if (ShowTxtDeleteDialog.this.m_bIsCheck) {
                ((IpmsgApplication) ShowTxtDeleteDialog.this.m_txtBrowserActivity.getApplication()).g_strDelFilePrompt = ContentTree.ROOT_ID;
                DataConfig.getInstance(ShowTxtDeleteDialog.this.m_txtBrowserActivity.getApplicationContext()).Write(7, ContentTree.ROOT_ID);
            }
            ShowTxtDeleteDialog.this.m_txtBrowserActivity.deleteTxtFile();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ShowTxtDeleteDialog.this.dismiss();
        }
    }

    private class CheckBNotifyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private CheckBNotifyOnCheckedChangeListener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            ShowTxtDeleteDialog.this.m_bIsCheck = z;
        }
    }
}

