package com.netfeige.display.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class IpmsgTextView extends TextView {
    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    public IpmsgTextView(Context context) {
        super(context);
    }

    public IpmsgTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public IpmsgTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}

