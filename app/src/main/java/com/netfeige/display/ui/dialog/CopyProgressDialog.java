package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.netfeige.R;

/* JADX INFO: loaded from: classes.dex */
public class CopyProgressDialog extends Dialog {
    public static ProgressBar m_progressBCopy;
    public static TextView m_textVProgress;
    private Button m_btnBackGround;
    private Button m_btnCancel;
    private Context m_context;
    private LayoutInflater m_inflater;
    private OnCopyProgressClickListener m_onCopyProgressClickListener;
    private View m_viewParent;

    public interface OnCopyProgressClickListener {
        void onCopyProgressClick(View view);
    }

    public OnCopyProgressClickListener getOnCopyProgressClickListener() {
        return this.m_onCopyProgressClickListener;
    }

    public void setOnCopyProgressClickListener(OnCopyProgressClickListener onCopyProgressClickListener) {
        this.m_onCopyProgressClickListener = onCopyProgressClickListener;
    }

    public CopyProgressDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.copyprogressdialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl(context, this.m_viewParent);
    }

    private void initControl(Context context, View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar_copy);
        m_progressBCopy = progressBar;
        progressBar.setProgress(0);
        TextView textView = (TextView) view.findViewById(R.id.textv_progress);
        m_textVProgress = textView;
        textView.setText("");
        Button button = (Button) view.findViewById(R.id.btn_backgroundcopy);
        this.m_btnBackGround = button;
        button.setOnClickListener(new ButtonClickListener());
        Button button2 = (Button) view.findViewById(R.id.btn_copycancel);
        this.m_btnCancel = button2;
        button2.setOnClickListener(new ButtonClickListener());
    }

    private class ButtonClickListener implements View.OnClickListener {
        private ButtonClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (CopyProgressDialog.this.m_onCopyProgressClickListener != null) {
                CopyProgressDialog.this.m_onCopyProgressClickListener.onCopyProgressClick(view);
            }
            CopyProgressDialog.this.dismiss();
        }
    }
}

