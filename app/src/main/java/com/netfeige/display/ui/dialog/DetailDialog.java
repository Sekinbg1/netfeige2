package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.filemanager.FileManager;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class DetailDialog extends Dialog {
    private Button m_btnConfirm;
    private Context m_context;
    private File m_file;
    private LayoutInflater m_inflater;
    private LinearLayout m_linearLSize;
    private View m_viewParent;

    public DetailDialog(Context context, File file) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_context = context;
        this.m_file = file;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.detaildialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl(context, this.m_viewParent);
    }

    private void initControl(Context context, View view) {
        this.m_linearLSize = (LinearLayout) view.findViewById(R.id.linear_size);
        if (!this.m_file.isFile()) {
            this.m_linearLSize.setVisibility(8);
        } else {
            this.m_linearLSize.setVisibility(0);
        }
        Button button = (Button) view.findViewById(R.id.btn_deleteconfirm);
        this.m_btnConfirm = button;
        button.setOnClickListener(new ComfirmClickListener());
        String[] fileDetail = FileManager.getIFileManager(this.m_context).getFileDetail(this.m_file);
        ((TextView) view.findViewById(R.id.tvshowname)).setText(fileDetail[0]);
        ((TextView) view.findViewById(R.id.tvshowsize)).setText(fileDetail[1]);
        ((TextView) view.findViewById(R.id.tvshowauthority)).setText(fileDetail[2]);
        ((TextView) view.findViewById(R.id.tvshowtime)).setText(fileDetail[3]);
        ((TextView) view.findViewById(R.id.tvshowpath)).setText(Public_Tools.cutName(fileDetail[4]));
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            DetailDialog.this.dismiss();
        }
    }
}

