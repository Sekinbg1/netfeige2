package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;

/* JADX INFO: loaded from: classes.dex */
public class SortDialog extends Dialog {
    private Button m_btnSortAsc;
    private Button m_btnSortDes;
    private Context m_context;
    private IDataConfig m_dataConfig;
    private IpmsgActivity m_ipmsgActivity;
    private RadioButton m_radioBtnName;
    private RadioButton m_radioBtnTime;
    private View m_viewParent;

    public SortDialog(Context context, LayoutInflater layoutInflater) {
        super(context, R.style.sort_dialog);
        this.m_context = context;
        this.m_ipmsgActivity = (IpmsgActivity) context;
        View viewInflate = layoutInflater.inflate(R.layout.sortdialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCanceledOnTouchOutside(true);
        this.m_dataConfig = DataConfig.getInstance(context);
    }

    @Override // android.app.Dialog
    protected void onStart() {
        initControl(this.m_context, this.m_viewParent);
        super.onStart();
    }

    private void initControl(Context context, View view) {
        this.m_radioBtnName = (RadioButton) view.findViewById(R.id.radiob_name);
        this.m_radioBtnTime = (RadioButton) view.findViewById(R.id.radiob_time);
        this.m_btnSortAsc = (Button) view.findViewById(R.id.btn_asc);
        this.m_btnSortDes = (Button) view.findViewById(R.id.btn_des);
        recoverUserSet(context);
        this.m_btnSortAsc.setOnClickListener(new BtnAscClickLister());
        this.m_btnSortDes.setOnClickListener(new BtnDesClickLister());
    }

    private class BtnDesClickLister implements View.OnClickListener {
        private BtnDesClickLister() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SortDialog.this.m_btnSortDes.setBackgroundResource(R.drawable.despressed);
            SortDialog.this.m_btnSortAsc.setBackgroundResource(R.drawable.des);
            if (SortDialog.this.m_radioBtnName.isChecked()) {
                SortDialog.this.m_dataConfig.Write(30, ContentTree.VIDEO_ID);
                SortDialog.this.reSort();
            } else {
                SortDialog.this.m_dataConfig.Write(30, ContentTree.IMAGE_ID);
                SortDialog.this.reSort();
            }
            SortDialog.this.m_ipmsgActivity.loadingImage();
        }
    }

    private class BtnAscClickLister implements View.OnClickListener {
        private BtnAscClickLister() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SortDialog.this.m_btnSortAsc.setBackgroundResource(R.drawable.ascpressed);
            SortDialog.this.m_btnSortDes.setBackgroundResource(R.drawable.asc);
            if (SortDialog.this.m_radioBtnName.isChecked()) {
                SortDialog.this.m_dataConfig.Write(30, ContentTree.ROOT_ID);
                SortDialog.this.reSort();
            } else {
                SortDialog.this.m_dataConfig.Write(30, ContentTree.AUDIO_ID);
                SortDialog.this.reSort();
            }
            SortDialog.this.m_ipmsgActivity.loadingImage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reSort() {
        Public_Tools.reSort();
        dismiss();
        IpmsgActivity.m_popupWindowSend.dismiss();
        Global.g_bWaitPaste = 0;
        Global.g_pastePaths.clear();
        this.m_ipmsgActivity.getPopupWindowPaste().dismiss();
        this.m_ipmsgActivity.cacelChoiced();
    }

    private void recoverUserSet(Context context) {
        String strRead = this.m_dataConfig.Read(30);
        if (strRead.equals(ContentTree.ROOT_ID)) {
            this.m_radioBtnName.setChecked(true);
            this.m_btnSortAsc.setBackgroundResource(R.drawable.ascpressed);
            this.m_btnSortDes.setBackgroundResource(R.drawable.des);
        } else if (strRead.equals(ContentTree.VIDEO_ID)) {
            this.m_radioBtnName.setChecked(true);
            this.m_btnSortDes.setBackgroundResource(R.drawable.despressed);
            this.m_btnSortAsc.setBackgroundResource(R.drawable.asc);
        } else if (strRead.equals(ContentTree.AUDIO_ID)) {
            this.m_radioBtnTime.setChecked(true);
            this.m_btnSortAsc.setBackgroundResource(R.drawable.ascpressed);
            this.m_btnSortDes.setBackgroundResource(R.drawable.des);
        } else {
            this.m_radioBtnTime.setChecked(true);
            this.m_btnSortDes.setBackgroundResource(R.drawable.despressed);
            this.m_btnSortAsc.setBackgroundResource(R.drawable.asc);
        }
    }
}

