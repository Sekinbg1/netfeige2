package com.netfeige.display.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.netfeige.R;
import com.netfeige.display.data.ViewPagerAdapter;
import com.netfeige.kits.DataConfig;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    private ImageView[] m_imageVDots;
    private List<View> m_listViews;
    private int m_nActivityType;
    private int m_nCurrentIndex;
    private ViewPager m_viewPager;
    private ViewPagerAdapter m_viewPagerAdapter;

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.guide);
        this.m_nActivityType = getIntent().getExtras().getInt("activityType");
        setRandomHeaderImage();
        initViews();
        initDots();
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

    private void setRandomHeaderImage() {
        try {
            DataConfig.getInstance(this).Write(36, String.valueOf((int) (Math.random() * 40.0d)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        ArrayList arrayList = new ArrayList();
        this.m_listViews = arrayList;
        arrayList.add(layoutInflaterFrom.inflate(R.layout.what_new_one, (ViewGroup) null));
        this.m_listViews.add(layoutInflaterFrom.inflate(R.layout.what_new_two, (ViewGroup) null));
        this.m_listViews.add(layoutInflaterFrom.inflate(R.layout.what_new_three, (ViewGroup) null));
        this.m_listViews.add(layoutInflaterFrom.inflate(R.layout.what_new_four, (ViewGroup) null));
        this.m_viewPagerAdapter = new ViewPagerAdapter(this.m_listViews, this, this.m_nActivityType);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.m_viewPager = viewPager;
        viewPager.setAdapter(this.m_viewPagerAdapter);
        this.m_viewPager.setOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        this.m_imageVDots = new ImageView[this.m_listViews.size()];
        for (int i = 0; i < this.m_listViews.size(); i++) {
            this.m_imageVDots[i] = (ImageView) linearLayout.getChildAt(i);
            this.m_imageVDots[i].setEnabled(true);
        }
        this.m_nCurrentIndex = 0;
        this.m_imageVDots[0].setEnabled(false);
    }

    private void setCurrentDot(int i) {
        if (i < 0 || i > this.m_listViews.size() - 1 || this.m_nCurrentIndex == i) {
            return;
        }
        this.m_imageVDots[i].setEnabled(false);
        this.m_imageVDots[this.m_nCurrentIndex].setEnabled(true);
        this.m_nCurrentIndex = i;
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        setCurrentDot(i);
    }
}

