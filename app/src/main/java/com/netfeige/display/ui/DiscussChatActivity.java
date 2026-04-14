package com.netfeige.display.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.GifTextDrawable;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.DiscussMessageAdapter;
import com.netfeige.display.data.DiscussMessageAdapter.GifRunnableImp;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.ui.dialog.MsgContextDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.protocol.Protocol;
import com.netfeige.service.IpmsgService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class DiscussChatActivity extends Activity implements IpmsgService.EventHandler {
    private static final int DELETEMSGHISTORY = 1;
    public static String ip;
    public static String mStrDiscussID;
    public static String strMacAddr;
    public Button backBtn;
    public Button deleteBtn;
    public Button faceBtn;
    public GridView faceGridView;
    public Button fileBtn;
    private InputMethodManager imm;
    public EditText inputText;
    public DiscussInfo mDiscussInfo;
    private Context m_context;
    private LinearLayout m_linearLPopupExit;
    private LinearLayout m_linearLPopupSetup;
    public IpmsgApplication m_myApp;
    private int m_nPopX;
    private int m_nPopY;
    private PopupWindow m_popupWindow;
    private SendFileLinearLayout m_sendFileLinearLayout;
    public DiscussMessageAdapter messageAdapter;
    public ListView messageList;
    public Button sendBtn;
    public TextView userNameText;
    public final int m_nAddMessage = 0;
    public final int m_nModifyMessage = 1;
    public final int m_nModifyFileStatus = 2;
    private boolean faceShown = false;
    private LayoutInflater m_inflater = null;
    private final int m_nPopUpX = 5;
    private final int m_nPopUpY = 75;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.DiscussChatActivity.9
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
            if (i == 0 || i == 1) {
                if (DiscussChatActivity.this.mDiscussInfo != null) {
                    DiscussChatActivity.this.mDiscussInfo.setBUnreadMsg(false);
                }
                DiscussChatActivity.this.messageAdapter.notifyDataSetChanged();
            } else if (i == 2) {
                MsgRecord msgRecord = (MsgRecord) message.obj;
                int i2 = 0;
                while (true) {
                    if (i2 >= DiscussChatActivity.this.messageList.getChildCount()) {
                        break;
                    }
                    View childAt = DiscussChatActivity.this.messageList.getChildAt(i2);
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
                            textView.setText(((IpmsgApplication) DiscussChatActivity.this.getApplication()).appendFace(msgRecord.bodyStr, textView, msgRecord.screenShotList));
                            if (((List) textView.getTag()).size() > 0) {
                                DiscussMessageAdapter discussMessageAdapter = DiscussChatActivity.this.messageAdapter;
                                discussMessageAdapter.getClass();
                                textView.post(discussMessageAdapter.new GifRunnableImp((List) textView.getTag()));
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

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str, boolean z) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyMessage(HostInformation hostInformation, MsgRecord msgRecord) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.m_context = this;
            this.imm = (InputMethodManager) getSystemService("input_method");
            this.m_myApp = (IpmsgApplication) getApplication();
            setContentView(R.layout.chat);
            this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
            this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
            this.m_nPopY = Public_Tools.dip2px(this, 75.0f);
            SendFileLinearLayout sendFileLinearLayout = (SendFileLinearLayout) findViewById(R.id.linearlayout_popwindow);
            this.m_sendFileLinearLayout = sendFileLinearLayout;
            sendFileLinearLayout.setVisibility(8);
            Button button = (Button) findViewById(R.id.back_btn_chat);
            this.backBtn = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    DiscussChatActivity.this.onBtnBack();
                }
            });
            Button button2 = (Button) findViewById(R.id.delete_btn_chat);
            this.deleteBtn = button2;
            button2.setBackground(getResources().getDrawable(R.drawable.x_more));
            this.deleteBtn.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    DiscussChatActivity.this.m_popupWindow.showAtLocation(DiscussChatActivity.this.findViewById(R.id.top_navigation_layout_chat), 53, DiscussChatActivity.this.m_nPopX, DiscussChatActivity.this.m_nPopY);
                }
            });
            this.userNameText = (TextView) findViewById(R.id.title_text_chat);
            Intent intent = getIntent();
            if (intent.getStringExtra("discussID") != null) {
                mStrDiscussID = intent.getStringExtra("discussID");
                this.userNameText.setText(intent.getStringExtra("discussName"));
            } else {
                mStrDiscussID = bundle.getString("discussID");
                this.userNameText.setText(intent.getStringExtra("discussName"));
            }
            this.mDiscussInfo = this.m_myApp.ipmsgService.getDiscussInfo(mStrDiscussID);
            this.messageList = (ListView) findViewById(R.id.message_list_chat);
            if (this.m_myApp.ipmsgService != null && mStrDiscussID != null) {
                ArrayList<MsgRecord> arrayList = this.m_myApp.ipmsgService.messages.get(mStrDiscussID);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.m_myApp.ipmsgService.messages.put(mStrDiscussID, arrayList);
                }
                DiscussMessageAdapter discussMessageAdapter = new DiscussMessageAdapter(this, R.layout.messageitem, arrayList, mStrDiscussID);
                this.messageAdapter = discussMessageAdapter;
                this.messageList.setAdapter((ListAdapter) discussMessageAdapter);
                this.messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.3
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        if (view.getTag() != null) {
                            MsgRecord msgRecord = (MsgRecord) view.getTag();
                            if (!msgRecord.isFile()) {
                                int[] iArr = new int[2];
                                view.getLocationOnScreen(iArr);
                                MsgContextDialog msgContextDialog = new MsgContextDialog(DiscussChatActivity.this, R.style.Dialog, (MsgRecord) view.getTag());
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
                                FileManager.getIFileManager(DiscussChatActivity.this.m_context).openFile(file);
                            }
                        }
                    }
                });
                this.faceGridView = (GridView) findViewById(R.id.face_gridView_chat);
                this.faceGridView.setAdapter((ListAdapter) new SimpleAdapter(this, this.m_myApp.faceList, R.layout.faceitem, new String[]{"faceImage"}, new int[]{R.id.face_imageview_faceitem}));
                this.faceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.4
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        HashMap<String, Object> map = DiscussChatActivity.this.m_myApp.faceList.get(i);
                        DiscussChatActivity.this.inputText.getText().insert(DiscussChatActivity.this.inputText.getSelectionStart(), DiscussChatActivity.this.m_myApp.appendFace(map));
                        DiscussChatActivity.this.faceGridView.setVisibility(8);
                        DiscussChatActivity.this.faceShown = false;
                        DiscussChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                    }
                });
                Button button3 = (Button) findViewById(R.id.face_btn_chat);
                this.faceBtn = button3;
                button3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (!DiscussChatActivity.this.faceShown) {
                            DiscussChatActivity.this.imm.hideSoftInputFromWindow(DiscussChatActivity.this.getCurrentFocus().getWindowToken(), 0);
                            DiscussChatActivity.this.faceGridView.setVisibility(0);
                            DiscussChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                            DiscussChatActivity.this.faceShown = true;
                        } else {
                            DiscussChatActivity.this.faceGridView.setVisibility(8);
                            DiscussChatActivity.this.faceShown = false;
                        }
                        DiscussChatActivity.this.inputText.requestFocus();
                    }
                });
                Button button4 = (Button) findViewById(R.id.file_btn_chat);
                this.fileBtn = button4;
                button4.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        DiscussChatActivity.this.imm.hideSoftInputFromWindow(DiscussChatActivity.this.getCurrentFocus().getWindowToken(), 0);
                        if (DiscussChatActivity.this.faceShown) {
                            DiscussChatActivity.this.faceGridView.setVisibility(8);
                            DiscussChatActivity.this.faceShown = false;
                        }
                        if (DiscussChatActivity.this.m_sendFileLinearLayout.isShown()) {
                            DiscussChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                        } else {
                            DiscussChatActivity.this.m_sendFileLinearLayout.setVisibility(0);
                        }
                        DiscussChatActivity.this.inputText.requestFocus();
                    }
                });
                Button button5 = (Button) findViewById(R.id.send_btn_chat);
                this.sendBtn = button5;
                button5.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.7
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        String string = DiscussChatActivity.this.inputText.getText().toString();
                        if (string.length() != 0) {
                            DiscussChatActivity.this.sendMsg(string, DiscussChatActivity.this.sendMsgToDiscuss(string, DiscussChatActivity.mStrDiscussID));
                        } else {
                            DiscussChatActivity discussChatActivity = DiscussChatActivity.this;
                            Public_Tools.showToast(discussChatActivity, discussChatActivity.getResources().getString(R.string.send_message_prompt), 0);
                        }
                        DiscussChatActivity.this.inputText.clearFocus();
                    }
                });
                EditText editText = (EditText) findViewById(R.id.input_edittext_chat);
                this.inputText = editText;
                editText.setFocusable(true);
                this.inputText.setFocusableInTouchMode(true);
                this.inputText.requestFocus();
                this.inputText.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.8
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        DiscussChatActivity.this.faceGridView.setVisibility(8);
                        DiscussChatActivity.this.faceShown = false;
                        DiscussChatActivity.this.m_sendFileLinearLayout.setVisibility(8);
                    }
                });
                initPopupWindow();
            }
        } catch (InflateException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void initPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popup_discuss, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup2));
        this.m_popupWindow.setFocusable(true);
        this.m_popupWindow.setOutsideTouchable(true);
        this.m_popupWindow.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.setup_linearl_popup_discuss);
        this.m_linearLPopupSetup = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowSetupOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.exit_linearl_popup_discuss);
        this.m_linearLPopupExit = linearLayout2;
        linearLayout2.setOnClickListener(new PopUpWindowExitOnClickListener());
    }

    private class PopUpWindowSetupOnClickListener implements View.OnClickListener {
        private PopUpWindowSetupOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(DiscussChatActivity.this, (Class<?>) NewDiscussActivity.class);
            intent.putExtra(a.a, 1);
            intent.putExtra("discussID", DiscussChatActivity.mStrDiscussID);
            DiscussChatActivity.this.startActivity(intent);
            DiscussChatActivity.this.m_popupWindow.dismiss();
        }
    }

    private class PopUpWindowExitOnClickListener implements View.OnClickListener {
        private PopUpWindowExitOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DiscussChatActivity.this.m_context);
            builder.setTitle(R.string.notify);
            builder.setMessage("你确定要退出讨论组\"" + ((Object) DiscussChatActivity.this.userNameText.getText()) + "\"?");
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.netfeige.display.ui.DiscussChatActivity.PopUpWindowExitOnClickListener.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((Protocol) DiscussChatActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.exit(DiscussChatActivity.mStrDiscussID, Public_Tools.getLocalMacAddress());
                    DiscussChatActivity.this.finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
            builder.show();
            DiscussChatActivity.this.m_popupWindow.dismiss();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_myApp.currentActivity = this;
        DiscussInfo discussInfo = this.mDiscussInfo;
        if (discussInfo != null) {
            discussInfo.setBUnreadMsg(false);
        }
        this.m_myApp.ipmsgService.ehList.add(this);
        sendFile();
        if (this.m_myApp.g_bBackRuning) {
            this.m_myApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    private void sendFile() {
        if (this.m_myApp.waitSendfiles.size() > 0) {
            sendFiles(mStrDiscussID);
            this.m_myApp.waitSendfiles.clear();
        }
        this.messageAdapter.notifyDataSetChanged();
        this.messageList.setSelection(this.m_myApp.ipmsgService.messages.get(mStrDiscussID).size() - 1);
    }

    private long sendFiles(String str) {
        if (FileActivity.s_whatFolder == null) {
            return 0L;
        }
        int i = AnonymousClass10.$SwitchMap$com$netfeige$common$Category[FileActivity.s_whatFolder.ordinal()];
        if (i == 1) {
            return sendFile(str, FileListView.m_vecLimitedApk);
        }
        if (i == 2) {
            return sendFile(str, FileListView.m_vecLimitedAudio);
        }
        if (i == 3) {
            return sendFile(str, FileListView.m_vecLimitedDocument);
        }
        if (i == 4) {
            return sendFile(str, FileListView.m_vecLimitedImage);
        }
        if (i == 5) {
            return sendFile(str, FileListView.m_vecLimitedVideo);
        }
        return sendFile(str, null);
    }

    private long sendFile(String str, Vector<String> vector) {
        return this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendFile(str, this.m_myApp.waitSendfiles, null, this.m_myApp.ipmsgService, vector, 0);
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
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.m_myApp.ipmsgService.ehList.remove(this);
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onBtnBack() {
        super.onBackPressed();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("discussID", mStrDiscussID);
        bundle.putString("discussName", this.userNameText.getText().toString());
        super.onSaveInstanceState(bundle);
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
        if (str.equals(mStrDiscussID)) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 0;
            messageObtain.obj = msgRecord;
            messageObtain.sendToTarget();
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileMessage(String str, MsgRecord msgRecord) {
        if (str.equals(mStrDiscussID)) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 2;
            messageObtain.obj = msgRecord;
            messageObtain.sendToTarget();
        }
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.DiscussChatActivity$10, reason: invalid class name */
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

    public long sendMsgToDiscuss(String str, String str2) {
        HostInformation hostInfo;
        DiscussInfo discussInfo = this.m_myApp.ipmsgService.getDiscussInfo(str2);
        if (str == null || discussInfo == null) {
            return -1L;
        }
        String[] strArrSplit = discussInfo.getStrsMember().split(Public_MsgID.PRO_SPACE_GROUP);
        String str3 = str + Public_MsgID.CUTAPART + str2;
        for (int i = 0; i < strArrSplit.length; i++) {
            if (!strArrSplit[i].equals(Public_Tools.getLocalMacAddress()) && (hostInfo = this.m_myApp.ipmsgService.getHostInfo(strArrSplit[i])) != null) {
                this.m_myApp.ipmsgService.m_DataSource.m_Protocol.sendMsg(str3, hostInfo);
            }
        }
        return Public_Tools.getCurrentTimeMillis();
    }

    public void sendMsg(String str, long j) {
        this.m_myApp.ipmsgService.processMessage(Public_Tools.getLocalHostInfo(), mStrDiscussID, str, true, false, j);
        this.inputText.setText("");
        this.imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        this.faceShown = false;
    }
}

