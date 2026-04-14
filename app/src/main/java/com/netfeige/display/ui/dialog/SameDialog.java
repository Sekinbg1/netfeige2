package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.netfeige.R;
import com.netfeige.common.FileInfo;
import com.netfeige.display.ui.IpmsgActivity;

/* JADX INFO: loaded from: classes.dex */
public class SameDialog extends Dialog {
    private int m_bFlag;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private FileInfo m_fileInfo;
    private LayoutInflater m_inflater;
    private IpmsgActivity m_ipmsgActivity;
    private int m_nPos2;
    private int m_nPosition;
    private String m_strOldName;
    private String m_strOldPath;
    private View m_viewParent;

    public SameDialog(Context context, int i, FileInfo fileInfo, String str, String str2, int i2, int i3) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_nPosition = i;
        this.m_fileInfo = fileInfo;
        this.m_strOldName = str;
        this.m_strOldPath = str2;
        this.m_nPos2 = i2;
        this.m_bFlag = i3;
        this.m_ipmsgActivity = (IpmsgActivity) context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.samedialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl(context, this.m_viewParent);
    }

    private void initControl(Context context, View view) {
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
            SameDialog.this.dismiss();
            SameDialog.this.m_ipmsgActivity.sameNameOprate(SameDialog.this.m_nPosition, SameDialog.this.m_fileInfo, SameDialog.this.m_strOldName, SameDialog.this.m_strOldPath, SameDialog.this.m_nPos2, SameDialog.this.m_bFlag);
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SameDialog.this.dismiss();
        }
    }
}

