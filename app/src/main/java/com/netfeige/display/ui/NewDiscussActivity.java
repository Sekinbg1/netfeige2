package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.DiscussInfo;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.SelectedMemListAdapter;
import com.netfeige.protocol.Protocol;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class NewDiscussActivity extends Activity {
    public static final int TYPE_DISCUSS_EDIT = 1;
    public static final int TYPE_DISCUSS_NEW = 0;
    private Button mBtnBack;
    private Button mBtnSave;
    private EditText mEditName;
    private ImageView mImgAddMember;
    private ListView mListViewMember;
    private IpmsgApplication mMyApp;
    private SelectedMemListAdapter mSelectedMemListAdapter;
    private String mStrDiscussId;
    private TextView mTextTitle;
    private ArrayList<HostInformation> mListMember = new ArrayList<>();
    private ArrayList<String> mListMemberExisted = new ArrayList<>();
    private final int REQUEST_CODE = 1;
    private int type = -1;
    private boolean mBIsManager = true;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mMyApp = (IpmsgApplication) getApplication();
        setContentView(R.layout.activity_newdiscuss);
        this.type = getIntent().getIntExtra(a.a, 0);
        initControl();
        initData();
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.back_btn_activity_newdiscuss);
        this.mBtnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.NewDiscussActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NewDiscussActivity.this.onBackPressed();
            }
        });
        this.mTextTitle = (TextView) findViewById(R.id.title_text_activity_newdiscuss);
        Button button2 = (Button) findViewById(R.id.save_btn_activity_newdiscuss);
        this.mBtnSave = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.NewDiscussActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String string = NewDiscussActivity.this.mEditName.getText().toString();
                if (string.trim().length() <= 0) {
                    Toast.makeText(NewDiscussActivity.this, "讨论组名称不能为空", 0).show();
                    return;
                }
                Vector<String> vector = new Vector<>();
                for (int i = 0; i < NewDiscussActivity.this.mListMember.size(); i++) {
                    vector.add(((HostInformation) NewDiscussActivity.this.mListMember.get(i)).strMacAddr);
                }
                if (NewDiscussActivity.this.type == 0) {
                    ((Protocol) NewDiscussActivity.this.mMyApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.create(string, vector, NewDiscussActivity.this.mStrDiscussId);
                    NewDiscussActivity.this.finish();
                    return;
                }
                for (int i2 = 0; i2 < vector.size(); i2++) {
                    if (NewDiscussActivity.this.mListMemberExisted.contains(vector.get(i2))) {
                        NewDiscussActivity.this.mListMemberExisted.remove(vector.get(i2));
                    } else {
                        ((Protocol) NewDiscussActivity.this.mMyApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.invite(NewDiscussActivity.this.mStrDiscussId, vector.get(i2));
                    }
                }
                if (NewDiscussActivity.this.mBIsManager) {
                    for (int i3 = 0; i3 < NewDiscussActivity.this.mListMemberExisted.size(); i3++) {
                        ((Protocol) NewDiscussActivity.this.mMyApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.exit(NewDiscussActivity.this.mStrDiscussId, (String) NewDiscussActivity.this.mListMemberExisted.get(i3));
                    }
                }
                NewDiscussActivity.this.finish();
            }
        });
        this.mEditName = (EditText) findViewById(R.id.name_edit_activity_newdiscuss);
        ImageView imageView = (ImageView) findViewById(R.id.add_member_img_activity_newdiscuss);
        this.mImgAddMember = imageView;
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.NewDiscussActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(NewDiscussActivity.this, (Class<?>) ChoiceUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "NewDiscussActivity");
                intent.putExtras(bundle);
                NewDiscussActivity.this.startActivityForResult(intent, 1);
            }
        });
        ListView listView = (ListView) findViewById(R.id.member_list_activity_newdiscuss);
        this.mListViewMember = listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.NewDiscussActivity.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            }
        });
    }

    private void initData() {
        HostInformation hostInfo = this.mMyApp.ipmsgService.getHostInfo(Public_Tools.getLocalMacAddress());
        if (this.type == 0) {
            this.mStrDiscussId = "Discuss_{" + UUID.randomUUID().toString().toUpperCase() + "}";
            if (hostInfo != null) {
                this.mEditName.setText(hostInfo.pszUserName + "鍒涘缓鐨勮璁虹粍");
                this.mListMember.add(hostInfo);
                this.mListMemberExisted.add(hostInfo.strMacAddr);
            }
        } else {
            this.mStrDiscussId = getIntent().getStringExtra("discussID");
            this.mTextTitle.setText(getResources().getString(R.string.discusssetup));
            DiscussInfo discussInfo = ((Protocol) this.mMyApp.ipmsgService.m_DataSource.m_Protocol).mProtocol_Discuss.getDiscussInfo(this.mStrDiscussId);
            if (discussInfo != null) {
                if (!discussInfo.getStrAuthor().equals(hostInfo.strMacAddr)) {
                    this.mBIsManager = false;
                }
                this.mEditName.setEnabled(false);
                this.mEditName.setText(discussInfo.getStrName());
                String[] strArrSplit = discussInfo.getStrsMember().split(Public_MsgID.PRO_SPACE_GROUP);
                for (int i = 0; i < strArrSplit.length; i++) {
                    HostInformation hostInfo2 = this.mMyApp.ipmsgService.getHostInfo(strArrSplit[i]);
                    if (hostInfo2 != null) {
                        this.mListMember.add(hostInfo2);
                        this.mListMemberExisted.add(strArrSplit[i]);
                    }
                }
            } else {
                finish();
            }
        }
        SelectedMemListAdapter selectedMemListAdapter = new SelectedMemListAdapter(this, 0, this.mListMember, this.mListMemberExisted, this.mBIsManager);
        this.mSelectedMemListAdapter = selectedMemListAdapter;
        this.mListViewMember.setAdapter((ListAdapter) selectedMemListAdapter);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
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

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && i2 == -1) {
            for (int i3 = 0; i3 < Global.g_sendUserList.size(); i3++) {
                try {
                    HostInformation hostInfo = this.mMyApp.ipmsgService.getHostInfo(Global.g_sendUserList.get(i3));
                    if (hostInfo != null && !this.mListMember.contains(hostInfo)) {
                        this.mListMember.add(hostInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.mSelectedMemListAdapter.notifyDataSetChanged();
        }
    }
}

