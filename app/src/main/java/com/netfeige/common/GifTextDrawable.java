package com.netfeige.common;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class GifTextDrawable extends AnimationDrawable {
    private TextView tv;
    private int curFrame = -1;
    private boolean Stop = false;

    public GifTextDrawable(TextView textView) {
        this.tv = textView;
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
    }

    @Override // android.graphics.drawable.DrawableContainer
    public boolean selectDrawable(int i) {
        this.curFrame = i;
        return super.selectDrawable(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void scheduleSelf(Runnable runnable, long j) {
        if (this.Stop) {
            return;
        }
        this.tv.postInvalidate();
        this.tv.postDelayed(this, getDuration(this.curFrame));
    }

    @Override // android.graphics.drawable.AnimationDrawable, android.graphics.drawable.Animatable
    public void stop() {
        super.stop();
        this.Stop = true;
    }
}

