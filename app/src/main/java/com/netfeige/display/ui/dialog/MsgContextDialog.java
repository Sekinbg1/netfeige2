package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.filemanager.FileManager;
import java.io.File;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MsgContextDialog extends Dialog {
    public ArrayList<EventHandler> ehList;
    private Button m_btnCancel;
    private Button m_btnCopy;
    private Button m_btnOpenFile;
    private ClipboardManager m_clipboard;
    private LinearLayout m_linearLCopy;
    private LinearLayout m_linearLOpenFile;
    private MsgRecord m_msgRecord;

    public interface EventHandler {
        void onOpenFolderInIpmsgActivity(String str);
    }

    public MsgContextDialog(Context context) {
        super(context);
        this.ehList = new ArrayList<>();
    }

    public MsgContextDialog(Context context, int i, MsgRecord msgRecord) {
        super(context, i);
        this.ehList = new ArrayList<>();
        this.m_msgRecord = msgRecord;
    }

    public MsgContextDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.ehList = new ArrayList<>();
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.msgcontextdialog);
        this.m_clipboard = (ClipboardManager) getContext().getSystemService("clipboard");
        this.m_linearLCopy = (LinearLayout) findViewById(R.id.copy_layout_msgcontext);
        if ((this.m_msgRecord.isFile() && !this.m_msgRecord.isSend() && this.m_msgRecord.getFileTranStatus() == Public_Def.TransStatus.Trans_Done) || (this.m_msgRecord.isFile() && this.m_msgRecord.isSend())) {
            this.m_linearLCopy.setVisibility(8);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.open_layout_msgcontext);
            this.m_linearLOpenFile = linearLayout;
            linearLayout.setVisibility(0);
            Button button = (Button) findViewById(R.id.open_btn_msgcontext);
            this.m_btnOpenFile = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.MsgContextDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    File file = new File(MsgContextDialog.this.m_msgRecord.getFileFullPath() + File.separator + MsgContextDialog.this.m_msgRecord.getFileName());
                    if (file.isDirectory()) {
                        for (int i = 0; i < MsgContextDialog.this.ehList.size(); i++) {
                            MsgContextDialog.this.ehList.get(i).onOpenFolderInIpmsgActivity(MsgContextDialog.this.m_msgRecord.getFileFullPath() + File.separator + MsgContextDialog.this.m_msgRecord.getFileName());
                        }
                    } else {
                        if (Public_Tools.isImageFile(file.getName())) {
                            ImageAdapter.s_imageList.clear();
                            ImageAdapter.s_imageList.add(new ImagePreview(0, file.getName(), file.getAbsolutePath(), null));
                        } else if (Public_Tools.isAudioFile(file.getName())) {
                            IpmsgApplication.g_arrMusicList.clear();
                            IpmsgApplication.g_arrMusicList.add(new Music(0, file.getName(), file.getAbsolutePath(), -1L));
                            Global.g_bOpenAudioInChatActivity = true;
                        } else if (Public_Tools.isVideoFile(file.getName())) {
                            IpmsgApplication.g_arrVideoList.clear();
                            IpmsgApplication.g_arrVideoList.add(new Music(0, file.getName(), file.getAbsolutePath(), -1L));
                        }
                        FileManager.getIFileManager(MsgContextDialog.this.getContext()).openFile(file);
                    }
                    MsgContextDialog.this.dismiss();
                }
            });
        } else {
            Button button2 = (Button) findViewById(R.id.copy_btn_msgcontext);
            this.m_btnCopy = button2;
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.MsgContextDialog.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MsgContextDialog.this.m_clipboard.setText(MsgContextDialog.this.m_msgRecord.bodyStr);
                    MsgContextDialog.this.dismiss();
                }
            });
        }
        Button button3 = (Button) findViewById(R.id.cancel_btn_msgcontext);
        this.m_btnCancel = button3;
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.dialog.MsgContextDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MsgContextDialog.this.dismiss();
            }
        });
    }
}

