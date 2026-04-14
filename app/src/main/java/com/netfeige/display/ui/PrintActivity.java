package com.netfeige.display.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.dlna.ContentTree;
import com.netfeige.protocol.Protocol;
import com.netfeige.service.IpmsgService;

/* JADX INFO: loaded from: classes.dex */
public class PrintActivity extends Activity {
    public static TextView s_textVChoicedPrinter;
    private Button m_btnAddition;
    private Button m_btnBack;
    private Button m_btnPrint;
    private Button m_btnSubtraction;
    private Context m_context;
    private LinearLayout m_linearLChoicePrinter;
    private String m_strFilePath;
    private TextView m_textVNumber;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.printer);
        init();
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onStart() {
        if (Global.g_hostInformation == null) {
            s_textVChoicedPrinter.setText(R.string.choiceprinter);
        } else {
            s_textVChoicedPrinter.setText(Global.g_hostInformation.pszUserName);
        }
        super.onStart();
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
        this.m_context = this;
        if (IpmsgService.printerList.size() > 0 && Global.g_hostInformation == null) {
            IpmsgService.printerList.get(0).isChecked = true;
            Global.g_hostInformation = IpmsgService.printerList.get(0);
        }
        this.m_strFilePath = getIntent().getExtras().getString("filePath");
        initControl();
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.back_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new BackBtnOnListener());
        Button button2 = (Button) findViewById(R.id.print_btn);
        this.m_btnPrint = button2;
        button2.setOnClickListener(new PrintBtnOnListener());
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlchoiceprinter);
        this.m_linearLChoicePrinter = linearLayout;
        linearLayout.setOnClickListener(new LinearLChoicePrinterListener());
        s_textVChoicedPrinter = (TextView) findViewById(R.id.choicedPrinter);
        this.m_textVNumber = (TextView) findViewById(R.id.printNumber);
        Button button3 = (Button) findViewById(R.id.printjia);
        this.m_btnAddition = button3;
        button3.setOnClickListener(new AdditionBtnOnListener());
        Button button4 = (Button) findViewById(R.id.printjian);
        this.m_btnSubtraction = button4;
        button4.setOnClickListener(new SubtractionBtnOnListener());
    }

    private class BackBtnOnListener implements View.OnClickListener {
        private BackBtnOnListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PrintActivity.this.onBackPressed();
        }
    }

    private class PrintBtnOnListener implements View.OnClickListener {
        private PrintBtnOnListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (Global.g_hostInformation == null) {
                Toast.makeText(PrintActivity.this.m_context, R.string.notifychoiceprinter, 0).show();
                return;
            }
            Protocol.getInstance().printQuery(PrintActivity.this.m_strFilePath, Global.g_hostInformation);
            for (int i = 0; i < IpmsgActivity.s_fileListView.getFileAdapter().getFileList().size(); i++) {
                IpmsgActivity.s_fileListView.getFileAdapter().getFileList().get(i).setCheck(false);
            }
            IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
            Global.g_filePath.clear();
            IpmsgActivity.m_popupWindowSend.dismiss();
            Toast.makeText(PrintActivity.this.m_context, R.string.notifyprintsended, 0).show();
        }
    }

    private class AdditionBtnOnListener implements View.OnClickListener {
        private AdditionBtnOnListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                int iIntValue = Integer.valueOf(PrintActivity.this.m_textVNumber.getText().toString()).intValue() + 1;
                PrintActivity.this.m_textVNumber.setText(iIntValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SubtractionBtnOnListener implements View.OnClickListener {
        private SubtractionBtnOnListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                int iIntValue = Integer.valueOf(PrintActivity.this.m_textVNumber.getText().toString()).intValue() - 1;
                if (iIntValue > 0) {
                    PrintActivity.this.m_textVNumber.setText(iIntValue + "");
                } else {
                    PrintActivity.this.m_textVNumber.setText(ContentTree.VIDEO_ID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class LinearLChoicePrinterListener implements View.OnClickListener {
        private LinearLChoicePrinterListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(PrintActivity.this.m_context, ChoicePrinterActivity.class);
            PrintActivity.this.m_context.startActivity(intent);
        }
    }
}

