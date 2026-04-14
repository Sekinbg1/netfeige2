package com.netfeige.display.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.netfeige.R;
import com.netfeige.common.FileInfo;
import com.netfeige.common.Global;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class DeleteDialog extends Dialog {
    private boolean m_bIsCheck;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private CheckBox m_checkBnotify;
    private Context m_context;
    private FileInfo m_fileInfo;
    private LayoutInflater m_inflater;
    private View m_viewParent;

    public DeleteDialog(Context context, FileInfo fileInfo) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_bIsCheck = false;
        this.m_context = context;
        this.m_fileInfo = fileInfo;
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

    private class CheckBNotifyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private CheckBNotifyOnCheckedChangeListener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            DeleteDialog.this.m_bIsCheck = z;
        }
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            DeleteDialog.this.dismiss();
            IpmsgActivity.m_popupWindowSend.dismiss();
            if (Global.g_filePath.size() <= 0 || !IpmsgActivity.s_fileListView.isInner(DeleteDialog.this.m_fileInfo.getPath(), Global.g_filePath)) {
                IpmsgActivity.s_fileListView.deleteFile(DeleteDialog.this.m_fileInfo);
            } else {
                IpmsgActivity.s_fileListView.deleteFileInfo(Global.g_filePath);
                Global.g_filePath.clear();
                if (IpmsgActivity.m_popupWindowSend.isShowing()) {
                    IpmsgActivity.m_popupWindowSend.dismiss();
                }
            }
            if (DeleteDialog.this.m_bIsCheck) {
                ((IpmsgApplication) ((Activity) DeleteDialog.this.m_context).getApplication()).g_strDelFilePrompt = ContentTree.ROOT_ID;
                DataConfig.getInstance(DeleteDialog.this.m_context.getApplicationContext()).Write(7, ContentTree.ROOT_ID);
            }
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            DeleteDialog.this.dismiss();
        }
    }
}

