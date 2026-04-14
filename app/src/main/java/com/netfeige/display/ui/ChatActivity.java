package com.netfeige.display.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.GifTextDrawable;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SoundPlayer;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MessageAdapter;
import com.netfeige.display.data.MessageAdapter.GifRunnableImp;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.ui.dialog.ClearRecordDialog;
import com.netfeige.display.ui.dialog.MsgContextDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.kits.DataConfig;
import com.netfeige.service.IpmsgService;
import com.netfeige.util.OpenFileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class ChatActivity extends Activity implements IpmsgService.EventHandler, MsgContextDialog.EventHandler, SensorEventListener {
    private static final int DELETEMSGHISTORY = 1;
    public static String ip;
    public static String strMacAddr;
    public Button backBtn;
    public Button deleteBtn;
    public Button faceBtn;
    public GridView faceGridView;
    public Button fileBtn;
    private InputMethodManager imm;
    public EditText inputText;
    private SensorManager m_SensorManager;
    private ClearRecordDialog m_clearRecordDialog;
    private Context m_context;
    public IpmsgApplication m_myApp;
    private SendFileLinearLayout m_sendFileLinearLayout;
    public MessageAdapter messageAdapter;
    public ListView messageList;
    public Button sendBtn;
    public TextView userNameText;
    public final int m_nAddMessage = 0;
    public final int m_nModifyMessage = 1;
    public final int m_nModifyFileStatus = 2;
    private boolean faceShown = false;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.ChatActivity.9
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ProgressBar progressBar;
            TextView textView;
            TextView textView2;
            TextView textView3;
            TextView textView4;
            LinearLayout linearLayout;
            LinearLayout linearLayout2;
            int i = message.what;
            if (i == 0) {
                ChatActivity.this.messageAdapter.notifyDataSetChanged();
                if (message.obj == null) {
                    ChatActivity chatActivity = ChatActivity.this;
                    Public_Tools.showToast(chatActivity, chatActivity.getString(R.string.offline_prompt), 1);
                }
            } else if (i == 1) {
                ChatActivity.this.messageAdapter.notifyDataSetChanged();
            } else if (i == 2) {
                MsgRecord msgRecord = (MsgRecord) message.obj;
                int i2 = 0;
                while (true) {
                    if (i2 >= ChatActivity.this.messageList.getChildCount()) {
                        break;
                    }
                    View childAt = ChatActivity.this.messageList.getChildAt(i2);
                    if (((MsgRecord) childAt.getTag()) == msgRecord) {
                        LinearLayout linearLayout3 = (LinearLayout) childAt.findViewById(R.id.operate1_layout_filetranitemfrom);
                        if (msgRecord.isSend()) {
                            progressBar = (ProgressBar) childAt.findViewById(R.id.progress_filetranitemto);
                            textView = (TextView) childAt.findViewById(R.id.content_text_messageitemto);
                            textView2 = (TextView) childAt.findViewById(R.id.size_text_filetranitemto);
                            textView3 = (TextView) childAt.findViewById(R.id.date_text_filetranitemto);
                            textView4 = (TextView) childAt.findViewById(R.id.progress_text_filetranitemto);
                            linearLayout = (LinearLayout) childAt.findViewById(R.id.progress_layout_filetranitemto);
                            linearLayout2 = (LinearLayout) childAt.findViewById(R.id.operate_layout_filetranitemto);
                        } else {
                            progressBar = (ProgressBar) childAt.findViewById(R.id.progress_filetranitemfrom);
                            textView = (TextView) childAt.findViewById(R.id.content_text_messageitemfrom);
                            textView2 = (TextView) childAt.findViewById(R.id.size_text_filetranitemfrom);
                            textView3 = (TextView) childAt.findViewById(R.id.date_text_filetranitemfrom);
                            textView4 = (TextView) childAt.findViewById(R.id.progress_text_filetranitemfrom);
                            linearLayout = (LinearLayout) childAt.findViewById(R.id.progress_layout_filetranitemfrom);
                            linearLayout2 = (LinearLayout) childAt.findViewById(R.id.operate_layout_filetranitemfrom);
                        }
                        if (!msgRecord.screenShotList.isEmpty()) {
                            List list = (List) textView.getTag();
                            if (list != null && list.size() > 0) {
                                for (int i3 = 0; i3 < list.size(); i3++) {
                                    ((GifTextDrawable) list.get(i3)).stop();
                                }
                            }
                            textView.setClickable(true);
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            textView.setText(((IpmsgApplication) ChatActivity.this.getApplication()).appendFace(msgRecord.bodyStr, textView, msgRecord.screenShotList));
                            if (((List) textView.getTag()).size() > 0) {
                                MessageAdapter messageAdapter = ChatActivity.this.messageAdapter;
                                messageAdapter.getClass();
                                textView.post(messageAdapter.new GifRunnableImp((List) textView.getTag()));
                            }
                        } else if (msgRecord.getFileId() != -1) {
                            int i4 = AnonymousClass10.$SwitchMap$com$netfeige$common$Public_Def$TransStatus[msgRecord.getFileTranStatus().ordinal()];
                            if (i4 == 1) {
                                progressBar.setProgress(0);
                            } else if (i4 == 2) {
                                textView.setText(msgRecord.bodyStr);
                            } else {
                                if (i4 == 3) {
                                    linearLayout2.setVisibility(8);
                                    linearLayout3.setVisibility(0);
                                } else if (i4 == 4) {
                                }
                                textView.setText(msgRecord.bodyStr);
                                progressBar.setProgress(msgRecord.getnProgress());
                                textView4.setText(msgRecord.getStrCurrSpeed());
                            }
                        } else {
                            textView.setText(msgRecord.bodyStr);
                            textView2.setText(String.valueOf(Public_Tools.getShortSize(msgRecord.getSize())));
                            textView3.setText(Html.fromHtml(msgRecord.getDate()));
                            linearLayout.setVisibility(8);
                            linearLayout2.setVisibility(8);
                            linearLayout3.setVisibility(8);
                        }
                    } else {
                        i2++;
                    }
                }
            }
            super.handleMessage(message);
        }
    };

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str, boolean z) {
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int i) {
        if (i != 1) {
            return null;
        }
        return this.m_clearRecordDialog;
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int i, Dialog dialog) {
        if (i == 1) {
            this.m_clearRecordDialog.getTextVContent().setText(R.string.delete_msg_history_content);
            if (this.m_myApp.ipmsgService.fileMsgs.get(strMacAddr) != null) {
                ArrayList<MsgRecord> arrayList = this.m_myApp.ipmsgService.fileMsgs.get(strMacAddr);
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    if (arrayList.get(i2).getFileId() != -1) {
                        this.m_clearRecordDialog.getTextVContent().setText(R.string.file_trans_prompt);
                    }
                }
            }
        }
        super.onPrepareDialog(i, dialog);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.m_context = this;
            this.m_clearRecordDialog = new ClearRecordDialog(this);
            this.m_SensorManager = (SensorManager) getSystemService("sensor");
            this.imm = (InputMethodManager) getSystemService("input_method");
            this.m_myApp = (IpmsgApplication) getApplication();
            setContentView(R.layout.chat);
            this.m_sendFileLinearLayout = (SendFileLinearLayout) findViewById(R.id.linearlayout_popwindow);
            Button button = (Button) findViewById(R.id.back_btn_chat);
            this.backBtn = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChatActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ChatActivity.this.onBtnBack();
                }
            });
            Button button2 = (Button) findViewById(R.id.delete_btn_chat);
            this.deleteBtn = button2;
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChatActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ChatActivity.this.showDialog(1);
                }
            });
            this.userNameText = (TextView) findViewById(R.id.title_text_chat);
            Intent intent = getIntent();
            if (intent.getStringExtra("ip") != null) {
                ip = intent.getStringExtra("ip");
                strMacAddr = intent.getStringExtra("strMacAddr");
                this.userNameText.setText(intent.getStringExtra("name"));
            } else {
                ip = bundle.getString("ip");
                strMacAddr = bundle.getString("strMacAddr");
                this.userNameText.setText(intent.getStringExtra("name"));
            }
            this.messageList = (ListView) findViewById(R.id.message_list_chat);
            if (this.m_myApp.ipmsgService != null && strMacAddr != null) {
                IpmsgApplication.g_currentHostInfo = getHostInfo();
                ArrayList<MsgRecord> arrayList = this.m_myApp.ipmsgService.messages.get(strMacAddr);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.m_myApp.ipmsgService.messages.put(strMacAddr, arrayList);
                }
                MessageAdapter messageAdapter = new MessageAdapter(this, R.layout.messageitem, arrayList);
                this.messageAdapter = messageAdapter;
                this.messageList.setAdapter((ListAdapter) messageAdapter);
                this.messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.ChatActivity.3
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        if (view.getTag() != null) {
                            MsgRecord msgRecord = (MsgRecord) view.getTag();
                            if (!msgRecord.isFile()) {
                                int[] iArr = new int[2];
                                view.getLocationOnScreen(iArr);
                                MsgContextDialog msgContextDialog = new MsgContextDialog(ChatActivity.this, R.style.Dialog, (MsgRecord) view.getTag());
                                msgContextDialog.ehList.add(ChatActivity.this);
                                Window window = msgContextDialog.getWindow();
                                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                                layoutParams.x = iArr[0];
                                layoutParams.y = iArr[1] - 300;
                                window.setAttributes(layoutParams);
                                msgContextDialog.setCanceledOnTouchOutside(true);
                                msgContextDialog.show();
                            }
                            if ((msgRecord.isFile() && !msgRecord.isSend() && msgRecord.getFileTranStatus() == Public_Def.TransStatus.Trans_Done) || (msgRecord.isFile() && msgRecord.isSend())) {
                                File file = new File(msgRecord.getFileFullPath() + File.separator + msgRecord.getFileName());
                                if (file.isDirectory()) {
                                    ChatActivity.this.onOpenFolderInIpmsgActivity(file.getPath());
                                    return;
                                }
                                if (Public_Tools.isImageFile(file.getName())) {
                                    Global.g_bIsClearImageList = true;
                                    ImageAdapter.s_imageList.clear();
                                    ImageAdapter.s_imageList.add(new ImagePreview(0, file.getName(), file.getAbsolutePath(), null));
                                } else if (Public_Tools.isAudioFile(file.getName())) {
                                    Global.g_bIsClearAudioList = true;
                                    IpmsgApplication.g_arrMusicList.clear();
                                    IpmsgApplication.g_arrMusicList.add(new Music(0, file.getName(), file.getAbsolutePath(), -1L));
                                    Global.g_bOpenAudioInChatActivity = true;
                                } else if (Public_Tools.isVideoFile(file.getName())) {
                                    IpmsgApplication.g_arrVideoList.clear();
                                    IpmsgApplication.g_arrVideoList.add(new Music(0, file.getName(), file.getAbsolutePath(), -1L));
                                }
                                ChatActivity.this.openFile(file);
                            }
                        }
                    }
                });
                this.faceGridView = (GridView) findViewById(R.id.face_gridView_chat);
                this.faceGridView.setAdapter((ListAdapter) new SimpleAdapter(this, this.m_myApp.faceList, R.layout.faceitem, new String[]{"faceImage"}, new int[]{R.id.face_imageview_faceitem}));
                this.faceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.ChatActivity.4
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        HashMap<String, Object> map = ChatActivity.this.m_myApp.faceList.get(i);
                        ChatActivity.this.inputText.getText().insert(ChatActivity.this.inputText.getSelectionStart(), ChatActivity.this.m_myApp.appendFace(map));
                        ChatActivity.this.faceGridView.setVisibility(8);
                        ChatActivity.this.faceShown = false;
                        ChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                    }
                });
                Button button3 = (Button) findViewById(R.id.face_btn_chat);
                this.faceBtn = button3;
                button3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChatActivity.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (!ChatActivity.this.faceShown) {
                            ChatActivity.this.imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), 0);
                            ChatActivity.this.faceGridView.setVisibility(0);
                            ChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                            ChatActivity.this.faceShown = true;
                        } else {
                            ChatActivity.this.faceGridView.setVisibility(8);
                            ChatActivity.this.faceShown = false;
                        }
                        ChatActivity.this.inputText.requestFocus();
                    }
                });
                Button button4 = (Button) findViewById(R.id.file_btn_chat);
                this.fileBtn = button4;
                button4.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChatActivity.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        ChatActivity.this.imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), 0);
                        if (ChatActivity.this.faceShown) {
                            ChatActivity.this.faceGridView.setVisibility(8);
                            ChatActivity.this.faceShown = false;
                        }
                        if (ChatActivity.this.m_sendFileLinearLayout.isShown()) {
                            ChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                        } else {
                            ChatActivity.this.m_sendFileLinearLayout.setVisibility(0);
                        }
                        ChatActivity.this.inputText.requestFocus();
                    }
                });
                Button button5 = (Button) findViewById(R.id.send_btn_chat);
                this.sendBtn = button5;
                button5.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChatActivity.7
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        HostInformation hostInfo = ChatActivity.this.getHostInfo();
                        if (hostInfo == null) {
                            ChatActivity chatActivity = ChatActivity.this;
                            Public_Tools.showToast(chatActivity, chatActivity.getString(R.string.offline_prompt), 1);
                            return;
                        }
                        String string = ChatActivity.this.inputText.getText().toString();
                        if (string.length() != 0) {
                            ChatActivity.this.sendMsg(string, ChatActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendMsg(string, hostInfo));
                        } else {
                            ChatActivity chatActivity2 = ChatActivity.this;
                            Public_Tools.showToast(chatActivity2, chatActivity2.getResources().getString(R.string.send_message_prompt), 0);
                        }
                        ChatActivity.this.inputText.clearFocus();
                    }
                });
                EditText editText = (EditText) findViewById(R.id.input_edittext_chat);
                this.inputText = editText;
                editText.setFocusable(true);
                this.inputText.setFocusableInTouchMode(true);
                this.inputText.requestFocus();
                this.inputText.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChatActivity.8
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        ChatActivity.this.faceGridView.setVisibility(8);
                        ChatActivity.this.faceShown = false;
                        ChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                    }
                });
                this.m_myApp.ipmsgService.ehList.add(this);
            }
        } catch (InflateException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openFile(File file) {
        try {
            startActivity(OpenFileUtil.openFile(this, file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_myApp.currentActivity = this;
        sendFile();
        if (this.m_myApp.g_bBackRuning) {
            this.m_myApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        SensorManager sensorManager = this.m_SensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(1), 3);
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    private void sendFile() {
        HostInformation hostInfo = getHostInfo();
        if (hostInfo == null) {
            Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
            return;
        }
        hostInfo.unreadMsg = false;
        if (this.m_myApp.waitSendfiles.size() > 0) {
            sendFiles(hostInfo, this.inputText.getText().toString().length() != 0 ? this.inputText.getText().toString() : null);
            this.m_myApp.waitSendfiles.clear();
        }
        this.messageAdapter.notifyDataSetChanged();
        this.messageList.setSelection(this.m_myApp.ipmsgService.messages.get(strMacAddr).size() - 1);
    }

    private long sendFiles(HostInformation hostInformation, String str) {
        if (FileActivity.s_whatFolder == null) {
            return 0L;
        }
        int i = AnonymousClass10.$SwitchMap$com$netfeige$common$Category[FileActivity.s_whatFolder.ordinal()];
        if (i == 1) {
            return sendFile(hostInformation, str, FileListView.m_vecLimitedApk);
        }
        if (i == 2) {
            return sendFile(hostInformation, str, FileListView.m_vecLimitedAudio);
        }
        if (i == 3) {
            return sendFile(hostInformation, str, FileListView.m_vecLimitedDocument);
        }
        if (i == 4) {
            return sendFile(hostInformation, str, FileListView.m_vecLimitedImage);
        }
        if (i == 5) {
            return sendFile(hostInformation, str, FileListView.m_vecLimitedVideo);
        }
        return sendFile(hostInformation, str, null);
    }

    private long sendFile(HostInformation hostInformation, String str, Vector<String> vector) {
        return this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendFile(str, this.m_myApp.waitSendfiles, hostInformation, this.m_myApp.ipmsgService, vector, 0);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.m_myApp.currentActivity == this) {
            this.m_myApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_myApp, getString(R.string.app_name), DataConfig.getInstance(this).Read(0), R.drawable.ic_launcher);
        }
        this.m_SensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.m_SensorManager.unregisterListener(this);
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.m_sendFileLinearLayout.isShown()) {
            this.m_sendFileLinearLayout.setVisibility(8);
        } else if (this.faceShown) {
            this.faceGridView.setVisibility(8);
            this.faceShown = false;
        } else {
            super.onBackPressed();
        }
    }

    public void onBtnBack() {
        super.onBackPressed();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.m_myApp.ipmsgService.ehList.remove(this);
        IpmsgApplication.g_currentHostInfo = null;
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("ip", ip);
        bundle.putString("strMacAddr", strMacAddr);
        bundle.putString("name", this.userNameText.getText().toString());
        super.onSaveInstanceState(bundle);
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
        if (str.equals(strMacAddr)) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 0;
            messageObtain.obj = msgRecord;
            messageObtain.sendToTarget();
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyMessage(HostInformation hostInformation, MsgRecord msgRecord) {
        if (hostInformation.strMacAddr.equals(strMacAddr)) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 1;
            messageObtain.sendToTarget();
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileMessage(String str, MsgRecord msgRecord) {
        if (str.equals(strMacAddr)) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 2;
            messageObtain.obj = msgRecord;
            messageObtain.sendToTarget();
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.ChatActivity$10, reason: invalid class name */
    static /* synthetic */ class AnonymousClass10 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Category;
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Public_Def$TransStatus;

        static {
            int[] iArr = new int[Public_Def.TransStatus.values().length];
            $SwitchMap$com$netfeige$common$Public_Def$TransStatus = iArr;
            try {
                iArr[Public_Def.TransStatus.Trans_Ready.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$TransStatus[Public_Def.TransStatus.Trans_Rename.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$TransStatus[Public_Def.TransStatus.Trans_Recving.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$TransStatus[Public_Def.TransStatus.Trans_Sending.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            int[] iArr2 = new int[Category.values().length];
            $SwitchMap$com$netfeige$common$Category = iArr2;
            try {
                iArr2[Category.apk.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.audio.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.document.ordinal()] = 3;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.image.ordinal()] = 4;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$netfeige$common$Category[Category.video.ordinal()] = 5;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    public void sendMsg(String str, long j) {
        HostInformation hostInfo = getHostInfo();
        if (hostInfo == null) {
            return;
        }
        this.m_myApp.ipmsgService.processMessage(hostInfo, "", str, true, false, j);
        this.inputText.setText("");
        this.faceGridView.setVisibility(8);
        this.imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        this.faceShown = false;
        this.m_sendFileLinearLayout.setVisibility(8);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        float[] fArr = sensorEvent.values;
        if (type == 1) {
            if (Math.abs(fArr[0]) > 14.0f || Math.abs(fArr[1]) > 14.0f) {
                HostInformation hostInfo = getHostInfo();
                if (hostInfo == null) {
                    Public_Tools.showToast(this, getString(R.string.offline_prompt), 1);
                    return;
                }
                String string = this.inputText.getText().toString();
                if (string.length() != 0) {
                    sendMsg(string, this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendMsg(string, hostInfo));
                    if (this.m_myApp.g_strSendAudio.equals(ContentTree.VIDEO_ID)) {
                        new SoundPlayer().play(this, R.raw.rock, false);
                    }
                }
                this.inputText.clearFocus();
            }
        }
    }

    public HostInformation getHostInfo() {
        for (int i = 0; i < this.m_myApp.ipmsgService.userList.size(); i++) {
            try {
                if (this.m_myApp.ipmsgService.userList.get(i).strMacAddr.equals(strMacAddr)) {
                    return this.m_myApp.ipmsgService.userList.get(i);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override // com.netfeige.display.ui.dialog.MsgContextDialog.EventHandler
    public void onOpenFolderInIpmsgActivity(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("folderPath", str);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }
}

