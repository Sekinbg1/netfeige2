package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.DBHelper;
import com.netfeige.common.HostInformation;
import com.netfeige.common.LanSharedItem;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SharePassword;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.LanShareActivity;
import com.netfeige.service.IpmsgService;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class CheckLanSharePwdDialog extends Dialog {
    private InputMethodManager imm;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private Context m_context;
    private EditText m_editPwd;
    private LayoutInflater m_inflater;
    HashMap<String, Object> m_mapSubQueryArgs;
    private TextView m_textVTitle;
    private View m_viewParent;

    public CheckLanSharePwdDialog(Context context, HashMap<String, Object> map) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_context = context;
        this.imm = (InputMethodManager) context.getSystemService("input_method");
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        this.m_viewParent = layoutInflater.inflate(R.layout.setmysharepwddialog, (ViewGroup) null);
        this.m_mapSubQueryArgs = map;
        if (((Public_Def.ShareCheckFlag) map.get("flag")) == Public_Def.ShareCheckFlag.SHARE_DOWNLOAD) {
            setCancelable(false);
        }
        setContentView(this.m_viewParent);
        initControl();
        initData();
    }

    public CheckLanSharePwdDialog(Context context, int i) {
        super(context, i);
        this.m_inflater = null;
    }

    public CheckLanSharePwdDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.m_inflater = null;
    }

    private void initControl() {
        this.m_textVTitle = (TextView) this.m_viewParent.findViewById(R.id.title_text_setmysharepwddialog);
        this.m_editPwd = (EditText) this.m_viewParent.findViewById(R.id.pwd_edit_setmysharepwddialog);
        this.m_btnConfirm = (Button) this.m_viewParent.findViewById(R.id.confirm_btn_setmysharepwddialog);
        this.m_btnCancel = (Button) this.m_viewParent.findViewById(R.id.cancel_btn_setmysharepwddialog);
        this.m_btnConfirm.setOnClickListener(new ComfirmClickListener());
        this.m_btnCancel.setOnClickListener(new CancelClickListener());
    }

    private void initData() {
        this.m_textVTitle.setText(((LanSharedItem) this.m_mapSubQueryArgs.get("lanSharedItem")).getM_strFrom() + "的访问密码");
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            CheckLanSharePwdDialog.this.imm.hideSoftInputFromWindow(CheckLanSharePwdDialog.this.getCurrentFocus().getWindowToken(), 0);
            LanSharedItem lanSharedItem = (LanSharedItem) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("lanSharedItem");
            SharePassword sharePassword = (SharePassword) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("sharePwd");
            if (!CheckLanSharePwdDialog.this.m_editPwd.getText().toString().equals(IpmsgApplication.g_mapUserSharePwd.get(lanSharedItem.getM_strFromMac()))) {
                Public_Tools.showToast(CheckLanSharePwdDialog.this.m_context, CheckLanSharePwdDialog.this.m_context.getString(R.string.pwderror), 0);
                return;
            }
            sharePassword.setM_strPassword(CheckLanSharePwdDialog.this.m_editPwd.getText().toString());
            if (sharePassword.getM_iID() == -1) {
                DBHelper.getInstance(CheckLanSharePwdDialog.this.m_context).insertSharePasswordRecord(sharePassword);
            } else {
                DBHelper.getInstance(CheckLanSharePwdDialog.this.m_context).updateSharePasswordRecord(sharePassword);
            }
            if (((Public_Def.ShareCheckFlag) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("flag")) == Public_Def.ShareCheckFlag.SHARE_DOWNLOAD) {
                ((LanShareActivity) CheckLanSharePwdDialog.this.m_context).dlRootFiles((Public_Def.DownloadCmd) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("dlcmd"), null);
            } else if (((Public_Def.ShareCheckFlag) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("flag")) == Public_Def.ShareCheckFlag.SHARE_DOWNLOAD_SINGLE) {
                ArrayList<LanSharedItem> arrayList = new ArrayList<>();
                arrayList.add(lanSharedItem);
                IpmsgService ipmsgService = ((IpmsgApplication) ((LanShareActivity) CheckLanSharePwdDialog.this.m_context).getApplication()).ipmsgService;
                HostInformation hostInfo = ipmsgService.getHostInfo(lanSharedItem.getM_strFromMac());
                if (hostInfo == null) {
                    Public_Tools.showToast(CheckLanSharePwdDialog.this.m_context, CheckLanSharePwdDialog.this.m_context.getString(R.string.offline_prompt), 1);
                } else {
                    ipmsgService.m_DataSource.m_Protocol.sendRootFileDLQuery(hostInfo, Public_Def.DownloadCmd.DOWNLOAD_ONLY, arrayList);
                }
            } else {
                ((LanShareActivity) CheckLanSharePwdDialog.this.m_context).sendSubFileListQuery((String) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("strMac"), lanSharedItem.getM_iID(), (String) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("strDirPath"));
            }
            CheckLanSharePwdDialog.this.dismiss();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            CheckLanSharePwdDialog.this.imm.hideSoftInputFromWindow(CheckLanSharePwdDialog.this.getCurrentFocus().getWindowToken(), 0);
            if (((Public_Def.ShareCheckFlag) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("flag")) == Public_Def.ShareCheckFlag.SHARE_DOWNLOAD) {
                ((LanShareActivity) CheckLanSharePwdDialog.this.m_context).dlRootFiles((Public_Def.DownloadCmd) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("dlcmd"), (String) CheckLanSharePwdDialog.this.m_mapSubQueryArgs.get("strMac"));
            }
            CheckLanSharePwdDialog.this.dismiss();
        }
    }
}

