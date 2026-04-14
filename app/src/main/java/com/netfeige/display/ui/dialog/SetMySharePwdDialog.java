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
import com.netfeige.R;
import com.netfeige.common.DBHelper;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SharePassword;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SetMySharePwdDialog extends Dialog {
    private InputMethodManager imm;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private Context m_context;
    private EditText m_editPwd;
    private LayoutInflater m_inflater;
    private SharePassword m_sharePwd;
    private View m_viewParent;

    public SetMySharePwdDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_sharePwd = null;
        this.m_context = context;
        this.imm = (InputMethodManager) context.getSystemService("input_method");
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.setmysharepwddialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        initControl();
        initData();
    }

    public SetMySharePwdDialog(Context context, int i) {
        super(context, i);
        this.m_inflater = null;
        this.m_sharePwd = null;
    }

    public SetMySharePwdDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.m_inflater = null;
        this.m_sharePwd = null;
    }

    private void initControl() {
        this.m_editPwd = (EditText) this.m_viewParent.findViewById(R.id.pwd_edit_setmysharepwddialog);
        this.m_btnConfirm = (Button) this.m_viewParent.findViewById(R.id.confirm_btn_setmysharepwddialog);
        this.m_btnCancel = (Button) this.m_viewParent.findViewById(R.id.cancel_btn_setmysharepwddialog);
        this.m_btnConfirm.setOnClickListener(new ComfirmClickListener());
        this.m_btnCancel.setOnClickListener(new CancelClickListener());
    }

    private void initData() {
        String str = "sMac = '" + Public_Tools.getLocalMacAddress() + "'";
        new ArrayList();
        ArrayList<SharePassword> sharePasswordRecord = DBHelper.getInstance(this.m_context).getSharePasswordRecord(str);
        if (sharePasswordRecord.isEmpty()) {
            this.m_sharePwd = new SharePassword(-1, Public_Tools.getLocalMacAddress(), null);
            return;
        }
        SharePassword sharePassword = sharePasswordRecord.get(0);
        this.m_sharePwd = sharePassword;
        this.m_editPwd.setText(sharePassword.getM_strPassword());
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SetMySharePwdDialog.this.imm.hideSoftInputFromWindow(SetMySharePwdDialog.this.getCurrentFocus().getWindowToken(), 0);
            SetMySharePwdDialog.this.m_sharePwd.setM_strPassword(SetMySharePwdDialog.this.m_editPwd.getText().toString());
            if (SetMySharePwdDialog.this.m_sharePwd.getM_iID() == -1) {
                DBHelper.getInstance(SetMySharePwdDialog.this.m_context).insertSharePasswordRecord(SetMySharePwdDialog.this.m_sharePwd);
            } else {
                DBHelper.getInstance(SetMySharePwdDialog.this.m_context).updateSharePasswordRecord(SetMySharePwdDialog.this.m_sharePwd);
            }
            SetMySharePwdDialog.this.dismiss();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SetMySharePwdDialog.this.imm.hideSoftInputFromWindow(SetMySharePwdDialog.this.getCurrentFocus().getWindowToken(), 0);
            SetMySharePwdDialog.this.dismiss();
        }
    }
}

