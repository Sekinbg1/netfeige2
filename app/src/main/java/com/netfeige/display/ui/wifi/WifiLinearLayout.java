package com.netfeige.display.ui.wifi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.netfeige.R;

/* JADX INFO: loaded from: classes.dex */
public class WifiLinearLayout extends RelativeLayout {
    private Button m_btnBack;
    private Button m_btnCreateWiFi;
    private Button m_btnSearchWiFi;

    public WifiLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.basicwifipage, (ViewGroup) this, true);
        initControl();
    }

    private void initControl() {
        this.m_btnBack = (Button) findViewById(R.id.back_btn_wifi);
        this.m_btnCreateWiFi = (Button) findViewById(R.id.btn_creatwifi);
        this.m_btnSearchWiFi = (Button) findViewById(R.id.btn_searchwifi);
    }

    public void setCreateWiFiBackgroundResource(int i) {
        this.m_btnCreateWiFi.setBackgroundResource(i);
    }

    public void setCreateWiFiText(int i) {
        this.m_btnCreateWiFi.setText(i);
    }

    public void setCreateWiFiEnabled(boolean z) {
        this.m_btnCreateWiFi.setEnabled(z);
    }

    public void setSearchWiFiText(int i) {
        this.m_btnSearchWiFi.setText(i);
    }

    public Button getCreateWiFi() {
        return this.m_btnCreateWiFi;
    }

    public Button getSearchWiFi() {
        return this.m_btnSearchWiFi;
    }

    public Button getBtnBack() {
        return this.m_btnBack;
    }
}

