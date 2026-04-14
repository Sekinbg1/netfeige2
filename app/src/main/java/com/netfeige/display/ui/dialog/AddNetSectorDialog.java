package com.netfeige.display.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.NetSectorActivity;
import java.util.ArrayList;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class AddNetSectorDialog extends Dialog {
    private InputMethodManager imm;
    private Button m_btnCancel;
    private Button m_btnConfirm;
    private Context m_context;
    private EditText m_editNetSectorName;
    private LayoutInflater m_inflater;
    private View m_viewParent;

    public AddNetSectorDialog(Context context) {
        super(context, R.style.sort_dialog);
        this.m_inflater = null;
        this.m_context = context;
        this.imm = (InputMethodManager) context.getSystemService("input_method");
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.m_inflater = layoutInflater;
        View viewInflate = layoutInflater.inflate(R.layout.addnetsectordialog, (ViewGroup) null);
        this.m_viewParent = viewInflate;
        setContentView(viewInflate);
        setCancelable(false);
        initControl();
    }

    private void initControl() {
        this.m_editNetSectorName = (EditText) this.m_viewParent.findViewById(R.id.name_edit_addnetsectordialog);
        Button button = (Button) this.m_viewParent.findViewById(R.id.confirm_btn_addnetsectordialog);
        this.m_btnConfirm = button;
        button.setOnClickListener(new ComfirmClickListener());
        Button button2 = (Button) this.m_viewParent.findViewById(R.id.cancel_btn_addnetsectordialog);
        this.m_btnCancel = button2;
        button2.setOnClickListener(new CancelClickListener());
    }

    private class ComfirmClickListener implements View.OnClickListener {
        private ComfirmClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            String string = AddNetSectorDialog.this.m_editNetSectorName.getText().toString();
            if (!AddNetSectorDialog.this.isSector(string)) {
                Public_Tools.showToast(AddNetSectorDialog.this.m_context, AddNetSectorDialog.this.m_context.getString(R.string.formaterror), 1);
                return;
            }
            NetSectorActivity netSectorActivity = (NetSectorActivity) AddNetSectorDialog.this.m_context;
            ArrayList<String> arrayList = ((IpmsgApplication) netSectorActivity.getApplication()).g_listNetSectors;
            if (AddNetSectorDialog.this.isExist(arrayList, string)) {
                Public_Tools.showToast(AddNetSectorDialog.this.m_context, AddNetSectorDialog.this.m_context.getString(R.string.sector_existed), 1);
                return;
            }
            arrayList.add(string);
            netSectorActivity.notifyDataSetChanged();
            AddNetSectorDialog.this.imm.hideSoftInputFromWindow(AddNetSectorDialog.this.getCurrentFocus().getWindowToken(), 0);
            AddNetSectorDialog.this.dismiss();
        }
    }

    private class CancelClickListener implements View.OnClickListener {
        private CancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AddNetSectorDialog.this.imm.hideSoftInputFromWindow(AddNetSectorDialog.this.getCurrentFocus().getWindowToken(), 0);
            AddNetSectorDialog.this.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSector(String str) {
        return Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$").matcher(str).matches();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isExist(ArrayList<String> arrayList, String str) {
        String defaultLocalHostIP = Public_Tools.getDefaultLocalHostIP();
        if (defaultLocalHostIP.substring(0, defaultLocalHostIP.lastIndexOf(".")).equals(str)) {
            return true;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(str)) {
                return true;
            }
        }
        return false;
    }
}

