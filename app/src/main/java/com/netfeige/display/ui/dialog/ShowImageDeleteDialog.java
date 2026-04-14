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
import com.netfeige.common.ImagePreview;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.ImagePreviewActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class ShowImageDeleteDialog extends Dialog {
    private boolean m_bIsCheck;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private CheckBox m_checkBnotify;
    private ImagePreviewActivity m_imagePreviewActivity;
    private LayoutInflater m_inflater;
    private View m_viewParent;

    public ShowImageDeleteDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_bIsCheck = false;
        this.m_imagePreviewActivity = (ImagePreviewActivity) context;
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
            ShowImageDeleteDialog.this.dismiss();
            if (ShowImageDeleteDialog.this.m_bIsCheck) {
                ((IpmsgApplication) ShowImageDeleteDialog.this.m_imagePreviewActivity.getApplication()).g_strDelFilePrompt = ContentTree.ROOT_ID;
                DataConfig.getInstance(ShowImageDeleteDialog.this.m_imagePreviewActivity.getApplicationContext()).Write(7, ContentTree.ROOT_ID);
            }
            try {
                if (ImagePreviewActivity.s_nCurrentImageIndex >= 0) {
                    ShowImageDeleteDialog.this.m_imagePreviewActivity.setImagePreview((ImagePreview) ShowImageDeleteDialog.this.m_imagePreviewActivity.getM_imageAdapter().getItem(ImagePreviewActivity.s_nCurrentImageIndex));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            ShowImageDeleteDialog.this.m_imagePreviewActivity.deleteImage(ShowImageDeleteDialog.this.m_imagePreviewActivity.getImagePreview().getM_strImagePath());
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ShowImageDeleteDialog.this.dismiss();
        }
    }

    private class CheckBNotifyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private CheckBNotifyOnCheckedChangeListener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            ShowImageDeleteDialog.this.m_bIsCheck = z;
        }
    }
}

