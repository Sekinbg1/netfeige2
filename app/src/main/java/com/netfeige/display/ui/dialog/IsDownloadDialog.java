package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.LanSharedItem;
import com.netfeige.display.ui.LanShareActivity;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class IsDownloadDialog extends Dialog {
    public static boolean m_bIsCheck = false;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private CheckBox m_checkBox;
    private Context m_context;
    private LayoutInflater m_inflater;
    HashMap<String, Object> m_mapLanShareDLArgs;
    private TextView m_textV;
    private View m_viewParent;

    public IsDownloadDialog(Context context, HashMap<String, Object> map) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.simpledialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        this.m_mapLanShareDLArgs = map;
        setContentView(viewInflate);
        initControl();
        initData();
    }

    public IsDownloadDialog(Context context, int i) {
        super(context, i);
        this.m_inflater = null;
    }

    public IsDownloadDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.m_inflater = null;
    }

    private void initControl() {
        this.m_textV = (TextView) this.m_viewParent.findViewById(R.id.textv_simpledialog);
        this.m_btnConfirm = (Button) this.m_viewParent.findViewById(R.id.confirm_btn_simpledialog);
        this.m_btnCancel = (Button) this.m_viewParent.findViewById(R.id.cancel_btn_simpledialog);
        this.m_btnConfirm.setOnClickListener(new ComfirmClickListener());
        this.m_btnCancel.setOnClickListener(new CancelClickListener());
        CheckBox checkBox = (CheckBox) this.m_viewParent.findViewById(R.id.checkbox_simpledialog);
        this.m_checkBox = checkBox;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.netfeige.display.ui.dialog.IsDownloadDialog.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                IsDownloadDialog.m_bIsCheck = z;
            }
        });
    }

    private void initData() {
        this.m_textV.setText("确认要下载此文件吗？");
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ((LanShareActivity) IsDownloadDialog.this.m_context).preClickDLDispose((LanSharedItem) IsDownloadDialog.this.m_mapLanShareDLArgs.get("lanSharedItem"));
            IsDownloadDialog.this.dismiss();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            IsDownloadDialog.this.dismiss();
        }
    }
}

