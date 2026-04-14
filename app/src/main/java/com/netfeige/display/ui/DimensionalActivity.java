package com.netfeige.display.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.netfeige.R;

/* JADX INFO: loaded from: classes.dex */
public class DimensionalActivity extends Activity {
    private Button m_btnBack;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.two_dimensional);
        Button button = (Button) findViewById(R.id.back_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.DimensionalActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DimensionalActivity.this.onBackPressed();
            }
        });
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
}

