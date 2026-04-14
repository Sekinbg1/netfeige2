package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.netfeige.R;
import com.netfeige.common.FileInfo;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.IpmsgActivity;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class RenameDialog extends Dialog {
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private Context m_context;
    private EditText m_editTFolderName;
    private FileInfo m_fileInfo;
    private LayoutInflater m_inflater;
    private IpmsgActivity m_ipmsgActivity;
    private int m_nPosition;
    private View m_viewParent;

    public RenameDialog(Context context, int i, FileInfo fileInfo) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_context = context;
        this.m_ipmsgActivity = (IpmsgActivity) context;
        this.m_nPosition = i;
        this.m_fileInfo = fileInfo;
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
            int iLastIndexOf;
            Editable text = RenameDialog.this.m_editTFolderName.getText();
            String strSubstring = (!RenameDialog.this.m_ipmsgActivity.getFile().isFile() || (iLastIndexOf = RenameDialog.this.m_fileInfo.getName().lastIndexOf(".")) == -1) ? "" : RenameDialog.this.m_fileInfo.getName().substring(iLastIndexOf, RenameDialog.this.m_fileInfo.getName().length());
            RenameDialog.this.m_ipmsgActivity.setNewName(text.toString() + strSubstring);
            String name = RenameDialog.this.m_fileInfo.getName();
            String path = RenameDialog.this.m_fileInfo.getPath();
            if ("" == RenameDialog.this.m_ipmsgActivity.getNewName() || RenameDialog.this.m_ipmsgActivity.getNewName().equals(name)) {
                RenameDialog.this.m_ipmsgActivity.toast(R.string.renamenotify);
                return;
            }
            int iLastIndexOf2 = RenameDialog.this.m_fileInfo.getPath().lastIndexOf(47);
            int positionOnName = Public_Tools.getPositionOnName(IpmsgActivity.s_fileListView.getFileAdapter().getFileList(), RenameDialog.this.m_ipmsgActivity.getNewName());
            if (positionOnName == -1) {
                RenameDialog.this.m_ipmsgActivity.differentNameOperate(RenameDialog.this.m_nPosition, name, path, iLastIndexOf2);
                RenameDialog.this.m_ipmsgActivity.startOperateContextMenuThread();
                if (iLastIndexOf2 != -1) {
                    RenameDialog.this.m_fileInfo.setPath(path.substring(0, iLastIndexOf2) + ServiceReference.DELIMITER + RenameDialog.this.m_ipmsgActivity.getNewName());
                }
            } else {
                new SameDialog(RenameDialog.this.m_context, RenameDialog.this.m_nPosition, RenameDialog.this.m_fileInfo, name, path, iLastIndexOf2, positionOnName).show();
            }
            RenameDialog.this.dismiss();
            IpmsgActivity.m_popupWindowSend.dismiss();
            RenameDialog.this.m_ipmsgActivity.cacelChoiced();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            RenameDialog.this.dismiss();
        }
    }

    public EditText getEditTFolderName() {
        return this.m_editTFolderName;
    }
}

