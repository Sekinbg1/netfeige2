package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.netfeige.R;
import com.netfeige.display.ui.ImagePreviewActivity;

/* JADX INFO: loaded from: classes.dex */
public class LastImageDialog extends Dialog {
    private Button m_btnConfirm;
    private ImagePreviewActivity m_imagePreviewActivity;
    private LayoutInflater m_inflater;
    private View m_viewParent;

    public LastImageDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_imagePreviewActivity = (ImagePreviewActivity) context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.lastimagedialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl(context, this.m_viewParent);
    }

    private void initControl(Context context, View view) {
        Button button = (Button) view.findViewById(R.id.btn_deleteconfirm);
        this.m_btnConfirm = button;
        button.setOnClickListener(new ComfirmClickListener());
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            LastImageDialog.this.dismiss();
            LastImageDialog.this.m_imagePreviewActivity.onBackPressed();
        }
    }
}

