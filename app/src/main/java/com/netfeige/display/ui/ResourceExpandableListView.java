package com.netfeige.display.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

/* JADX INFO: loaded from: classes.dex */
public class ResourceExpandableListView extends ExpandableListView {
    public ResourceExpandableListView(Context context) {
        super(context);
    }

    public ResourceExpandableListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ResourceExpandableListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
    }
}

