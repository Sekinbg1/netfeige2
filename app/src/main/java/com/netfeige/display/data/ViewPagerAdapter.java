package com.netfeige.display.data;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.display.ui.IpmsgActivity;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ViewPagerAdapter extends PagerAdapter {
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    private Activity m_activity;
    private List<View> m_listViews;
    private int m_nActivityType;

    @Override // android.support.v4.view.PagerAdapter
    public void finishUpdate(View view) {
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
    }

    @Override // android.support.v4.view.PagerAdapter
    public Parcelable saveState() {
        return null;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void startUpdate(View view) {
    }

    public ViewPagerAdapter(List<View> list, Activity activity, int i) {
        this.m_listViews = list;
        this.m_activity = activity;
        this.m_nActivityType = i;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(View view, int i, Object obj) {
        ((ViewPager) view).removeView(this.m_listViews.get(i));
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        List<View> list = this.m_listViews;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override // android.support.v4.view.PagerAdapter
    public Object instantiateItem(View view, int i) {
        ((ViewPager) view).addView(this.m_listViews.get(i), 0);
        if (i == this.m_listViews.size() - 1) {
            TextView textView = (TextView) view.findViewById(R.id.iv_start_feige);
            if (this.m_nActivityType == 0) {
                textView.setText("使用飞鸽");
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.ViewPagerAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        ViewPagerAdapter.this.setGuided();
                        ViewPagerAdapter.this.goHome();
                    }
                });
            } else {
                textView.setText("返回飞鸽");
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.ViewPagerAdapter.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        ViewPagerAdapter.this.m_activity.onBackPressed();
                    }
                });
            }
        }
        return this.m_listViews.get(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goHome() {
        this.m_activity.startActivity(new Intent(this.m_activity, (Class<?>) IpmsgActivity.class));
        this.m_activity.finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGuided() {
        SharedPreferences.Editor editorEdit = this.m_activity.getSharedPreferences(SHAREDPREFERENCES_NAME, 0).edit();
        editorEdit.putBoolean("isFirstIn", false);
        editorEdit.commit();
    }
}

