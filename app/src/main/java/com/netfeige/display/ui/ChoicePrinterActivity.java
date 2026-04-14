package com.netfeige.display.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.data.MsgRecord;
import com.netfeige.display.data.PrinterAdapter;
import com.netfeige.service.IpmsgService;

/* JADX INFO: loaded from: classes.dex */
public class ChoicePrinterActivity extends Activity implements IpmsgService.EventHandler {
    public static PrinterAdapter m_printerAdapter;
    private Button m_btnBack;
    private IpmsgApplication m_ipmsgApp;
    private ListView m_listVPrinters;
    private TextView m_textVShowNotify;

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddMessage(String str, MsgRecord msgRecord) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileList(String str, boolean z) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyFileMessage(String str, MsgRecord msgRecord) {
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onModifyMessage(HostInformation hostInformation, MsgRecord msgRecord) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.printershow);
        init();
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    private void init() {
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        this.m_textVShowNotify = (TextView) findViewById(R.id.textv_showcontect);
        Button button = (Button) findViewById(R.id.back_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ChoicePrinterActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ChoicePrinterActivity.this.onBackPressed();
            }
        });
        this.m_listVPrinters = (ListView) findViewById(R.id.listView_printer);
        PrinterAdapter printerAdapter = new PrinterAdapter(this);
        m_printerAdapter = printerAdapter;
        this.m_listVPrinters.setAdapter((ListAdapter) printerAdapter);
        this.m_ipmsgApp.ipmsgService.ehList.add(this);
        if (IpmsgService.printerList.size() > 0) {
            this.m_listVPrinters.setVisibility(0);
            this.m_textVShowNotify.setVisibility(8);
        } else {
            this.m_listVPrinters.setVisibility(8);
            this.m_textVShowNotify.setVisibility(0);
        }
    }

    public void back() {
        onBackPressed();
    }

    @Override // com.netfeige.service.IpmsgService.EventHandler
    public void onAddOrModifyHostInfo(HostInformation hostInformation, Global.UserHandleType userHandleType) {
        if (IpmsgService.printerList.size() > 0) {
            if (!this.m_listVPrinters.isShown()) {
                this.m_listVPrinters.setVisibility(0);
            }
            if (this.m_textVShowNotify.isShown()) {
                this.m_textVShowNotify.setVisibility(8);
                return;
            }
            return;
        }
        if (this.m_listVPrinters.isShown()) {
            this.m_listVPrinters.setVisibility(8);
        }
        if (this.m_textVShowNotify.isShown()) {
            return;
        }
        this.m_textVShowNotify.setVisibility(0);
    }
}

