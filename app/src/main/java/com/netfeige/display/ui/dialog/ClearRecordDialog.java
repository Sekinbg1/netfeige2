package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.HostInformation;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.ui.ChatActivity;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ClearRecordDialog extends Dialog {
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private ChatActivity m_chatActivity;
    private LayoutInflater m_inflater;
    private IpmsgApplication m_ipmsgApp;
    private TextView m_textVContent;
    private View m_viewParent;

    public TextView getTextVContent() {
        return this.m_textVContent;
    }

    public ClearRecordDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        ChatActivity chatActivity = (ChatActivity) context;
        this.m_chatActivity = chatActivity;
        this.m_ipmsgApp = (IpmsgApplication) chatActivity.getApplication();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.clearrecorddialog, (ViewGroup) null);
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
        this.m_textVContent = (TextView) view.findViewById(R.id.textv_content);
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ClearRecordDialog.this.dismiss();
            HostInformation hostInfo = ClearRecordDialog.this.m_chatActivity.getHostInfo();
            if (ClearRecordDialog.this.m_ipmsgApp.ipmsgService.fileMsgs.get(ChatActivity.strMacAddr) != null && hostInfo != null) {
                ArrayList<MsgRecord> arrayList = ClearRecordDialog.this.m_ipmsgApp.ipmsgService.fileMsgs.get(ChatActivity.strMacAddr);
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getFileId() != -1) {
                        if (arrayList.get(i).isSend()) {
                            ClearRecordDialog.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.cancelSendFile(arrayList.get(i).getFileInfo(), hostInfo);
                        } else {
                            ClearRecordDialog.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.cancelRecvFile(arrayList.get(i).getFileInfo(), hostInfo);
                        }
                        arrayList.get(i).setFileId(-1L);
                        arrayList.get(i).setFileInfo(null);
                    }
                }
                ClearRecordDialog.this.m_ipmsgApp.ipmsgService.fileMsgs.get(ChatActivity.strMacAddr).clear();
            }
            if (ClearRecordDialog.this.m_ipmsgApp.ipmsgService.messages.get(ChatActivity.strMacAddr) != null) {
                ClearRecordDialog.this.m_ipmsgApp.ipmsgService.messages.get(ChatActivity.strMacAddr).clear();
            }
            ClearRecordDialog.this.m_chatActivity.messageAdapter.notifyDataSetChanged();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ClearRecordDialog.this.dismiss();
        }
    }
}

