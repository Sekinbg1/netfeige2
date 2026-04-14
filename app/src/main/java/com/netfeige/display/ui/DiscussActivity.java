package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.InviteDiscussInfo;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SoundPlayer;
import com.netfeige.display.data.DiscussExitListAdapter;
import com.netfeige.display.data.DiscussListAdapter;
import com.netfeige.display.data.InviteDiscussListAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.protocol.Protocol;
import com.netfeige.protocol.Protocol_Discuss;
import com.netfeige.service.IpmsgService;

/* JADX INFO: loaded from: classes.dex */
public class DiscussActivity extends Activity implements IpmsgService.DiscussInviteEventHandler, IpmsgService.DiscussInfoEventHandler, IpmsgService.DiscussExitEventHandler, IpmsgService.EventHandler {
    private DiscussListAdapter mAdapterDiscussList;
    private InviteDiscussListAdapter mAdapterInviteDiscussList;
    private DiscussExitListAdapter mDiscussExitListAdapter;
    private LinearLayout mLLayoutInvite;
    private ListView mListDiscuss;
    private ListView mListExitDiscuss;
    private ListView mListInviteDiscuss;
    private IpmsgApplication mMyApp;
    private RelativeLayout mRLayoutEmpty;
    private TextView mTextNew;
    private Button m_btnBack;
    private Button m_btnNew;
    private final int m_nMsgCategory = 2;
    private final int m_nListAddMsg = 1;
    private SoundPlayer mPlayer = null;
    public Handler handler = new Handler() { // from class: com.netfeige.display.ui.DiscussActivity.6
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2 && message.arg1 == 1) {
                DiscussActivity.this.mAdapterDiscussList.notifyDataSetChanged();
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
        setContentView(R.layout.activity_discuss);
        this.mMyApp = (IpmsgApplication) getApplication();
        this.mPlayer = new SoundPlayer();
        Button button = (Button) findViewById(R.id.back_btn_activity_discuss);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DiscussActivity.this.onBackPressed();
            }
        });
        Button button2 = (Button) findViewById(R.id.new_btn_activity_discuss);
        this.m_btnNew = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DiscussActivity.this.startActivity(new Intent(DiscussActivity.this, (Class<?>) NewDiscussActivity.class));
            }
        });
        ListView listView = (ListView) findViewById(R.id.exit_tip_list_activity_discuss);
        this.mListExitDiscuss = listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.DiscussActivity.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DiscussActivity.this.mMyApp.ipmsgService.mListExitDiscuss.remove(i);
                DiscussActivity.this.mDiscussExitListAdapter.notifyDataSetChanged();
            }
        });
        this.mLLayoutInvite = (LinearLayout) findViewById(R.id.invite_llayout_activity_discuss);
        this.mListInviteDiscuss = (ListView) findViewById(R.id.invite_list_activity_discuss);
        this.mRLayoutEmpty = (RelativeLayout) findViewById(R.id.empty_rlayout_activity_discuss);
        TextView textView = (TextView) findViewById(R.id.new_text_activity_discuss);
        this.mTextNew = textView;
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DiscussActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DiscussActivity.this.startActivity(new Intent(DiscussActivity.this, (Class<?>) NewDiscussActivity.class));
            }
        });
        ListView listView2 = (ListView) findViewById(R.id.list_activity_discuss);
        this.mListDiscuss = listView2;
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.DiscussActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DiscussInfo item = ((DiscussListAdapter) adapterView.getAdapter()).getItem(i);
                Intent intent = new Intent(DiscussActivity.this, (Class<?>) DiscussChatActivity.class);
                intent.putExtra("discussID", item.getStrId());
                intent.putExtra("discussName", item.getStrName());
                DiscussActivity.this.startActivity(intent);
            }
        });
        this.mListDiscuss.setEmptyView(this.mRLayoutEmpty);
        initData();
        this.mMyApp.ipmsgService.mDiscussInviteEHList.add(this);
        this.mMyApp.ipmsgService.mDiscussInfoEHList.add(this);
        this.mMyApp.ipmsgService.mDiscussExitEHList.add(this);
        this.mMyApp.ipmsgService.ehList.add(this);
    }

    private void initData() {
        DiscussExitListAdapter discussExitListAdapter = new DiscussExitListAdapter(this, 0, this.mMyApp.ipmsgService.mListExitDiscuss);
        this.mDiscussExitListAdapter = discussExitListAdapter;
        this.mListExitDiscuss.setAdapter((ListAdapter) discussExitListAdapter);
        InviteDiscussListAdapter inviteDiscussListAdapter = new InviteDiscussListAdapter(this, 0, this.mMyApp.ipmsgService.mListInviteDiscuss, new OnMyInviteClickHandler());
        this.mAdapterInviteDiscussList = inviteDiscussListAdapter;
        this.mListInviteDiscuss.setAdapter((ListAdapter) inviteDiscussListAdapter);
        DiscussListAdapter discussListAdapter = new DiscussListAdapter(this, 0, this.mMyApp.ipmsgService.mListDiscuss);
        this.mAdapterDiscussList = discussListAdapter;
        this.mListDiscuss.setAdapter((ListAdapter) discussListAdapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInviteList() {
        this.mAdapterInviteDiscussList.notifyDataSetChanged();
        if (this.mMyApp.ipmsgService.mListInviteDiscuss.size() > 0) {
            this.mLLayoutInvite.setVisibility(0);
        } else {
            this.mLLayoutInvite.setVisibility(8);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mMyApp.ipmsgService.mDiscussInviteEHList.remove(this);
        this.mMyApp.ipmsgService.mDiscussInfoEHList.remove(this);
        this.mMyApp.ipmsgService.mDiscussExitEHList.remove(this);
        this.mMyApp.ipmsgService.ehList.remove(this);
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.mMyApp.currentActivity = this;
        this.mDiscussExitListAdapter.notifyDataSetChanged();
        updateInviteList();
        this.mAdapterDiscussList.notifyDataSetChanged();
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
    }

    @Override // com.netfeige.service.IpmsgService.DiscussInviteEventHandler
    public void onInvite() {
        updateInviteList();
    }

    @Override // com.netfeige.service.IpmsgService.DiscussInfoEventHandler
    public void onDiscussInfo() {
        this.mAdapterDiscussList.notifyDataSetChanged();
    }

    @Override // com.netfeige.service.IpmsgService.DiscussExitEventHandler
    public void onDiscussExit() {
        this.mDiscussExitListAdapter.notifyDataSetChanged();
        this.mAdapterDiscussList.notifyDataSetChanged();
    }

    class OnMyInviteClickHandler implements InviteDiscussListAdapter.OnInviteClickListener {
        OnMyInviteClickHandler() {
        }

        @Override // com.netfeige.display.data.InviteDiscussListAdapter.OnInviteClickListener
        public void onReceive(View view) {
            InviteDiscussInfo inviteDiscussInfo = (InviteDiscussInfo) view.getTag();
            DiscussActivity.this.mMyApp.ipmsgService.mListInviteDiscuss.remove(inviteDiscussInfo);
            DiscussActivity.this.updateInviteList();
            HostInformation hostInfo = DiscussActivity.this.mMyApp.ipmsgService.getHostInfo(inviteDiscussInfo.getHostInformation().strMacAddr);
            if (hostInfo != null) {
                ((Protocol) DiscussActivity.this.mMyApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.agree(inviteDiscussInfo.getDiscussInfo().getStrId(), hostInfo);
            } else {
                DiscussActivity discussActivity = DiscussActivity.this;
                Public_Tools.showToast(discussActivity, discussActivity.getString(R.string.offline_prompt), 0);
            }
        }

        @Override // com.netfeige.display.data.InviteDiscussListAdapter.OnInviteClickListener
        public void onRefuse(View view) {
            InviteDiscussInfo inviteDiscussInfo = (InviteDiscussInfo) view.getTag();
            DiscussActivity.this.mMyApp.ipmsgService.mListInviteDiscuss.remove(inviteDiscussInfo);
            DiscussActivity.this.updateInviteList();
            HostInformation hostInfo = DiscussActivity.this.mMyApp.ipmsgService.getHostInfo(inviteDiscussInfo.getHostInformation().strMacAddr);
            if (hostInfo != null) {
                ((Protocol) DiscussActivity.this.mMyApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.reject(inviteDiscussInfo.getDiscussInfo().getStrId(), hostInfo);
            }
        }
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
        if (str == null || !str.startsWith(Protocol_Discuss.smPreID) || msgRecord == null || msgRecord.isSend()) {
            return;
        }
        Message messageObtain = Message.obtain(this.handler);
        messageObtain.what = 2;
        messageObtain.arg1 = 1;
        messageObtain.sendToTarget();
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileMessage(String str, MsgRecord msgRecord) {
        if (msgRecord.getFileId() == -1 && str != null && str.startsWith(Protocol_Discuss.smPreID)) {
            Message messageObtain = Message.obtain(this.handler);
            messageObtain.what = 2;
            messageObtain.arg1 = 1;
            messageObtain.sendToTarget();
        }
    }
}

