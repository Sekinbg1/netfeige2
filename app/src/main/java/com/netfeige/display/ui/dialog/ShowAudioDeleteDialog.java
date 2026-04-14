package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.netfeige.R;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.PlayMusicActivity;
import com.netfeige.display.ui.PlayVideoActivity;

/* JADX INFO: loaded from: classes.dex */
public class ShowAudioDeleteDialog extends Dialog {
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private Context m_context;
    private LayoutInflater m_inflater;
    private View m_viewParent;

    public ShowAudioDeleteDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.showdeletedialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        this.m_context = context;
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
            ShowAudioDeleteDialog.this.dismiss();
            try {
                if (!(ShowAudioDeleteDialog.this.m_context instanceof PlayMusicActivity)) {
                    if (ShowAudioDeleteDialog.this.m_context instanceof PlayVideoActivity) {
                        ((PlayVideoActivity) ShowAudioDeleteDialog.this.m_context).deleteVideo();
                    }
                } else {
                    ((PlayMusicActivity) ShowAudioDeleteDialog.this.m_context).deleteAudio(IpmsgApplication.g_arrPlayingList.get(PlayMusicActivity.s_nCurrentIndex).getPath());
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ShowAudioDeleteDialog.this.dismiss();
        }
    }
}

