package com.netfeige.display.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;

/* JADX INFO: loaded from: classes.dex */
public class HeadImageActivity extends Activity {
    private GridView m_gridVHeadImage;
    private IDataConfig m_iDataConfig;
    private IpmsgApplication m_ipmsgApp;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.headimage);
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        this.m_iDataConfig = DataConfig.getInstance(this);
        this.m_gridVHeadImage = (GridView) findViewById(R.id.gridView_headerimage);
        this.m_gridVHeadImage.setAdapter((ListAdapter) new SimpleAdapter(this, this.m_ipmsgApp.g_headImageList, R.layout.headimageitem, new String[]{"headImage"}, new int[]{R.id.head_imageview_item}));
        this.m_gridVHeadImage.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.HeadImageActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                try {
                    String str = (String) HeadImageActivity.this.m_ipmsgApp.g_headImageList.get(i).get("headCode");
                    if (!HeadImageActivity.this.m_iDataConfig.Read(36).equals(str)) {
                        HeadImageActivity.this.m_ipmsgApp.ipmsgService.getHostInfo(Public_Tools.getLocalHostInfo().strMacAddr).headImage = str;
                        HeadImageActivity.this.m_iDataConfig.Write(36, str);
                        Global.g_bChangedHead = true;
                    }
                    HeadImageActivity.this.onBackPressed();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
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

