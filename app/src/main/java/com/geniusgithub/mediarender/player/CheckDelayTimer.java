package com.geniusgithub.mediarender.player;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class CheckDelayTimer extends AbstractTimer {
    private int lastPos;

    public CheckDelayTimer(Context context) {
        super(context);
        this.lastPos = 0;
    }

    public void setPos(int i) {
        this.lastPos = i;
    }

    public boolean isDelay(int i) {
        return i != 0 && i == this.lastPos;
    }
}

